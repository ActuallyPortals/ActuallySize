package actually.portals.ActuallySize.world.grid.events;

import actually.portals.ActuallySize.world.grid.ASIBeegBlock;
import actually.portals.ActuallySize.world.grid.ASIWorldBlock;
import actually.portals.ActuallySize.world.grid.fluids.ASIWorldFluid;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * An event that drains or fills fluid blocks
 * in a beeg block domain, and sometimes, its
 * vicinity
 *
 * @author Actually Portals
 * @since 1.0.0
 */
public class ASIBeegBucketEvent extends Event implements ASIBeegBlockEvent {

    /**
     * The beeg block that is being changed
     *
     * @since 1.0.0
     */
    @NotNull final ASIBeegBlock beegBlock;

    /**
     * Blocks that will be changed. Excludes the one block that started
     * this all, since this event is triggered after that bucket event.
     *
     * @since 1.0.0
     */
    @NotNull final ArrayList<ASIWorldFluid> toDrain;

    /**
     * The block that was first changed so that the Beeg Drain was triggered.
     *
     * @since 1.0.0
     */
    @Nullable final ASIWorldBlock originalDrain;

    /**
     * Player doing the beeg breaking
     *
     * @since 1.0.0
     */
    @NotNull final ServerPlayer player;

    /**
     * The fluid that is being changed, or {@link net.minecraft.world.level.material.Fluids#EMPTY}
     * if multiple / all fluids are being changed.
     *
     * @since 1.0.0
     */
    @NotNull final Fluid fluid;

    /**
     * The total units of fluid that will be changed,
     * excluding those from the original changed block
     *
     * @since 1.0.0
     */
    int totalFluidUnits;

    /**
     * The bucket item when it was empty
     *
     * @since 1.0.0
     */
    @NotNull final ItemStack emptyBucketItem;

    /**
     * The bucket item when it is full
     *
     * @since 1.0.0
     */
    @NotNull final ItemStack filledBucketItem;

    /**
     * @param beegBlock              The beeg block that is being drained
     * @param toDrain                Blocks that will be drained, excluding original drain
     * @param originalDrain          The block that was first drained
     * @param player                 Player doing the beeg draining
     * @param fluid              The fluid being drained, EMPTY if all fluids
     * @param emptyBucketItem    The bucket before going into this
     * @param filledBucketItem   The bucket after going into this
     * @param totalFluidUnits    The total number of fluid units expected to be drained
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIBeegBucketEvent(
            @NotNull ASIBeegBlock beegBlock,
            @NotNull ArrayList<ASIWorldFluid> toDrain,
            @Nullable ASIWorldBlock originalDrain,
            @NotNull ServerPlayer player,
            @NotNull Fluid fluid,
            @NotNull ItemStack emptyBucketItem,
            @NotNull ItemStack filledBucketItem,
            int totalFluidUnits) {
        this.beegBlock = beegBlock;
        this.toDrain = toDrain;
        this.originalDrain = originalDrain;
        this.player = player;
        this.fluid = fluid;
        this.emptyBucketItem = emptyBucketItem;
        this.filledBucketItem = filledBucketItem;
        this.totalFluidUnits = totalFluidUnits;
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public @NotNull ServerPlayer getPlayer() { return player; }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public @Nullable ASIWorldBlock getOriginalDrain() { return originalDrain; }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public @NotNull ArrayList<ASIWorldFluid> getToDrain() { return toDrain; }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public @NotNull ASIBeegBlock getBeegBlock() { return beegBlock; }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public @NotNull ItemStack getFilledBucketItem() {
        return filledBucketItem;
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public @NotNull ItemStack getEmptyBucketItem() {
        return emptyBucketItem;
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public void setTotalFluidUnits(int totalFluidUnits) {
        this.totalFluidUnits = totalFluidUnits;
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public int getTotalFluidUnits() {
        return totalFluidUnits;
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public @NotNull Fluid getFluid() {
        return fluid;
    }
}
