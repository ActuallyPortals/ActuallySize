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
        /*HDA*/ActuallySizeInteractions.Log("ASI &1 HDF &r Flux Action VERIFICATION start [From " + (getFrom().getStackLocation() == null ? "null": getFrom().getStackLocation().getStatement()) + "] [To " + (getTo().getStackLocation() == null ? "null": getTo().getStackLocation().getStatement()) + "]");

        // The entity counterparts must match
        if (getTo().getEntityCounterpart() == null) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &1 HDF &c Entity Unverified");
            return false; }

        if (!getTo().getEntityCounterpart().equals(getFrom().getEntityCounterpart())) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &1 HDF &c Entity Estranged");
            return false; }

        if (!getTo().isVerified()) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &1 HDF &c Destination Unverified");
            return false;
        }

        if (!getFrom().isVerified()) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &1 HDF &c Origin Unverified");
            return false;
        }

        /*HDA*/ActuallySizeInteractions.Log("ASI &1 HDF &r Flux Action VERIFICATION &a PASSED");

        // The two constituents must be verified
        return true;
    }

    @Override
    public boolean isAllowed() {
        /*HDA*/ActuallySizeInteractions.Log("ASI &1 HDF &r Flux Action ALLOW start");

        if (!getTo().isAllowed()) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &1 HDF &c Destination Disallowed");
            return false;
        }

        if (!getFrom().isAllowed()) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &1 HDF &c Origin Disallowed");
            return false;
        }

        /*HDA*/ActuallySizeInteractions.Log("ASI &1 HDF &r Flux Action ALLOW &b PASSED");

        // The two constituents must be allowed
        return true;
    }

    @Override
    public void resolve() {
        /*HDA*/ActuallySizeInteractions.Log("ASI &1 HDF &r Item-Entity Flux &b RESOLVING");

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
                    /*HDA*/ActuallySizeInteractions.Log("ASI &1 HDF &7 Remote player cursor adjusted");
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
        /*HDA*/ActuallySizeInteractions.Log("ASI &1 HDF &r Flux Holder " + holderEntityFrom.getScoreboardName() + " " + ((ASIPSRegisterableHoldPoint)holdPointFrom).getNamespacedKey() + " => " + holderEntityTo.getScoreboardName() + " " + ((ASIPSRegisterableHoldPoint)holdPointTo).getNamespacedKey());
        EntityDualityCounterpart instantFrom = holderDualityTo.actuallysize$getHeldEntityDualities().get(holdPointFrom);
        if (instantFrom instanceof Entity && ((Entity) instantFrom).getUUID().equals(entityCounterpart.getUUID())) {
            holderDualityFrom.actuallysize$getHeldEntityDualities().remove(holdPointFrom); }
        holderDualityTo.actuallysize$getHeldEntityDualities().put(holdPointTo, entityDuality);

        /*
         * #2   Unregistering from the old item counterpart
         */
        if (itemDualityFrom != null && itemDualityFrom.actuallysize$isDualityActive()) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &1 HDF &r Unregistering old item " + itemCounterpartFrom.getDisplayName().getString());
            itemDualityFrom.actuallysize$setItemEntityHolder(null);
            itemDualityFrom.actuallysize$setEntityCounterpart(null);
            itemDualityFrom.actuallysize$setItemStackLocation(null);
        }

        /*
         * #3   Registering onto the item counterpart
         */
        /*HDA*/ActuallySizeInteractions.Log("ASI &1 HDF &r Registered new item " + itemCounterpartTo.getDisplayName().getString());
        itemDualityTo.actuallysize$setItemEntityHolder(holderDualityTo);
        itemDualityTo.actuallysize$setEntityCounterpart(entityCounterpart);
        itemDualityTo.actuallysize$setItemStackLocation(stackLocationTo);

        /*
         * #4   Updating the entity counterpart.
         */
        /*HDA*/ActuallySizeInteractions.Log("ASI &1 HDF &r Flux Entity " + entityCounterpart.getScoreboardName());
        entityDuality.actuallysize$setItemEntityHolder(holderDualityTo);
        entityDuality.actuallysize$setItemCounterpart(itemCounterpartTo);
        entityDuality.actuallysize$setItemStackLocation(stackLocationTo);
        entityDuality.actuallysize$setHoldPoint(holdPointTo);

        // Done for clientside
        if (!isServer) { return; }

        /*
         * #5   Sync it over the network
         */

        // Create packet to send over the network and send to those tracking this entity
        /*HDA*/ActuallySizeInteractions.Log("ASI &1 HDF &r Flux packet. ");
        ASINCItemEntityFluxPacket packet = new ASINCItemEntityFluxPacket(
                new ASINCItemEntityDeactivationPacket(getFrom().getEntityCounterpart()),
                new ASINCItemEntityActivationPacket(getTo().getStackLocation(), getTo().getEntityCounterpart()));
        ASINetworkManager.broadcastEntityUpdate(entityCounterpart, packet);

        /*HDA*/ActuallySizeInteractions.Log("ASI &1 HDF &r Item-Entity duality flux &a COMPLETED ");
    }
}
