package actually.portals.ActuallySize.pickup.mixininterfaces;

import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Entities spawned while they have an item in existence
 * that represent them are not to be saved in chunks, and
 * if they are saved in chunks, there must be ways to
 * identify them to delete them later.
 * <p>
 * Furthermore, they must be synced server to players to
 * tell them they belong to an ItemStack somewhere.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface EntityDualityCounterpart {

    /**
     * A shortcut to call the {@link actually.portals.ActuallySize.pickup.actions.ASIPSDualityEscapeAction}
     * on this entity, letting it be released from its hold point. This shouldn't really do anything if this
     * is not being held.
     *
     * @see #actuallysize$getHoldPoint()
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$escapeDuality();

    /**
     * A shortcut to call the {@link actually.portals.ActuallySize.pickup.actions.ASIPSDualityDeactivationAction}
     * on this entity, letting it be returned to its item. This shouldn't really do anything if this
     * is not being held.
     *
     * @see #actuallysize$getHoldPoint()
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$deactivateDuality();

    /**
     * @return If this entity has a hold point of the kind that behaves
     *         like a ride point that turns the held entity into a pseudo
     *         passenger (as far as local player authority and tick order
     *         are concerned).
     *
     * @see #actuallysize$getHoldPoint()
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    boolean actuallysize$isHeld();

    /**
     * @return The place relative to the holder that this
     *         entity will be. Basically a ride point for
     *         held entities.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable ASIPSHoldPoint actuallysize$getHoldPoint();

    /**
     * @param point The place relative to the holder that this
     *              entity will be. Basically a ride point for
     *              held entities.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$setHoldPoint(@Nullable ASIPSHoldPoint point);

    /**
     * @return The tags that identify an Item-Entity duality Entity
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull CompoundTag actuallysize$getEntityDualityTags();

    /**
     * @return The entity that holds this ItemStack in their hand (or inventory for the of players)
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable ItemEntityDualityHolder actuallysize$getItemEntityHolder();

    /**
     * @return The ultimate entity holding this entity, which might be the
     *         holder's holder or the entity itself if it has no holder.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull ItemEntityDualityHolder actuallysize$getRootDualityHolder();

    /**
     * @param who The entity that holds this ItemStack in their hand (or inventory for the of players)
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$setItemEntityHolder(@Nullable ItemEntityDualityHolder who);

    /**
     * @return The ItemStack this entity is linked to, or itself if this is the item
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable ItemStack actuallysize$getItemCounterpart();

    /**
     * @param who The ItemStack this entity is linked to, or itself if this is the item
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$setItemCounterpart(@Nullable ItemStack who);

    /**
     * @return In the holder's inventory, where is this item stack?
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable ItemStackLocation<? extends Entity> actuallysize$getItemStackLocation();

    /**
     * @param who In the holder's inventory, where is this item stack?
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$setItemStackLocation(@Nullable ItemStackLocation<? extends Entity> who);

    /**
     * This being true necessarily means the hold point AND holder are not null.
     *
     * @return If this ItemStack/Entity has its ItemStack/Entity counterpart somewhere.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    boolean actuallysize$isActive();
}
