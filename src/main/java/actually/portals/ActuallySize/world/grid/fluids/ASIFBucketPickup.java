package actually.portals.ActuallySize.world.grid.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import org.jetbrains.annotations.NotNull;

/**
 * A block that can be picked up by a bucket
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public abstract class ASIFBucketPickup extends ASIWorldFluid {

    /**
     * The block that can be picked up with a bucket
     *
     * @since 1.0.0
     */
    @NotNull final BucketPickup bucketPickup;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public BucketPickup getBucketPickup() { return bucketPickup; }

    /**
     * @param state The Block State
     * @param pos   The position in the world
     * @param world The world
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIFBucketPickup(@NotNull BlockState state, @NotNull BlockPos pos, @NotNull Level world) {
        super(state, pos, world);
        bucketPickup = (BucketPickup) state.getBlock();
    }

    /**
     * @param pos   The position in the world
     * @param world The world
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIFBucketPickup(@NotNull BlockPos pos, @NotNull Level world) {
        this(world.getBlockState(pos), pos, world);
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public int dry(int units) {

        // Cant dry if not drying enough
        if (units < FluidType.BUCKET_VOLUME) { return 0; }

        // Dry this block by picking it up
        ItemStack picked = getBucketPickup().pickupBlock(getWorld(), getPos(), getState());

        // No result? We are done
        if (picked.isEmpty()) { return 0; }

        // Undo and cancel if picked up result has no fluid capability
        FluidStack obtained = (new FluidBucketWrapper(picked)).getFluid();
        if (obtained.isEmpty()) {
            getWorld().setBlock(getPos(), getState(), Block.UPDATE_ALL_IMMEDIATE);
            return 0; }

        // Return the amount of fluid that was removed
        return FluidType.BUCKET_VOLUME;
    }
}
