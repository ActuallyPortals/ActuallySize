package actually.portals.ActuallySize.pickup.events;

import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;

/**
 * This is called in the server-side when the Entity counterpart
 * of an Item-Entity is about to be spawned. Presumably
 * because the ItemStack was selected to hold in hand.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
@Cancelable
public class ASIPSTryActivateItemEntityEvent extends ASIPSItemEntityDualityEvent {

    /**
     * @param itemCounterpart   The item in is location in the holder's inventory
     * @param entityCounterpart The entity of this Item-Entity duality
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSTryActivateItemEntityEvent(@NotNull ItemStackLocation<? extends Entity> itemCounterpart, @NotNull Entity entityCounterpart) {
        super(itemCounterpart, entityCounterpart);
    }
}
