package actually.portals.ActuallySize.pickup.mixininterfaces;

import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * He who holds an Item that is linked to an Entity existing in the world.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface ItemEntityDualityHolder {

    /**
     * @return The active Item-Entities held by this Duality Holder
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull Map<ASIPSHoldPoint, EntityDualityCounterpart> actuallysize$getHeldEntityDualities();

    /**
     * The aim of this method is to register when an item was put in
     * this slot, it will take care of removing the previous Entity
     * of the item-entity in here and spawning the new one.
     *
     * @param slot What slot this item was put in.
     *
     * @param dualityEntity The entity that also represents an item
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$setHeldItemEntityDuality(@NotNull ASIPSHoldPoint slot, @Nullable EntityDualityCounterpart dualityEntity);
    
    /**
     * @return The Item-Entity in this slot, if there is one
     *
     * @param slot What slot this Item-Entity was put in.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable EntityDualityCounterpart actuallysize$getHeldItemEntityDuality(@NotNull ASIPSHoldPoint slot);

    /**
     * @return If 'who' is held by this holder, or held by an entity held by this holder.
     *
     * @param who The entity that you want to check
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    boolean actuallysize$isIndirectlyHolding(@NotNull EntityDualityCounterpart who);

    /**
     * Releases all the active item-entity dualities out into the world using
     * the {@link actually.portals.ActuallySize.pickup.actions.ASIPSDualityEscapeAction}
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$escapeAllDualities();

    /**
     * Returns all active item-entity dualities to their items in the inventory
     * the {@link actually.portals.ActuallySize.pickup.actions.ASIPSDualityDeactivationAction}
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$deactivateAllDualities();

    /**
     * @param index An object of any sort (EquipmentSlot, ItemStack location)
     *              that represents somewhere in the Entity's domain/inventory where
     *              the Item counterpart of the Item-Entity duality is located
     *
     * @return The hold point this entity has for the specified index
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable ASIPSHoldPoint actuallysize$getHoldPoint(@Nullable Object index);

    /**
     * @return The world where the holder currently exists.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull Level actuallysize$getHolderWorld();
}
