package actually.portals.ActuallySize.world.grid.events;

import actually.portals.ActuallySize.world.grid.ASIBeegBlock;
import actually.portals.ActuallySize.world.grid.ASIWorldBlock;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * An event fired when multiple blocks are broken due to beeg break
 *
 * @since 1.0.0
 * @author Actually Portals
 */
@Cancelable
public class ASIBeegBreakEvent extends Event implements ASIBeegBlockEvent {

    /**
     * The beeg block that is being broken
     *
     * @since 1.0.0
     */
    @NotNull final ASIBeegBlock beegBlock;

    /**
     * Blocks that will be destroyed. Excludes the one block that started this all, since
     * this event is triggered after that break event.
     *
     * @since 1.0.0
     */
    @NotNull final ArrayList<ASIWorldBlock> toDestroy;

    /**
     * The block that was first broken so that the Beeg Break was triggered.
     *
     * @since 1.0.0
     */
    @Nullable final ASIWorldBlock originalDestroy;

    /**
     * Player doing the beeg breaking
     *
     * @since 1.0.0
     */
    @NotNull final ServerPlayer player;

    /**
     * The damage that we expect the tool in the beeg's hand to take, by default the number
     * of blocks that will be mined reduced by the square of the scale of the beeg.
     *
     * @since 1.0.0
     */
    int expectedDurabilityDamage;

    /**
     * @param beegBlock The beeg block that is being broken
     * @param toDestroy Blocks that will be destroyed, excluding original destroy
     * @param originalDestroy The block that was first broken
     * @param player Player doing the beeg breaking
     * @param expectedDurabilityDamage The damage that we expect the tool in the beeg's hand to take
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIBeegBreakEvent(
            @NotNull ASIBeegBlock beegBlock,
            @NotNull ArrayList<ASIWorldBlock> toDestroy,
            @Nullable ASIWorldBlock originalDestroy,
            @NotNull ServerPlayer player,
            int expectedDurabilityDamage) {
        this.beegBlock = beegBlock;
        this.toDestroy = toDestroy;
        this.originalDestroy = originalDestroy;
        this.player = player;
        this.expectedDurabilityDamage = expectedDurabilityDamage;
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @NotNull ServerPlayer getPlayer() {
        return player;
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @Nullable ASIWorldBlock getOriginalDestroy() {
        return originalDestroy;
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @NotNull ArrayList<ASIWorldBlock> getToDestroy() {
        return toDestroy;
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public @NotNull ASIBeegBlock getBeegBlock() {
        return beegBlock;
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public int getExpectedDurabilityDamage() {
        return expectedDurabilityDamage;
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public void setExpectedDurabilityDamage(int expectedDurabilityDamage) {
        this.expectedDurabilityDamage = expectedDurabilityDamage;
    }

    /**
     * @return The tool this player will be mining these blocks with
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public ItemStack getTool() { return getPlayer().getMainHandItem(); }
}
