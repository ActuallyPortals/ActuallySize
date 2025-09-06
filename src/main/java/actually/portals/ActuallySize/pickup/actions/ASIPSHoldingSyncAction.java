package actually.portals.ActuallySize.pickup.actions;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.netcode.ASINetworkManager;
import actually.portals.ActuallySize.netcode.packets.clientbound.ASINCHoldPointsConfigurationBroadcast;
import actually.portals.ActuallySize.netcode.packets.clientbound.ASINCHoldPointsSyncReply;
import actually.portals.ActuallySize.netcode.packets.clientbound.ASINCItemEntityActivationPacket;
import actually.portals.ActuallySize.netcode.packets.serverbound.ASINSHoldingSyncRequest;
import actually.portals.ActuallySize.pickup.ASIPickupSystemManager;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSRegisterableHoldPoint;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.HoldPointConfigurable;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemDualityCounterpart;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemExplorerStatement;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.APIFriendlyProcess;
import gunging.ootilities.GungingOotilitiesMod.scheduling.SchedulingManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * A shorthand for syncing all information on dualities to a player,
 * called when they first join the server, teleport far away, or
 * change dimensions:
 * <br> [0] Sends information on network indices (optional)
 * <br> (1) Sends information on hold points for world entities
 * <br> (2) Sends information on active dualities
 * <br><br>
 * Alternatively, if called from the client side, it requests this
 * sync action to occur on the server.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIPSHoldingSyncAction implements APIFriendlyProcess {

    /**
     * If syncing initial connection stuff too
     *
     * @since 1.0.0
     */
    boolean networkIndexSync;

    /**
     * Allows to sync initial server network stuff too
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public void withNetworkIndices() { networkIndexSync = true; }

    /**
     * If syncing nearby configurable-hold-point entities
     *
     * @since 1.0.0
     */
    boolean configurablesSync;

    /**
     * Allows to sync nearby configurable-hold-point entities
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public void withConfigurables() { configurablesSync = true; }

    /**
     * Allows to send this requestor's configured slots to other nearby players
     *
     * @since 1.0.0
     */
    @Nullable HashMap<ItemExplorerStatement<?,?>, ASIPSRegisterableHoldPoint> broadcastConfigurables;

    /**
     * Allows to send this requestor's configured slots to other nearby players
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public void withBroadcast(@Nullable HashMap<ItemExplorerStatement<?,?>, ASIPSRegisterableHoldPoint> bc) { broadcastConfigurables = bc; }

    /**
     * If syncing nearby active item-entity dualities
     *
     * @since 1.0.0
     */
    boolean dualitiesSync;

    /**
     * Allows to sync nearby active item-entity dualities
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public void withActiveDualities() { dualitiesSync = true; }

    /**
     * The player that requested the sync action, to whom we are syncing
     *
     * @since 1.0.0
     */
    @NotNull Player requestor;

    /**
     * @param who The player that requested the sync action, to whom we are syncing
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSHoldingSyncAction(@NotNull Player who) { requestor = who; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override public boolean isVerified() { return true; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override public boolean isAllowed() {

        // Has this person requested a sync too recently? Only every few ticks please
        Long previousTick = requestorFreq.get(requestor.getUUID());
        if (previousTick != null) { return SchedulingManager.getServerTicks() - previousTick > 10; }

        // Nothing known, accept. This is not cancellable.
        return true;
    }

    /**
     * An array to buffer requests
     *
     * @since 1.0.0
     */
    @NotNull static final HashMap<UUID, Long> requestorFreq = new HashMap<>();

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override public void resolve() {

        // Local client? Send packet to server
        if (!(requestor instanceof ServerPlayer)) {

            // Send packet request to server
            ASINSHoldingSyncRequest packet = new ASINSHoldingSyncRequest(networkIndexSync, configurablesSync, dualitiesSync);
            ASINetworkManager.playerToServer(packet);
            return;
        }

        // Cast for ease of use
        ServerPlayer target = (ServerPlayer) requestor;
        ServerLevel world = (ServerLevel) target.level();

        /*
         * [0] Initial network sync indices
         */
        if (networkIndexSync) {

            // Iterate all namespaces
            for (String namespace : ASIPickupSystemManager.HOLD_POINT_REGISTRY.listStatementNamespaces()) {

                // To Sync
                ArrayList<ASIPSRegisterableHoldPoint> toSync = ASIPickupSystemManager.HOLD_POINT_REGISTRY.listHoldPoints(namespace);
                if (toSync.isEmpty()) { continue; }

                // Send information
                ASINCHoldPointsSyncReply packet = new ASINCHoldPointsSyncReply(namespace, toSync);
                ASINetworkManager.serverToPlayer((ServerPlayer) requestor, packet);
            }
        }

        /*
         * (1) Rate limit requests
         */
        requestorFreq.put(requestor.getUUID(), SchedulingManager.getServerTicks());

        /*
         * (2) Configure local entity hold points
         */
        if (configurablesSync || broadcastConfigurables != null) {

            // Iterate entities within like 32 chunks, should be enough
            List<ServerPlayer> nearbyPlayers = world.getPlayers((p) -> {

                // Must be a different player and within 32 chunks
                return !p.equals(target) && p.position().distanceToSqr(target.position()) < 262145;
            });
            //HDA//ActuallySizeInteractions.Log("ASI OWL &6 Broadcasting player configs x" + nearbyPlayers.size());

            ASINCHoldPointsConfigurationBroadcast bcp = null;
            if (broadcastConfigurables != null) {
                bcp = new ASINCHoldPointsConfigurationBroadcast(broadcastConfigurables, target);
                ASINetworkManager.serverToPlayer(target, bcp);
            }

            // Broadcast their configuration
            for (ServerPlayer nearby : nearbyPlayers) {
                //HDA//ActuallySizeInteractions.Log("ASI OWL &6 Synced to &e " + nearby.getScoreboardName());

                // Request their configuration
                if (configurablesSync) {

                    // Cast
                    HoldPointConfigurable asConfigurable = (HoldPointConfigurable) nearby;

                    // Send information on this' entities slots
                    ASINCHoldPointsConfigurationBroadcast broadcast =
                            new ASINCHoldPointsConfigurationBroadcast(
                                    asConfigurable.actuallysize$getLocalHoldPoints().getRegisteredPoints(),
                                    nearby);

                    // Send configuration
                    ASINetworkManager.serverToPlayer(target, broadcast);
                }

                // Broadcast yours
                if (bcp != null) { ASINetworkManager.serverToPlayer(nearby, bcp); }
            }
        }

        if (dualitiesSync) {

            /*
             * (3) Send information on active dualities
             */
            List<Entity> foundEntities = world.getEntities(target, target.getBoundingBox().inflate(512, 512, 512));
            //HDA//ActuallySizeInteractions.Log("ASI OWL &6 Broadcasting dualities sync for x" + foundEntities.size());
            for (Entity nearby : foundEntities) {

                // We are only considering active dualities
                EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) nearby;
                if (!dualityEntity.actuallysize$isActive()) { continue; }
                ItemDualityCounterpart dualityItem = (ItemDualityCounterpart) (Object) dualityEntity.actuallysize$getItemCounterpart();
                if (dualityItem == null) { continue; }
                ItemStackLocation<? extends Entity> stackLocation = dualityItem.actuallysize$getItemStackLocation();
                //HDA//ActuallySizeInteractions.Log("ASI OWL &6 + &7 Broadcasting dualities data on &e " + nearby.getScoreboardName());

                // Send information
                /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "EDS", "&f Broadcasting activation packet for {0}", stackLocation);
                ASINCItemEntityActivationPacket packet = new ASINCItemEntityActivationPacket(stackLocation, nearby);
                ASINetworkManager.serverToPlayer(target, packet);
            }
        }
    }
}
