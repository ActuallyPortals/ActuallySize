package actually.portals.ActuallySize.pickup.events;

import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * ASI Picked-up entities exist both inside inventory and outside
 * simultaneously, but only while held in your hand. These events
 * deal with situations involving both the ItemStack and the Entity.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIPSItemEntityDualityEvent extends EntityEvent {

    /**
     * The entity holding the item that represents the entity about
     * to be removed because this holder stopped holding the item.
     *
     * @since 1.0.0
     */
    @NotNull ItemEntityDualityHolder itemEntityHolder;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public ItemEntityDualityHolder getItemEntityHolder() { return itemEntityHolder; }

    /**
     * The item counterpart of this Item-Entity in the holder's inventory.
     *
     * @since 1.0.0
     */
    @NotNull ItemStack itemCounterpart;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public ItemStack getItemCounterpart() { return itemCounterpart; }

    /**
     * @return The entity counterpart of this Item-Entity. To be removed.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public Entity getEntityCounterpart() { return getEntity(); }

    /**
     * The location of the ItemStack in  the holder
     *
     * @since 1.0.0
     */
    @NotNull final ItemStackLocation<? extends Entity> stackLocation;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @NotNull ItemStackLocation<? extends Entity> getStackLocation() { return stackLocation; }

    /**
     * @param stackLocation The item in is location in the holder's inventory
     * @param entityCounterpart The entity of this Item-Entity duality
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSItemEntityDualityEvent(@NotNull ItemStackLocation<? extends Entity> stackLocation, @NotNull Entity entityCounterpart) {
        super(entityCounterpart);
        itemEntityHolder = (ItemEntityDualityHolder) stackLocation.getHolder();
        ItemStack item = stackLocation.getItemStack();
        this.itemCounterpart = item == null ? ItemStack.EMPTY : item;
        this.stackLocation = stackLocation;
    }
}
