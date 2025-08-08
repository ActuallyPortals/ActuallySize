package actually.portals.ActuallySize.pickup.events;

import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * This is called when the Entity counterpart of an
 * Item-Entity is activated. In the serverside, this
 * happens right before it actually spawns. In the
 * client-side, it happens right after registering
 * the Entity-Item-Holder links
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIPSActivateItemEntityEvent extends ASIPSItemEntityDualityEvent {

    /**
     * @param itemCounterpart   The item in is location in the holder's inventory
     * @param entityCounterpart The entity of this Item-Entity duality
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSActivateItemEntityEvent(@NotNull ItemStackLocation<? extends Entity> itemCounterpart, @NotNull Entity entityCounterpart) {
        super(itemCounterpart, entityCounterpart);
    }
}
