package actually.portals.ActuallySize.pickup.events;

import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * This is called when the Entity counterpart of an
 * Item-Entity is about to be removed. Presumably
 * because the ItemStack was put away or something.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIPSDeactivateItemEntityEvent extends ASIPSItemEntityDualityEvent {

    /**
     * @param itemCounterpart   The item in is location in the holder's inventory
     * @param entityCounterpart The entity of this Item-Entity duality
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSDeactivateItemEntityEvent(@NotNull ItemStackLocation<? extends Entity> itemCounterpart, @NotNull Entity entityCounterpart) {
        super(itemCounterpart, entityCounterpart);
    }
}
