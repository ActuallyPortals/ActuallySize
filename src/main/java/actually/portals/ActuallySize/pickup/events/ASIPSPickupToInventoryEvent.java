package actually.portals.ActuallySize.pickup.events;

import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;

/**
 * An event fired when an entity is picked up using only one hand...
 * or rather, one inventory slot. This way it is possible to pickup
 * tinies directly to the offhand, or any other inventory slot
 *
 * @since 1.0.0
 * @author Actually Portals
 */
@Cancelable
public class ASIPSPickupToInventoryEvent extends ASIPSEntityPickupEntityEvent {

    /**
     * The slot where this picked up entity will be put into as an item
     *
     * @since 1.0.0
     */
    @NotNull ItemStackLocation<? extends Entity> slot;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public ItemStackLocation<? extends Entity> getSlot() { return slot; }

    /**
     * An event fired when the ASI plugin detects that
     * some entity wants to pick up something else.
     *
     * @param tiny The entity being picked up
     * @param beeg The entity doing the picking up
     * @param slot The slot where the picked-up entity will be stored
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSPickupToInventoryEvent(@NotNull Entity beeg, @NotNull Entity tiny, @NotNull ItemStackLocation<? extends Entity> slot) {
        super(beeg, tiny);
        this.slot = slot;
    }
}
