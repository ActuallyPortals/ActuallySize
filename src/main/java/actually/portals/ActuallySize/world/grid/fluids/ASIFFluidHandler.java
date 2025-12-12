package actually.portals.ActuallySize.world.grid.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

/**
 * A class for blocks that are handled through wrappers
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIFFluidHandler extends ASIWorldFluid {

    /**
     * The tank being accessed
     *
     * @since 1.0.0
     */
    int accessTank = 0;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public int getAccessTank() { return accessTank; }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public void setAccessTank(int accessTank) {
        if (accessTank < 0) { accessTank = 0; }
        this.accessTank = accessTank;
    }

    /**
     * The fluid handler for this block
     *
     * @since 1.0.0
     */
    @NotNull final IFluidHandler handler;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public IFluidHandler getHandler() { return handler; }

    /**
     * @param state The Block State
     * @param pos   The position in the world
     * @param world The world
     * @param handler The handler to be used
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIFFluidHandler(@NotNull BlockState state, @NotNull BlockPos pos, @NotNull Level world, @NotNull IFluidHandler handler) {
        super(state, pos, world);
        this.handler = handler;
    }

    /**
     * @param pos   The position in the world
     * @param world The world
     * @param handler The handler to be used
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIFFluidHandler(@NotNull BlockPos pos, @NotNull Level world, @NotNull IFluidHandler handler) {
        this(world.getBlockState(pos), pos, world, handler);
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public boolean hasFluidCapabilities() { return getHandler().getTanks() > accessTank; }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    @NotNull public Fluid getFluid() { return hasFluidCapabilities() ? getFluidStack().getFluid() : Fluids.EMPTY; }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    @NotNull public FluidStack getFluidStack() { return hasFluidCapabilities() ? getHandler().getFluidInTank(accessTank) : FluidStack.EMPTY; }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public int getFluidAmount() { return hasFluidCapabilities() ? getFluidStack().getAmount() : 0; }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public int getMaximumFluidAmount() { return hasFluidCapabilities() ? getHandler().getTankCapacity(accessTank) : 0; }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public int dry(int units) {

        // Return the amount of fluid that was removed
        FluidStack stack = getHandler().drain(units, IFluidHandler.FluidAction.EXECUTE);
        return stack.isEmpty() ? 0 : stack.getAmount();
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public int fill(int units, @NotNull Fluid fluid) {

        // Return the amount of fluid that was filled
        return getHandler().fill(new FluidStack(fluid, units), IFluidHandler.FluidAction.EXECUTE);
    }
}
