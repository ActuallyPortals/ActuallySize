package actually.portals.ActuallySize.netcode;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.netcode.packets.ASINetworkDelayableAction;
import actually.portals.ActuallySize.netcode.packets.clientbound.*;
import actually.portals.ActuallySize.netcode.packets.serverbound.ASINSHoldPointsConfigurationSync;
import actually.portals.ActuallySize.pickup.ASIPickupSystemManager;
import actually.portals.ActuallySize.pickup.actions.*;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSHoldPointRegistry;
import actually.portals.ActuallySize.pickup.mixininterfaces.HoldPointConfigurable;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.APIFriendlyProcess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * The class that handles the Clientbound packets sent from the server
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIClientsidePacketHandler {

    /**
     * @param packet The packet that arrived from the network with certain player's hold slots
     * @param contextSupplier The context in which this is taking place
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void handleRemotePlayerHoldConfiguration(@NotNull ASINCHoldPointsConfigurationBroadcast packet, @NotNull Supplier<NetworkEvent.Context> contextSupplier) {

        // Kinda need a reference to the server
        LocalPlayer local = Minecraft.getInstance().player;
        if (local == null) { return; }
        ActuallySizeInteractions.Log("ASI &dPS REG &7 Received Hold points configuration for &3 #" + packet.getPlayerIndex() + " &b (local #" + local.getId() + ")");

        // Look for this player
        Entity byID = local.level().getEntity(packet.getPlayerIndex());
        if (!(byID instanceof Player)) { return; }

        // Accept registry
        HoldPointConfigurable config = (HoldPointConfigurable) byID;
        config.actuallysize$setLocalHoldPoints(packet.produceRegistry());

        ActuallySizeInteractions.Log("ASI &dPS REG&7 Accepted for REMOTE" + (local.getId() == packet.getPlayerIndex() ? " (except it is actually LOCAL)" : "") + ": ");
        config.actuallysize$getLocalHoldPoints().log();
    }

    /**
     * @param syncing Information to sync statement network indices
     * @param contextSupplier The network context by which this is taking place
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void handleStatementSync(@NotNull ASINCHoldPointsSyncReply syncing, @NotNull Supplier<NetworkEvent.Context> contextSupplier) {

        // Receive the most up-to-date indices
        ASIPickupSystemManager.receiveNetworkSync(syncing);

        // Rebuild the local player config slots
        LocalPlayer local = Minecraft.getInstance().player;
        if (local == null) { return; }

        // Rebuild my preferred hold points
        HoldPointConfigurable asConfigurable = ((HoldPointConfigurable) local);
        ASIPSHoldPointRegistry asRegistry = ASIPickupSystemManager.buildLocalPlayerPreferredHoldPoints();
        asConfigurable.actuallysize$setLocalHoldPoints(asRegistry);

        ActuallySizeInteractions.Log("ASI &dPS REG&7 Accepted for LOCAL: ");
        asRegistry.log();

        // Sync to the server
        ASINSHoldPointsConfigurationSync sync = new ASINSHoldPointsConfigurationSync(asRegistry.getRegisteredPoints());
        ASINetworkManager.playerToServer(sync);
    }

    /**
     * @return The world where this network context is taking place.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable public static Level getDualityActionWorld(@NotNull Supplier<NetworkEvent.Context> contextSupplier) {

        // Find world... the best we've got is the minecraft local players' really... I think?
        Level world = Minecraft.getInstance().level;
        if (world == null) {
            if (Minecraft.getInstance().player == null) {
                /*HDA*/ActuallySizeInteractions.Log("ASI &6CPH-HDE &c UNKNOWN WORLD");
                return null; }
            world = Minecraft.getInstance().player.level(); }
        /*HDA*/ActuallySizeInteractions.Log("ASI &6CPH &r Received Packet World " + world.isClientSide);

        return world;
    }

    /**
     * @param action The Item-Entity duality action that arrived from the network
     * @param contextSupplier The network context by which this is taking place
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void handleItemEntityAction(@NotNull ASIPSDualityAction action, @NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        /*HDA*/ActuallySizeInteractions.Log("ASI &6CPH-HDF &r Evaluating action packet " + action.getClass().getSimpleName());

        // Attempt to handle the entity activation
        if (!action.isVerified()) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &6CPH &c NETWORK SERIALIZATION ERROR for " + action.getClass().getSimpleName());
            return; }

        // Resolve
        if (!action.tryResolve()) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &6CPH &3 Could not resolve " + action.getClass().getSimpleName() + ", DELAYING. ");

            // Find the enqueued actions
            UUID player = action.getStackLocation().getHolder().getUUID();
            ArrayList<APIFriendlyProcess> perPlayer = enqueuedDualityActivations.get(player);
            if (perPlayer == null) { perPlayer = new ArrayList<>(); }

            // Add this one to the list
            perPlayer.add(action);
            enqueuedDualityActivations.put(player, perPlayer); }
    }

    /**
     * @param packet The combined Item-Entity duality actions that arrived from the network
     * @param contextSupplier The network context by which this is taking place
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void handleItemEntityFlux(@NotNull ASINCItemEntityFluxPacket packet, @NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        /*HDA*/ActuallySizeInteractions.Log("ASI &6CPH-HDF &r Evaluating flux packet ");

        // Attempt to handle the entity activation
        ASIPSDualityFluxAction action = new ASIPSDualityFluxAction(packet, getDualityActionWorld(contextSupplier));
        if (!action.isVerified()) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &6CPH-HDF &c NETWORK SERIALIZATION ERROR for ASIPSDualityFluxAction. ");
            return; }

        if (!action.tryResolve()) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &6CPH-HDF &3 Could not resolve ASIPSDualityFluxAction, DELAYING. ");

            // Find the enqueued actions
            UUID player = packet.getTo().getStackLocation().getHolder().getUUID();
            ArrayList<APIFriendlyProcess> perPlayer = enqueuedDualityActivations.get(player);
            if (perPlayer == null) { perPlayer = new ArrayList<>(); }

            // Add this one to the list
            perPlayer.add(action);
            enqueuedDualityActivations.put(player, perPlayer);
        }
    }

    /**
     * Sometimes the Duality Activation packet arrives before the
     * inventory slot update packet, which results in the wrong item
     * being registered to the Duality interaction.
     * <p>
     * This will enqueue the activations to wait until the inventory
     * updates arrive, and thus successfully activate the packets.
     *
     * @since 1.0.0
     */
    static @NotNull HashMap<UUID, ArrayList<APIFriendlyProcess>> enqueuedDualityActivations = new HashMap<>();

    /**
     * A class that saves duality activation packets until the entity spawn
     * packet arrives, inventory updates settle, or after the holder themselves
     * has spawned if these are sent during world loading lol.
     *
     * @see #enqueuedDualityActivations
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void tryResolveAllEnqueued() {
        if (enqueuedDualityActivations.isEmpty()) { return; }
        /*HDA*/ActuallySizeInteractions.Log("ASI &6CPH-HDA &3 Retrying UUIDs &b x" + enqueuedDualityActivations.size());

        // Make a copy to avoid editing something while iterating it
        ArrayList<UUID> enqueued = new ArrayList<>(enqueuedDualityActivations.keySet());

        // Enqueue
        for (UUID uuid : enqueued) { resolveEnqueuedDualityActivationsFor(uuid); }
    }

    /**
     * @param who Whose enqueued actions to retry
     *
     * @see #enqueuedDualityActivations
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void resolveEnqueuedDualityActivationsFor(@NotNull UUID who) {
        ArrayList<APIFriendlyProcess> perPlayer = enqueuedDualityActivations.get(who);
        if (perPlayer == null) { return; }
        if (perPlayer.isEmpty()) { return; }

        // Store remainder
        ArrayList<APIFriendlyProcess> kept = new ArrayList<>();

        /*HDA*/ActuallySizeInteractions.Log("ASI &6CPH-HDA &3 Processing delayed packets &b x" + perPlayer.size() + " for " + who);
        // Retry resolving them
        for (APIFriendlyProcess action : perPlayer) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &6CPH-HDA &3 + &b x" + action.getClass().getSimpleName());
            
            // Try to resolve, postpone even further if that fails
            if (!action.tryResolve()) {
                
                // Timeout sequences
                if (action instanceof ASINetworkDelayableAction) {
                    
                    // Increase attempts by one
                    ((ASINetworkDelayableAction) action).logAttempt();
                    
                    // When too many just forget about it
                    if (((ASINetworkDelayableAction) action).getAttempts() > 15) { continue; }
                }
                
                // Okay, continue as long as it remains verified
                if (action.isVerified()) { kept.add(action); }
            }
        }
        /*HDA*/ActuallySizeInteractions.Log("ASI &6CPH-HDA &3 Remainder &b x" + kept.size());

        // Clear key when done
        if (kept.isEmpty()) {
            enqueuedDualityActivations.remove(who);

        // Remember those that are yet to be resolved
        } else {
            enqueuedDualityActivations.put(who, kept);
        }
    }
}
