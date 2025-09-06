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
        /*HDA*/ActuallySizeInteractions.LogHDA(true, getClass(), "HDE", "Item Constructor");

        stackLocation = itemDuality.actuallysize$getItemStackLocation();
        entityCounterpart = itemDuality.actuallysize$getEntityCounterpart();
        itemCounterpart = (ItemStack) (Object) itemDuality;

        holdPoint =
                // If the entity exists, use the entity hold point
                entityCounterpart != null ? ((EntityDualityCounterpart) entityCounterpart).actuallysize$getHoldPoint() :

                // Otherwise, fetch the hold point directly from the holder
                stackLocation == null ? null :
                        ((ItemEntityDualityHolder) stackLocation.getHolder()).actuallysize$getHoldPoint(stackLocation);

        /*HDA*/ActuallySizeInteractions.LogHDA(false, getClass(), "HDE", "Item Constructor");
    }

    /**
     * @param entityDuality An entity of an active Item-Entity duality.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSDualityEscapeAction(@NotNull EntityDualityCounterpart entityDuality) {
        /*HDA*/ActuallySizeInteractions.LogHDA(true, getClass(), "HDE", "Entity Constructor");

        entityCounterpart = (Entity) entityDuality;
        holdPoint = entityDuality.actuallysize$getHoldPoint();
        itemCounterpart = entityDuality.actuallysize$getItemCounterpart();
        if (itemCounterpart != null) {
            stackLocation = ((ItemDualityCounterpart) (Object) itemCounterpart).actuallysize$getItemStackLocation();
        }

        /*HDA*/ActuallySizeInteractions.LogHDA(false, getClass(), "HDE", "Entity Constructor");
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
        /*HDA*/ActuallySizeInteractions.LogHDA(true, getClass(), "HDE", "Packet Constructor");

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

        /*HDA*/ActuallySizeInteractions.LogHDA(false, getClass(), "HDE", "Packet Constructor");
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
        /*HDA*/ActuallySizeInteractions.LogHDA(true, getClass(), "HDE", "Slot Constructor");

        this.stackLocation = stackLocation;
        itemCounterpart = stackLocation.getItemStack();
        ItemEntityDualityHolder dualityHolder = (ItemEntityDualityHolder) this.stackLocation.getHolder();
        holdPoint = dualityHolder.actuallysize$getHoldPoint(this.stackLocation);

        // First priority is any active entity duality in the target hold point. We are escaping that.
        if (holdPoint != null) {
            entityCounterpart = (Entity) dualityHolder.actuallysize$getHeldItemEntityDuality(holdPoint);
            /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "Found {0} duality in holder", (entityCounterpart != null ? entityCounterpart.getScoreboardName() : "null"));
        }

        // Second priority is deactivating the entity duality linked to this item, we'd be escaping that
        if (itemCounterpart != null && entityCounterpart == null) {
            ItemDualityCounterpart itemDuality = (ItemDualityCounterpart) (Object) itemCounterpart;
            entityCounterpart = itemDuality.actuallysize$getEntityCounterpart();
            /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "Found {0} duality in item", (entityCounterpart != null ? entityCounterpart.getScoreboardName() : "null"));
        }

        /*HDA*/ActuallySizeInteractions.LogHDA(false, getClass(), "HDE", "Slot Constructor");
    }

    @Override
    public boolean isVerified() {
        /*HDA*/ActuallySizeInteractions.LogHDA(true, getClass(), "HDE", "Verifying");
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "Clientside &f {0}", (entityCounterpart == null ? "null" : entityCounterpart.level().isClientSide));
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "Slot &e {0}", (stackLocation == null ? "null" : stackLocation));
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "Hold Point &b {0}", holdPoint);
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "Item &b {0}", (itemCounterpart == null ? "null" : itemCounterpart.getDisplayName().getString()));
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "Entity &f {0}", (entityCounterpart == null ? "null" : entityCounterpart.getScoreboardName()));

        // The entity, which will remain, must exist
        if (entityCounterpart == null) {
            /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "&c Null Entity");
            /*HDA*/ActuallySizeInteractions.LogHDA(false, getClass(), "HDE", "Verifying");
            return false; }

        if (stackLocation != null && holdPoint == null) {
            /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "&c Null Slot");
            /*HDA*/ActuallySizeInteractions.LogHDA(false, getClass(), "HDE", "Verifying");
            return false; }

        // We actually don't care that much about the item and holder. Done
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "&2 VERIFIED");
        /*HDA*/ActuallySizeInteractions.LogHDA(false, getClass(), "HDE", "Verifying");
        return true;
    }

    @Override
    public boolean isAllowed() {
        /*HDA*/ActuallySizeInteractions.LogHDA(true, getClass(), "HDE", "Allowing");
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "Clientside &f {0}", (entityCounterpart == null ? "null" : entityCounterpart.level().isClientSide));
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "Slot &e {0}", (stackLocation == null ? "null" : stackLocation));
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "Hold Point &b {0}", holdPoint);
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "Item &b {0}", (itemCounterpart == null ? "null" : itemCounterpart.getDisplayName().getString()));
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "Entity &f {0}", (entityCounterpart == null ? "null" : entityCounterpart.getScoreboardName()));

        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "&6 ALLOWED");
        /*HDA*/ActuallySizeInteractions.LogHDA(false, getClass(), "HDE", "Allowing");
        
        // This is not really cancellable
        return true;
    }

    @Override
    public void resolve() {
        /*HDA*/ActuallySizeInteractions.LogHDA(true, getClass(), "HDE", "Resolving");
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "Clientside &f {0}", (entityCounterpart == null ? "null" : entityCounterpart.level().isClientSide));
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "Slot &e {0}", (stackLocation == null ? "null" : stackLocation));
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "Hold Point &b {0}", holdPoint);
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "Item &b {0}", (itemCounterpart == null ? "null" : itemCounterpart.getDisplayName().getString()));
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "Entity &f {0}", (entityCounterpart == null ? "null" : entityCounterpart.getScoreboardName()));
        
        // Should have been confirmed during verification step but
        if (entityCounterpart == null) {
            /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "&3 No entity to deactivate");
            /*HDA*/ActuallySizeInteractions.LogHDA(false, getClass(), "HDE", "Resolving");
            return; }

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
            /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "Deactivation Event Broadcast");
            ASIPSDeactivateItemEntityEvent broadcast = new ASIPSDeactivateItemEntityEvent(stackLocation, entityCounterpart);
            MinecraftForge.EVENT_BUS.post(broadcast);
        }

        /*
         * #2   Unregistering the entity counterpart.
         */
        if (entityDuality != null) {
            /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "Unregistered Entity");
            entityDuality.actuallysize$setItemEntityHolder(null);
            entityDuality.actuallysize$setItemCounterpart(null);
            entityDuality.actuallysize$setItemStackLocation(null);
            entityDuality.actuallysize$setHoldPoint(null);
        }

        /*
         * #3   Unregistering from the item counterpart
         */
        if (itemDuality != null) {
            /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "Unregistered Item");
            itemDuality.actuallysize$setItemEntityHolder(null);
            itemDuality.actuallysize$setEntityCounterpart(null);
            itemDuality.actuallysize$setItemStackLocation(null);
        }

        /*
         * #4   Unregistering from the holder
         */
        if (dualityHolder != null) {
            /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "Unregistered Holder");
            dualityHolder.actuallysize$setHeldItemEntityDuality(holdPoint, null);
        }

        /*
         * #5   Remove the item (optional)
         *
         * --> But only in the server-side <--
         */
        if (isServer) {

            // Delete item
            if (itemCounterpart != null && andRemoveItem) { itemCounterpart.setCount(0); }

            /*
             * #6 Unregister from Item-Entity duality kitty
             */
            /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "Unregistered Server");
            ASIPickupSystemManager.unregisterActiveEntityCounterpart(entityCounterpart);

            /*
             * #7   Sync it over the network
             */

            // Create packet to send over the network and send to those tracking this entity
            /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "&f Network Packet Sent");
            ASINCItemEntityEscapePacket packet = new ASINCItemEntityEscapePacket(entityCounterpart);
            ASINetworkManager.broadcastEntityUpdate(entityCounterpart, packet);
        }

        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "HDE", "&3 REDIRECTED");
        /*HDA*/ActuallySizeInteractions.LogHDA(false, getClass(), "HDE", "Resolving");
    }
}
