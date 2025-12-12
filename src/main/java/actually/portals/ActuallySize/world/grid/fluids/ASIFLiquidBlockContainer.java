package actually.portals.ActuallySize.world.grid.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.wrappers.BlockWrapper;
import org.jetbrains.annotations.NotNull;

/**
 * For specifically liquid block containers
 *
 * @author Actually Portals
 * @since 1.0.0
 */
public class ASIFLiquidBlockContainer extends ASIFFluidHandler {

    /**
     * The fluid handler for this block
     *
     * @since 1.0.0
     */
    @NotNull
    final LiquidBlockContainer liquidBlockContainer;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public LiquidBlockContainer getLiquidBlockContainer() { return liquidBlockContainer; }

    /**
     * @param state The Block State
     * @param pos   The position in the world
     * @param world The world
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIFLiquidBlockContainer(@NotNull BlockState state, @NotNull BlockPos pos, @NotNull Level world) {
        super(state, pos, world, new BlockWrapper.LiquidContainerBlockWrapper((LiquidBlockContainer) state.getBlock(), world, pos));
        liquidBlockContainer = (LiquidBlockContainer) state.getBlock();
    }

    /**
     * @param pos   The position in the world
     * @param world The world
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIFLiquidBlockContainer(@NotNull BlockPos pos, @NotNull Level world) {
        this(world.getBlockState(pos), pos, world);
    }

    @Override
    public @NotNull FluidStack getFluidStack() {

        // For Liquid Block Containers, it seems the Fluid State is used
        FluidStack fromHandler = super.getFluidStack();
        if (!fromHandler.isEmpty()) { return fromHandler; }
        return new FluidStack(getState().getFluidState().getType(), FluidType.BUCKET_VOLUME);
    }

    @Override
    public int dry(int units) {

        // Preferentially use its handler
        if (units <= 0) { return 0; }
        int fromHandler = super.dry(units);
        if (fromHandler > 0) { return fromHandler; }

        // Need a bucket of dryness
        if (units < FluidType.BUCKET_VOLUME) { return 0; }

        // Otherwise, we break the block there was just no other way :valor pray:
        getWorld().setBlock(getPos(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
        return FluidType.BUCKET_VOLUME;
    }

    @Override
    public int fill(int units, @NotNull Fluid fluid) {

        // Cancel if the Liquid Block Container does not support this liquid
        if (!getLiquidBlockContainer().canPlaceLiquid(getWorld(), getPos(), getState(), fluid)) { return 0; }

        // Carry on
        return super.fill(units, fluid);
    }
}
