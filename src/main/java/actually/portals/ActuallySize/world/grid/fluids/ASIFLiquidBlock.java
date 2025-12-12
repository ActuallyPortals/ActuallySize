package actually.portals.ActuallySize.world.grid.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

/**
 * An actual liquid block
 *
 * @author Actually Portals
 * @since 1.0.0
 */
public class ASIFLiquidBlock extends ASIFBucketPickup {

    /**
     * The block that can be picked up with a bucket
     *
     * @since 1.0.0
     */
    @NotNull final LiquidBlock liquidBlock;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public LiquidBlock getLiquidBlock() { return liquidBlock; }

    /**
     * @param state The Block State
     * @param pos   The position in the world
     * @param world The world
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIFLiquidBlock(@NotNull BlockState state, @NotNull BlockPos pos, @NotNull Level world) {
        super(state, pos, world);
        liquidBlock = (LiquidBlock) state.getBlock();
    }

    /**
     * @param pos   The position in the world
     * @param world The world
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIFLiquidBlock(@NotNull BlockPos pos, @NotNull Level world) {
        this(world.getBlockState(pos), pos, world);
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override public boolean hasFluidCapabilities() { return true; }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override public @NotNull Fluid getFluid() { return getLiquidBlock().getFluid().getSource(); }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override public int getFluidAmount() { return getLiquidBlock().getFluidState(getState()).isSource() ? FluidType.BUCKET_VOLUME : 0; }

    @Override
    public int dry(int units) {
        int dried = super.dry(units);

        // When drying failed... maybe it is an incomplete fluid block that we WANT to dry
        if (dried == 0 && units > 0) {

            // Check the fluid state, it has to exist
            FluidState fluidState = getLiquidBlock().getFluidState(getState());
            if (fluidState.isEmpty()) { return dried; }

            // Source blocks SHOULD have worked, if they didn't, respect it
            if (fluidState.isSource()) { return dried; }

            // Existing amount? Dry it
            if (fluidState.getAmount() > 0) {
                getWorld().setBlock(getPos(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
            }
        }

        return dried;
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public int fill(int units, @NotNull Fluid fluid) {

        // Cant fill if not filling enough
        if (units < FluidType.BUCKET_VOLUME) { return 0; }

        // Fill
        getWorld().setBlock(getPos(), fluid.defaultFluidState().createLegacyBlock(), Block.UPDATE_ALL_IMMEDIATE);

        // Return the amount of fluid that was added
        return FluidType.BUCKET_VOLUME;
    }
}
