package actually.portals.ActuallySize.pickup.actions;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.netcode.ASINetworkManager;
import actually.portals.ActuallySize.netcode.packets.clientbound.ASINCItemEntityEscapePacket;
import actually.portals.ActuallySize.pickup.ASIPickupSystemManager;
import actually.portals.ActuallySize.pickup.events.ASIPSDeactivateItemEntityEvent;
import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This process will deactivate an Item-Entity duality by
 * freeing the entity and destroying the item. Usually,
 * deactivation means the entity "returns" to the item and
 * thus the item remains while the entity is destroyed,
 * but this action will do the opposite: The entity will
 * remain while destroying the item.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIPSDualityEscapeAction extends ASIPSDualityAction {

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
     * If the item will be removed as a result of this
     * entity escaping. Only disable if you know what
     * you are doing — since this can lead to duplicating
     * the entity.
     * <p>
     * A popular example of disabling this is when consuming
     * the item to place the entity down, since the consumption
     * is happening as part of {@link actually.portals.ActuallySize.pickup.item.ASIPSHeldEntityItem#useOn(UseOnContext)}
     * so it doesn't need to be removed here. This way, placing down
     * the entity in creative mode does not consume it and it can be
     * placed down infinitely.
     *
     * @since 1.0.0
     */
    boolean andRemoveItem = true;

    /**
     * @param removeItem If the item will be deleted as a result of this entity escaping.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void setAndRemoveItem(boolean removeItem) { andRemoveItem = removeItem; }

    /**
     * @param itemDuality A presumably active Item-Entity item
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSDualityEscapeAction(@NotNull ItemDualityCounterpart itemDuality) {
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
     * @author Actually Portals
     */
    public ASIPSDualityEscapeAction(@NotNull EntityDualityCounterpart entityDuality) {
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
    public ASIPSDualityEscapeAction(@NotNull ASINCItemEntityEscapePacket fromPacket, @Nullable Level world) {

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
     * @author Actually Portals
     */
    public ASIPSDualityEscapeAction(@NotNull ItemStackLocation<? extends Entity> stackLocation) {
        this.stackLocation = stackLocation;
        itemCounterpart = stackLocation.getItemStack();
        holdPoint = ((ItemEntityDualityHolder) this.stackLocation.getHolder()).actuallysize$getHoldPoint(this.stackLocation);

        // First priority is any active entity duality in the target hold point. We are escaping that.
        entityCounterpart = (Entity) ((ItemEntityDualityHolder) stackLocation.getHolder()).actuallysize$getHeldItemEntityDuality(holdPoint);

        // Second priority is deactivating the entity duality linked to this item, we'd be escaping that
        if (itemCounterpart != null && entityCounterpart == null) {
            ItemDualityCounterpart itemDuality = (ItemDualityCounterpart) (Object) itemCounterpart;
            entityCounterpart = itemDuality.actuallysize$getEntityCounterpart();
        }
    }

    @Override
    public boolean isVerified() {
        /*HDA*/ActuallySizeInteractions.Log("ASI &3 HDE &3 [" + (entityCounterpart == null ? "null" : entityCounterpart.level().isClientSide) + "] &8 Verifying " + getClass().getSimpleName() + " " + (stackLocation == null ? "null ISL" : stackLocation.getStatement().toString()) + "... ");

        // The entity, which will remain, must exist
        if (entityCounterpart == null) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &3 HDE &c Entity Unverified");
            return false; }

        if (stackLocation != null && holdPoint == null) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &3 HDE &c This Item Stack Location is not supported");
            return false; }

        // We actually don't care that much about the item and holder. Done
        /*HDA*/ActuallySizeInteractions.Log("ASI &3 HDE &3 [" + entityCounterpart.level().isClientSide + "] &2 Verified " + getClass().getSimpleName() + " " + (stackLocation == null ? "null ISL" : stackLocation.getStatement().toString()) + "... ");
        return true;
    }

    @Override
    public boolean isAllowed() {
        /*HDA*/ActuallySizeInteractions.Log("ASI &3 HDE &3 [" + (entityCounterpart == null ? "null" : entityCounterpart.level().isClientSide) + "] &e Allowed " + getClass().getSimpleName() + " " + (stackLocation == null ? "null ISL" : stackLocation.getStatement().toString()) + "... ");

        // This is not really cancellable
        return true;
    }

    @Override
    public void resolve() {
        /*HDA*/ActuallySizeInteractions.Log("ASI &3 HDE &3 [" + (entityCounterpart == null ? "null" : entityCounterpart.level().isClientSide) + "] &b Resolving " + getClass().getSimpleName() + " " + (stackLocation == null ? "null ISL" : stackLocation.getStatement().toString()) + "... ");

        // Should have been confirmed during verification step but
        if (entityCounterpart == null) { return; }

        // Full-Identifying
        ItemDualityCounterpart itemDuality = (ItemDualityCounterpart) (Object) itemCounterpart;
        EntityDualityCounterpart entityDuality = (EntityDualityCounterpart) entityCounterpart;
        ItemEntityDualityHolder dualityHolder = itemDuality == null ? null : itemDuality.actuallysize$getItemEntityHolder();
        boolean isServer = !entityCounterpart.level().isClientSide;

        // #0 Terminate duality flux calculations
        if (isServer) { ASIPickupSystemManager.clearDualityFlux(entityCounterpart); }

        /*
         * #1 Run Event - If the item exists, run the Deactivation Event
         */
        if (stackLocation != null) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &3 HDE &r Deactivation Event Broadcast");
            ASIPSDeactivateItemEntityEvent broadcast = new ASIPSDeactivateItemEntityEvent(stackLocation, entityCounterpart);
            MinecraftForge.EVENT_BUS.post(broadcast);
        }

        /*
         * #2   Unregistering the entity counterpart.
         */
        if (entityDuality != null) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &3 HDE &r Unregistering - Entity " + entityCounterpart.getScoreboardName());
            entityDuality.actuallysize$setItemEntityHolder(null);
            entityDuality.actuallysize$setItemCounterpart(null);
            entityDuality.actuallysize$setItemStackLocation(null);
            entityDuality.actuallysize$setHoldPoint(null);
        }

        /*
         * #3   Unregistering from the item counterpart
         */
        if (itemDuality != null) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &3 HDE &r Unregistering - Item " + itemCounterpart.getDisplayName().getString());
            itemDuality.actuallysize$setItemEntityHolder(null);
            itemDuality.actuallysize$setEntityCounterpart(null);
            itemDuality.actuallysize$setItemStackLocation(null);
        }

        /*
         * #4   Unregistering from the holder
         */
        if (dualityHolder != null) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &3 HDE &r Unregistering - Holder " + stackLocation.getHolder().getScoreboardName() + " : " + stackLocation.getStatement());
            dualityHolder.actuallysize$setHeldItemEntityDuality(holdPoint, null);
        }

        /*
         * #5   Remove the item (optional)
         *
         * --> But only in the server-side <--
         */
        if (isServer) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &3 HDE &r Unregistering - Server");

            // Delete item
            if (itemCounterpart != null && andRemoveItem) { itemCounterpart.setCount(0); }

            /*
             * #6 Unregister from Item-Entity duality kitty
             */
            ASIPickupSystemManager.unregisterActiveEntityCounterpart(entityCounterpart);

            /*
             * #7   Sync it over the network
             */

            // Create packet to send over the network and send to those tracking this entity
            ASINCItemEntityEscapePacket packet = new ASINCItemEntityEscapePacket(entityCounterpart);
            ASINetworkManager.broadcastEntityUpdate(entityCounterpart, packet);
            /*HDA*/ActuallySizeInteractions.Log("ASI &3 HDE &r Sent packet. ");
        }
    }
}
