package actually.portals.ActuallySize.world.grid.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

/**
 * For specifically waterlogged blocks, special because these
 * dry as Bucket Pickup but fill as Liquid Block Containers
 *
 * @author Actually Portals
 * @since 1.0.0
 */
public class ASIFWaterloggedBlock extends ASIFBucketPickup {

    /**
     * The fluid handler for this block
     *
     * @since 1.0.0
     */
    @NotNull final SimpleWaterloggedBlock waterloggedBlock;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public SimpleWaterloggedBlock getWaterloggedBlock() { return waterloggedBlock; }

    /**
     * This ASI World Fluid but as a ASIF Bucket Pickup object
     *
     * @since 1.0.0
     */
    @NotNull final ASIFLiquidBlockContainer asLiquidBlock;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public ASIFLiquidBlockContainer getAsLiquidBlock() { return asLiquidBlock; }

    /**
     * @param state The Block State
     * @param pos   The position in the world
     * @param world The world
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIFWaterloggedBlock(@NotNull BlockState state, @NotNull BlockPos pos, @NotNull Level world) {
        super(state, pos, world);
        waterloggedBlock = (SimpleWaterloggedBlock) state.getBlock();
        asLiquidBlock = new ASIFLiquidBlockContainer(state, pos, world);
    }

    /**
     * @param pos   The position in the world
     * @param world The world
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIFWaterloggedBlock(@NotNull BlockPos pos, @NotNull Level world) {
        this(world.getBlockState(pos), pos, world);
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public boolean hasFluidCapabilities() { return true; }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override public @NotNull Fluid getFluid() { return getAsLiquidBlock().getFluid(); }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override public int getFluidAmount() { return getState().getFluidState().isEmpty() ? 0 : FluidType.BUCKET_VOLUME; }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override public int fill(int units, @NotNull Fluid fluid) {
        return getWaterloggedBlock().placeLiquid(getWorld(), getPos(), getState(), fluid.defaultFluidState()) ? FluidType.BUCKET_VOLUME : 0;
    }
}
