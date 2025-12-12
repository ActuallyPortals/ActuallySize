package actually.portals.ActuallySize.world.grid.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.wrappers.BlockWrapper;
import org.jetbrains.annotations.NotNull;

public class ASIFNormalBlock extends ASIFFluidHandler {

    /**
     * @param state The Block State
     * @param pos   The position in the world
     * @param world The world
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIFNormalBlock(@NotNull BlockState state, @NotNull BlockPos pos, @NotNull Level world) {
        super(state, pos, world, new BlockWrapper(state, world, pos));
    }

    /**
     * @param pos   The position in the world
     * @param world The world
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIFNormalBlock(@NotNull BlockPos pos, @NotNull Level world) {
        this(world.getBlockState(pos), pos, world);
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public boolean hasFluidCapabilities() {

        // Solid blocks don't actually have capabilities
        if (getState().isAir()) { return true; }
        if (getState().isSolid()) { return false; }

        // Whatever the parent thinks
        return super.hasFluidCapabilities();
    }

    @Override
    public int fill(int units, @NotNull Fluid fluid) {

        // Run whatever has to run
        int filled = super.fill(units, fluid);

        // Any amount of filling will replace this block with water
        if (filled > 0) {
            FluidUtil.destroyBlockOnFluidPlacement(getWorld(), getPos());
            getWorld().setBlock(getPos(), fluid.defaultFluidState().createLegacyBlock(), Block.UPDATE_ALL_IMMEDIATE); }

        // Return result
        return filled;
    }
}
