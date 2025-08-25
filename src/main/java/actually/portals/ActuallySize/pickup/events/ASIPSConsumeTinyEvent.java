package actually.portals.ActuallySize.pickup.events;

import actually.portals.ActuallySize.pickup.consumption.ASIPSPlayerConsumption;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;

/**
 * Event ran when a player item-entity item is consumed.
 * Based on this result, certain settings will happen.
 * <br><br>
 * <b>Only runs in the server-side.</b>
 *
 * @since 1.0.0
 * @author Actually Portals
 */
@Cancelable
public class ASIPSConsumeTinyEvent extends PlayerEvent {

    /**
     * The player doing the consuming
     *
     * @since 1.0.0
     */
    @NotNull final ServerPlayer holder;

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
     * @param player The player being consumed
     * @param holder The player doing the consuming
     * @param itemCounterpart The item that represents this player
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSConsumeTinyEvent(@NotNull ServerPlayer holder, @NotNull ServerPlayer player, @NotNull ItemStack itemCounterpart, @NotNull ASIPSPlayerConsumption consumer) {
        super(player);
        this.itemCounterpart = itemCounterpart;
        this.holder = holder;
        this.consumer = consumer;
    }

    /**
     * What happens upon completion of this event?
     *
     * @since 1.0.0
     */
    @NotNull ASIPSPlayerConsumption consumer;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public ASIPSPlayerConsumption getConsumer() { return consumer; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public void setConsumer(@NotNull ASIPSPlayerConsumption eat) { consumer = eat; }
}
