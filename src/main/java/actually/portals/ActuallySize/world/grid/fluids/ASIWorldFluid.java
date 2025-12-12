package actually.portals.ActuallySize.world.grid.fluids;

import actually.portals.ActuallySize.world.grid.ASIWorldBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

/**
 * There seems to be a ton of different ways for fluids
 * to be contained or drained and its best if I just
 * keep everything in one place damn
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public abstract class ASIWorldFluid extends ASIWorldBlock {

    /**
     * @param state The Block State
     * @param pos   The position in the world
     * @param world The world
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIWorldFluid(@NotNull BlockState state, @NotNull BlockPos pos, @NotNull Level world) {
        super(state, pos, world);
    }

    /**
     * @param pos   The position in the world
     * @param world The world
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIWorldFluid(@NotNull BlockPos pos, @NotNull Level world) {
        this(world.getBlockState(pos), pos, world);
    }

    /**
     * @return If this block supports fluid-related mechanics
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public abstract boolean hasFluidCapabilities();

    /**
     * @return The fluid type in this world block
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public abstract Fluid getFluid();

    /**
     * @return The fluid stack in this world
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public FluidStack getFluidStack() { return new FluidStack(getFluid(), getFluidAmount()); }

    /**
     * @return The fluid amount in this world block
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public abstract int getFluidAmount();

    /**
     * @return The maximum fluid amount this world block can hold
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public int getMaximumFluidAmount() { return hasFluidCapabilities() ? FluidType.BUCKET_VOLUME : 0; }

    /**
     * Removes from any and all liquid from this block,
     * or at least, the liquid that would be obtained
     * when calling {@link #getFluidStack()}.
     *
     * @param units The maximum units of fluid to dry
     *
     * @return The units that were successfully dried
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public abstract int dry(int units);

    /**
     * Fills this block by these units, returning
     * the remainder that did not fit.
     *
     * @param fluid The fluid to fill this with
     * @param units The maximum units of fluid to fill
     *
     * @return The units that were successfully filled
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public abstract int fill(int units, @NotNull Fluid fluid);
}
