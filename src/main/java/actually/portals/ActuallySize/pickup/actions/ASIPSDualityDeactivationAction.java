package actually.portals.ActuallySize.pickup.actions;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.netcode.ASINetworkManager;
import actually.portals.ActuallySize.netcode.packets.clientbound.ASINCItemEntityDeactivationPacket;
import actually.portals.ActuallySize.pickup.events.ASIPSDeactivateItemEntityEvent;
import actually.portals.ActuallySize.pickup.ASIPickupSystemManager;
import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This duality deactivation action is meant to deactivate any
 * one active Item-Entity duality item. If it is not active, it
 * will fail in {@link #isVerified()} step. This action will
 * <p>
 * (1) Save the Entity counterpart onto the item
 * <p>
 * (2) Remove the Entity counterpart from the world
 * <p>
 * (2) Unregister the variables between Item, Entity and Holder
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIPSDualityDeactivationAction extends ASIPSDualityAction {

    /**
     * @see #getStackLocation()
     * @since 1.0.0
     */
    @Nullable ItemStackLocation<? extends Entity> stackLocation;
    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override public @Nullable ItemStackLocation<? extends Entity> getStackLocation() { return stackLocation; }
    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override public void setStackLocation(@Nullable ItemStackLocation<? extends Entity> isl) { stackLocation = isl; }

    /**
     * @see #getItemCounterpart()
     * @since 1.0.0
     */
    @Nullable ItemStack itemCounterpart;
    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override public @Nullable ItemStack getItemCounterpart() { return itemCounterpart; }
    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override public void setItemCounterpart(@Nullable ItemStack item) { itemCounterpart = item;}

    /**
     * @see #getEntityCounterpart()
     * @since 1.0.0
     */
    @Nullable Entity entityCounterpart;
    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override @Nullable public Entity getEntityCounterpart() { return entityCounterpart; }
    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override public void setEntityCounterpart(@Nullable Entity entity) { entityCounterpart = entity; }

    /**
     * @see #getHoldPoint()
     * @since 1.0.0
     */
    @Nullable ASIPSHoldPoint holdPoint;
    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override @Nullable public ASIPSHoldPoint getHoldPoint() { return holdPoint; }
    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override public void setHoldPoint(@Nullable ASIPSHoldPoint hold) { holdPoint = hold; }

    /**
     * @param itemDuality A presumably active Item-Entity item
     *
     * @since 1.0.0
     */
    public ASIPSDualityDeactivationAction(@NotNull ItemDualityCounterpart itemDuality) {
        stackLocation = itemDuality.actuallysize$getItemStackLocation();
        entityCounterpart = itemDuality.actuallysize$getEntityCounterpart();
        itemCounterpart = (ItemStack) (Object) itemDuality;

        holdPoint =
                // If the entity exists, use the entity hold point
                entityCounterpart != null ? ((EntityDualityCounterpart) entityCounterpart).actuallysize$getHoldPoint() :

                // Otherwise, fetch the hold point directly from the holder
                stackLocation == null ? null :
                        ((ItemEntityDualityHolder) stackLocation.getHolder()).actuallysize$getHoldPoint(stackLocation);
    }

    /**
     * @param entityDuality An entity of an active Item-Entity duality.
     *
     * @since 1.0.0
     */
    public ASIPSDualityDeactivationAction(@NotNull EntityDualityCounterpart entityDuality) {
        entityCounterpart = (Entity) entityDuality;
        holdPoint = entityDuality.actuallysize$getHoldPoint();
        itemCounterpart = entityDuality.actuallysize$getItemCounterpart();
        if (itemCounterpart != null) {
            stackLocation = ((ItemDualityCounterpart) (Object) itemCounterpart).actuallysize$getItemStackLocation();
        }
    }

    /**
     * To handle deactivation of Item-Entities in the client-side, where
     * "deactivation" means unregistering the trio Item-Entity-Holder.
     *
     * @param fromPacket The packet that arrived from the network
     *
     * @param world The best guess for the world where the entity and holder are.
     *              It is unknown if it is possible to receive packets from other
     *              worlds not the local player's, or if an entity duality can be
     *              deactivated in another world or so.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSDualityDeactivationAction(@NotNull ASINCItemEntityDeactivationPacket fromPacket, @Nullable Level world) {

        // I mean, we are checking that the world exists later on...
        if (world == null) { return; }

        // Entity must exist, it is the only real requirement
        entityCounterpart = fromPacket.getEntityCounterpart(world);
        if (entityCounterpart == null) { return; }

        // Proceed as if building a normal deactivation based on an entity parameter
        holdPoint = ((EntityDualityCounterpart) entityCounterpart).actuallysize$getHoldPoint();
        itemCounterpart = ((EntityDualityCounterpart) entityCounterpart).actuallysize$getItemCounterpart();
        if (itemCounterpart != null) {
            stackLocation = ((ItemDualityCounterpart) (Object) itemCounterpart).actuallysize$getItemStackLocation();
        }
    }

    /**
     * Best used with Item Stack Locations that are a constant slot. Caution when used
     * with variable slots like the mainhand -> When it tries to remove the entity
     * previously in this slot, it might search for an item in the current mainhand
     * which could be different for the previous and fail to unregister it.
     *
     * @param stackLocation A location in someone's inventory to clear/deactivate.
     *
     * @since 1.0.0
     */
    public ASIPSDualityDeactivationAction(@NotNull ItemStackLocation<? extends Entity> stackLocation) {
        this.stackLocation = stackLocation;
        itemCounterpart = stackLocation.getItemStack();
        ItemEntityDualityHolder dualityHolder = ((ItemEntityDualityHolder) this.stackLocation.getHolder());
        holdPoint = dualityHolder.actuallysize$getHoldPoint(this.stackLocation);

        // First priority is any active entity duality in the target hold point. We are deactivating that.
        entityCounterpart = (Entity) ((ItemEntityDualityHolder) stackLocation.getHolder()).actuallysize$getHeldItemEntityDuality(holdPoint);

        // Second priority is deactivating the entity duality linked to this item, we'd be deactivating that
        if (itemCounterpart != null && entityCounterpart == null) {
            ItemDualityCounterpart itemDuality = (ItemDualityCounterpart) (Object) itemCounterpart;
            entityCounterpart = itemDuality.actuallysize$getEntityCounterpart();
        }

        /*HDA*/ActuallySizeInteractions.Log("ASI &9 HDD &c StackLocation constructor ENTITY FOUND? " + (entityCounterpart != null) + " // For " + stackLocation.getStatement());
        // Third priority is deactivating the entity in this slot of the holder
        if (entityCounterpart == null) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &9 HDD &c StackLocation constructor Try to get from holder " + stackLocation.getHolder().getScoreboardName());
            entityCounterpart = (Entity) dualityHolder.actuallysize$getHeldItemEntityDuality(holdPoint);
        }
    }

    @Override
    public boolean isVerified() {

        // Basic requirements
        if (stackLocation == null) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &9 HDD &c Item Location Unverified");
            return false; }

        if (holdPoint == null) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &c Only Equipment Slot statements are supported atm");
            return false; }

        // Duality must exist
        if (entityCounterpart == null) {

            /*
             * Checking for holder having an item here will stop variable slots
             * from unregistering correctly (most obviously, the main hand) since
             * this will dissuade them from unregistering their duality entity
             */
            //ItemEntityDualityHolder dualityHolder = (ItemEntityDualityHolder) itemCounterpart.getEntity();
            //if (dualityHolder.actuallysize$getHeldItemEntityDuality(itemCounterpart.getSpecialization().getEquipmentSlot()) == null) {
            //    //*HDA*/ActuallySizeInteractions.Log("ASI &9 HDD &c Holder Activity Unverified");
            //    return false;
            //}

            //*HDA*/ActuallySizeInteractions.Log("ASI &9 HDD &c Entity Unverified");
            //return false;

            /*HDA*/ActuallySizeInteractions.Log("ASI &9 HDD &2 Clear Verified Passed");
            return true;

        // If the entity does exist
        } else {

            // It must be alive // not removed

        }

        if (itemCounterpart == null) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &9 HDD &c Item Unverified");
            return false; }

        /*HDA*/ActuallySizeInteractions.Log("ASI &9 HDD &2 Verification Passed");

        // Done
        return true;
    }

    @Override
    public boolean isAllowed() {
        /*HDA*/ActuallySizeInteractions.Log("ASI &9 HDD &b Permission Passed");

        // This is not a cancellable event, really
        return true;
    }

    @Override
    public void resolve() {
        /*HDA*/ActuallySizeInteractions.Log("ASI &9 HDD &b RESOLVING");

        // Should have been confirmed during verification step but
        if (stackLocation == null) { return; }
        if (holdPoint == null) { return; }
        if (entityCounterpart == null) { return; }

        /*
         * Attempting to deactivate players redirects them to escape instead
         */
        if (entityCounterpart instanceof Player) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &9 HDD &c Player exception - redirecting to ESCAPE action");
            ASIPSDualityEscapeAction playerEscape = new ASIPSDualityEscapeAction((EntityDualityCounterpart) entityCounterpart);
            if (playerEscape.isVerified()) { playerEscape.resolve(); }
            return;
        }

        // Full-Identifying
        ItemEntityDualityHolder dualityHolder = (ItemEntityDualityHolder) stackLocation.getHolder();
        ItemDualityCounterpart itemDuality = (ItemDualityCounterpart) (Object) itemCounterpart;
        EntityDualityCounterpart entityDuality = (EntityDualityCounterpart) entityCounterpart;
        boolean isServer = !dualityHolder.actuallysize$getHolderWorld().isClientSide;

        // #0 Terminate duality flux calculations
        if (isServer) { ASIPickupSystemManager.clearDualityFlux(entityCounterpart); }

        /*
         * #1 Run Event - If an Entity is being deleted, run the Deactivation Event
         */
        if (entityCounterpart != null) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &9 HDD &r Deactivation Event Broadcast");
            ASIPSDeactivateItemEntityEvent broadcast = new ASIPSDeactivateItemEntityEvent(stackLocation, entityCounterpart);
            MinecraftForge.EVENT_BUS.post(broadcast);
        }

        /*
         * #2   Unregistering from the item counterpart
         */
        if (itemDuality != null) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &9 HDD &r Unregistering - Item " + itemCounterpart.getDisplayName().getString());
            itemDuality.actuallysize$setItemEntityHolder(null);
            itemDuality.actuallysize$setEntityCounterpart(null);
            itemDuality.actuallysize$setItemStackLocation(null);
        }

        /*
         * #3   Unregistering the entity counterpart.
         */
        if (entityDuality != null) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &9 HDD &r Unregistering - Entity " + entityCounterpart.getScoreboardName());
            entityDuality.actuallysize$setItemEntityHolder(null);
            entityDuality.actuallysize$setItemCounterpart(null);
            entityDuality.actuallysize$setItemStackLocation(null);
            entityDuality.actuallysize$setHoldPoint(null);
        }

        /*
         * #4   Unregistering from the holder
         */
        /*HDA*/ActuallySizeInteractions.Log("ASI &9 HDD &r Unregistering - Holder " + stackLocation.getHolder().getScoreboardName() + " : " + stackLocation.getStatement());
        dualityHolder.actuallysize$setHeldItemEntityDuality(holdPoint, null);

        /*
         * #5   Save the entity counterpart into the item
         * #6   Remove the entity counterpart from the world
         *
         * --> But only in the server-side <--
         */
        if (entityCounterpart != null && isServer) {

            // Save and Pop
            if (itemCounterpart != null) {
                /*HDA*/ActuallySizeInteractions.Log("ASI &9 HDD &r Serverside Save");
                ASIPickupSystemManager.saveNonPlayerIntoItemNBT(entityCounterpart, itemCounterpart);
                itemCounterpart.setPopTime(3);
            }

            // Delete entity
            /*HDA*/ActuallySizeInteractions.Log("ASI &9 HDD &r Unregistering - Server");
            entityCounterpart.remove(Entity.RemovalReason.UNLOADED_WITH_PLAYER);

            /*
             * #9 Unregister from Item-Entity duality kitty
             */
            ASIPickupSystemManager.unregisterActiveEntityCounterpart(entityCounterpart);

            /*
             * #10   Sync it over the network
             */

            // Create packet to send over the network and send to those tracking this entity
            ASINCItemEntityDeactivationPacket packet = new ASINCItemEntityDeactivationPacket(entityCounterpart);
            ASINetworkManager.broadcastEntityUpdate(entityCounterpart, packet);
            /*HDA*/ActuallySizeInteractions.Log("ASI &9 HDD &r Sent packet. ");
        }
    }
}
