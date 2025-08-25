package actually.portals.ActuallySize.pickup.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;

/**
 * Event ran when an edible item-entity item is consumed.
 * Based on this result, certain settings will happen.
 * Most notably, if it is a non-player, it results in
 * the entity being removed.
 * <br><br>
 * <b>Only runs in the server-side.</b> Cancelling
 * will prevent the item from being consumed, but
 * players will still be able to run the animation.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
@Cancelable
public class ASIPSConsumeMobEvent extends EntityEvent {

    /**
     * The player doing the consuming
     *
     * @since 1.0.0
     */
    @NotNull
    final ServerPlayer holder;

    /**
     * The item that represents the player being consumed
     *
     * @since 1.0.0
     */
    @NotNull final ItemStack itemCounterpart;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public ItemStack getItemCounterpart() { return itemCounterpart; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public ServerPlayer getHolder() { return holder; }

    /**
     * @param entity The entity being consumed
     * @param holder The entity doing the consuming
     * @param itemCounterpart The item that is being consumed
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSConsumeMobEvent(@NotNull ServerPlayer holder, @NotNull Entity entity, @NotNull ItemStack itemCounterpart) {
        super(entity);
        this.holder = holder;
        this.itemCounterpart = itemCounterpart;
    }

    /**
     * For non-players only, if they will be consumed and deleted by ASI.
     * The alternative is that nothing will happen btw, the Item Duality
     * will probably be broken tho since the act of consuming an item
     * decreases its count by 1 (turning it into AIR).
     *
     * @since 1.0.0
     */
    boolean consumeMob = true;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public boolean isConsumeMob() { return consumeMob; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public void setConsumeMob(boolean consume) { consumeMob = consume; }
}
