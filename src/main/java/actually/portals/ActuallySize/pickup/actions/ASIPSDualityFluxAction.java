package actually.portals.ActuallySize.pickup.actions;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.netcode.ASINetworkManager;
import actually.portals.ActuallySize.netcode.packets.ASINetworkDelayableAction;
import actually.portals.ActuallySize.netcode.packets.clientbound.ASINCItemEntityActivationPacket;
import actually.portals.ActuallySize.netcode.packets.clientbound.ASINCItemEntityDeactivationPacket;
import actually.portals.ActuallySize.netcode.packets.clientbound.ASINCItemEntityFluxPacket;
import actually.portals.ActuallySize.pickup.ASIPickupSystemManager;
import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSRegisterableHoldPoint;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import gunging.ootilities.GungingOotilitiesMod.exploring.players.ISPExplorerStatements;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.APIFriendlyProcess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Sometimes, you switch an item from one place to another, and if it is
 * an Item-Entity both slots will have it active. Then it is annoying to
 * delete it from the world and rebuild it. That's why the Flux action will
 * simply make sure the item is registered where it has to be registered
 * and keep the original entity counterpart alive.
 * <p>
 * This most importantly works with Players who cannot be removed and must
 * be kept alive through the entire process.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIPSDualityFluxAction implements APIFriendlyProcess, ASINetworkDelayableAction {

    /**
     * @see #getAttempts()
     *
     * @since 1.0.0
     */
    int attempts;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override public int getAttempts() { return attempts; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override public void logAttempt() { attempts++; }

    /**
     * The deactivation duality action, the origin, the location this duality is being removed from.
     *
     * @since 1.0.0
     */
    @NotNull public final ASIPSDualityAction from;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public ASIPSDualityAction getFrom() { return from; }

    /**
     * The activation duality action, the target, the final location of this duality.
     *
     * @since 1.0.0
     */
    @NotNull public final ASIPSDualityAction to;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public ASIPSDualityAction getTo() { return to; }

    /**
     * @param from The deactivation duality action, the origin, the location this duality is being removed from.
     * @param to The activation duality action, the target, the final location of this duality.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSDualityFluxAction(@NotNull ASIPSDualityAction from, @NotNull ASIPSDualityAction to) {
        this.from = from;
        this.to = to;
    }

    /**
     * @param packet The combined flux packet encoding for both a deactivation and activation events
     * @param world The world where the packets are taking place (They really should be in the same world!)
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSDualityFluxAction(@NotNull ASINCItemEntityFluxPacket packet, @Nullable Level world) {
        this.from = new ASIPSDualityDeactivationAction(packet.getFrom(), world);
        this.to = new ASIPSDualityActivationAction(packet.getTo(), world);
    }

    @Override
    public boolean isVerified() {
        /*HDA*/ActuallySizeInteractions.LogHDA(true, getClass(), "HDF", "Verifying");
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDF", "[From] &f {0}", getFrom().getClass().getSimpleName());
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDF", "[To] &f {0}", getTo().getClass().getSimpleName());
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDF", "Clientside", (getTo().getEntityCounterpart() == null ? "null" : getTo().getEntityCounterpart().level().isClientSide));

        // The entity counterparts must match
        if (getTo().getEntityCounterpart() == null) {
            /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDF", "&c Null [To] Entity");
            /*HDA*/ActuallySizeInteractions.LogHDA(false, getClass(), "HDE", "Verifying");
            return false; }

        if (!getTo().getEntityCounterpart().equals(getFrom().getEntityCounterpart())) {
            /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDF", "&c Estranged Entity");
            /*HDA*/ActuallySizeInteractions.LogHDA(false, getClass(), "HDE", "Verifying");
            return false;
        }

        if (!getTo().isVerified()) {
            /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDF", "&c [To] Unverified");
            /*HDA*/ActuallySizeInteractions.LogHDA(false, getClass(), "HDE", "Verifying");
            return false;
        }

        if (!getFrom().isVerified()) {
            /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDF", "&c [From] Unverified");
            /*HDA*/ActuallySizeInteractions.LogHDA(false, getClass(), "HDE", "Verifying");
            return false;
        }

        // The two constituents must be verified
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDF", "&2 VERIFIED");
        /*HDA*/ActuallySizeInteractions.LogHDA(false, getClass(), "HDF", "Verifying");
        return true;
    }

    @Override
    public boolean isAllowed() {
        /*HDA*/ActuallySizeInteractions.LogHDA(true, getClass(), "HDF", "Allowing");
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDF", "[From] &f {0}", getFrom().getClass().getSimpleName());
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDF", "[To] &f {0}", getTo().getClass().getSimpleName());
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDF", "Clientside", (getTo().getEntityCounterpart() == null ? "null" : getTo().getEntityCounterpart().level().isClientSide));

        if (!getTo().isAllowed()) {
            /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDF", "&6 [To] Disallowed");
            /*HDA*/ActuallySizeInteractions.LogHDA(false, getClass(), "HDF", "Allowing");
            return false;
        }

        if (!getFrom().isAllowed()) {
            /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDF", "&6 [From] Disallowed");
            /*HDA*/ActuallySizeInteractions.LogHDA(false, getClass(), "HDF", "Allowing");
            return false;
        }

        // The two constituents must be allowed
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDF", "&6 ALLOWED");
        /*HDA*/ActuallySizeInteractions.LogHDA(false, getClass(), "HDF", "Allowing");
        return true;
    }

    @Override
    public void resolve() {
        /*HDA*/ActuallySizeInteractions.LogHDA(true, getClass(), "HDF", "Resolving");
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDF", "[From] &f {0}", getFrom().getClass().getSimpleName());
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDF", "[To] &f {0}", getTo().getClass().getSimpleName());
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDF", "Clientside", (getTo().getEntityCounterpart() == null ? "null" : getTo().getEntityCounterpart().level().isClientSide));

        // Full identify
        Entity entityCounterpart = to.getEntityCounterpart();
        EntityDualityCounterpart entityDuality = (EntityDualityCounterpart) entityCounterpart;
        ItemStack itemCounterpartFrom = from.getItemCounterpart();
        ItemDualityCounterpart itemDualityFrom = (ItemDualityCounterpart) (Object) itemCounterpartFrom;
        ItemStack itemCounterpartTo = to.getItemCounterpart();
        ItemDualityCounterpart itemDualityTo = (ItemDualityCounterpart) (Object) itemCounterpartTo;
        ASIPSHoldPoint holdPointFrom = from.getHoldPoint();
        ASIPSHoldPoint holdPointTo = to.getHoldPoint();
        ItemStackLocation<? extends Entity> stackLocationFrom = from.getStackLocation();
        ItemStackLocation<? extends Entity> stackLocationTo = to.getStackLocation();

        if (entityCounterpart == null) { return; }
        boolean isServer = !entityCounterpart.level().isClientSide;

        /*
         * In the client-side, remote players do not have a cursor
         * slot. Then this makes for a special case when activating
         * a duality actually creates that item.
         */
        if (!isServer) {
            if (stackLocationTo.getHolder() instanceof net.minecraft.client.player.RemotePlayer) {
                if (stackLocationTo.getStatement().equals(ISPExplorerStatements.CURSOR)) {

                    // That's funny haha, involving ourselves with the cursor slot of a remote player...
                    Player player = (Player) stackLocationTo.getHolder();
                    ItemStack drop = ASIPickupSystemManager.generateHeldItem(entityCounterpart);
                    player.inventoryMenu.setCarried(drop);
                    /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDF", "&3 Remote player cursor adjusted");
                    itemCounterpartTo = drop;
                    itemDualityTo = (ItemDualityCounterpart) (Object) itemCounterpartTo;
                }
            }
        }

        // This would have been checked in verify but okay
        if (holdPointFrom == null) { return; }
        if (holdPointTo == null) { return; }
        if (itemDualityTo == null) { return; }
        if (stackLocationTo == null) { return; }
        if (stackLocationFrom == null) { return; }
        Entity holderEntityFrom = stackLocationFrom.getHolder();
        ItemEntityDualityHolder holderDualityFrom = (ItemEntityDualityHolder) holderEntityFrom;
        Entity holderEntityTo = stackLocationTo.getHolder();
        ItemEntityDualityHolder holderDualityTo = (ItemEntityDualityHolder) holderEntityTo;

        /*
         * #1   Holder direct switcheroo
         *
         * Note that it is only "removed" from the hold point array if it is in fact this same
         * entity that is still there, because in some extremely quick scenarios (namely, a SWAP
         * flux where two item-dualities swap slots), the old slot may have a different entity
         * already.
         */
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDF", "Flux holder &6 {0} {1} &r => &e {2} {3}", holderEntityFrom.getScoreboardName(), holdPointFrom, holderEntityTo.getScoreboardName(), holdPointTo);
        EntityDualityCounterpart instantFrom = holderDualityTo.actuallysize$getHeldEntityDualities().get(holdPointFrom);
        if (instantFrom instanceof Entity && ((Entity) instantFrom).getUUID().equals(entityCounterpart.getUUID())) {
            holderDualityFrom.actuallysize$getHeldEntityDualities().remove(holdPointFrom); }
        holderDualityTo.actuallysize$getHeldEntityDualities().put(holdPointTo, entityDuality);

        /*
         * #2   Unregistering from the old item counterpart
         */
        if (itemDualityFrom != null && itemDualityFrom.actuallysize$isDualityActive()) {
            /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDF", "Unregistered old item &f {0}", itemCounterpartFrom.getDisplayName().getString());
            itemDualityFrom.actuallysize$setItemEntityHolder(null);
            itemDualityFrom.actuallysize$setEntityCounterpart(null);
            itemDualityFrom.actuallysize$setItemStackLocation(null);
        }

        /*
         * #3   Registering onto the item counterpart
         */
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDF", "Registered new item &f {0}", itemCounterpartTo.getDisplayName().getString());
        itemDualityTo.actuallysize$setItemEntityHolder(holderDualityTo);
        itemDualityTo.actuallysize$setEntityCounterpart(entityCounterpart);
        itemDualityTo.actuallysize$setItemStackLocation(stackLocationTo);

        /*
         * #4   Updating the entity counterpart.
         */
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDF", "Flux entity {0}", entityCounterpart.getScoreboardName());
        entityDuality.actuallysize$setItemEntityHolder(holderDualityTo);
        entityDuality.actuallysize$setItemCounterpart(itemCounterpartTo);
        entityDuality.actuallysize$setItemStackLocation(stackLocationTo);
        entityDuality.actuallysize$setHoldPoint(holdPointTo);

        // Done for clientside
        if (!isServer) {
            /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDF", "&3 RESOLVED");
            /*HDA*/ActuallySizeInteractions.LogHDA(false, getClass(), "HDF", "Resolving");
            return; }

        /*
         * #5   Sync it over the network
         */

        // Create packet to send over the network and send to those tracking this entity
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "&f Network Packet Sent");
        ASINCItemEntityFluxPacket packet = new ASINCItemEntityFluxPacket(
                new ASINCItemEntityDeactivationPacket(getFrom().getEntityCounterpart()),
                new ASINCItemEntityActivationPacket(getTo().getStackLocation(), getTo().getEntityCounterpart()));
        ASINetworkManager.broadcastEntityUpdate(entityCounterpart, packet);

        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDF", "&3 RESOLVED");
        /*HDA*/ActuallySizeInteractions.LogHDA(false, getClass(), "HDF", "Resolving");
    }
}
