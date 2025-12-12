package actually.portals.ActuallySize.world.grid;

import actually.portals.ActuallySize.world.grid.fluids.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.wrappers.BlockWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidBlockWrapper;
import org.jetbrains.annotations.NotNull;

/**
 * A BlockState with a BlockPos in a World
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIWorldBlock {

    /**
     * The Block State
     *
     * @since 1.0.0
     */
    @NotNull final BlockState state;

    /**
     * The position in the world
     *
     * @since 1.0.0
     */
    @NotNull final BlockPos pos;

    /**
     * The world
     *
     * @since 1.0.0
     */
    @NotNull final Level world;

    /**
     * @param state The block state
     * @param pos The position in the world
     * @param world The world
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIWorldBlock(@NotNull BlockState state, @NotNull BlockPos pos, @NotNull Level world) {
        this.state = state;
        this.pos = pos;
        this.world = world;
    }

    /**
     * @param pos The position in the world
     * @param world The world
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIWorldBlock(@NotNull BlockPos pos, @NotNull Level world) {
        this(world.getBlockState(pos), pos, world);
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @NotNull Level getWorld() { return world; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @NotNull BlockPos getPos() { return pos; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @NotNull BlockState getState() { return state; }

    /**
     * @return The best way to handle fluids in this block
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull ASIWorldFluid toFluid() {
        Block block = getState().getBlock();

        if (block instanceof SimpleWaterloggedBlock) {
            return new ASIFWaterloggedBlock(getState(), getPos(), getWorld()); }
        if (block instanceof LiquidBlockContainer) {
            return new ASIFLiquidBlockContainer(getState(), getPos(), getWorld()); }
        if (block instanceof IFluidBlock) {
            return new ASIFFluidHandler(getState(), getPos(), getWorld(),
                    new FluidBlockWrapper((IFluidBlock) block, getWorld(), getPos())); }
        if (block instanceof LiquidBlock) { return new ASIFLiquidBlock(getState(), getPos(), getWorld()); }

        return new ASIFNormalBlock(getState(), getPos(), getWorld());
    }
}
