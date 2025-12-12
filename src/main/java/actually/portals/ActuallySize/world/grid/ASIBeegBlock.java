package actually.portals.ActuallySize.world.grid;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.world.grid.construction.ASIGConstructor;
import actually.portals.ActuallySize.world.grid.construction.cube.ASIGCShelled;
import actually.portals.ActuallySize.world.grid.events.ASIBeegBreakEvent;
import actually.portals.ActuallySize.world.grid.events.ASIBeegDrainEvent;
import actually.portals.ActuallySize.world.grid.events.ASIBeegFillEvent;
import actually.portals.ActuallySize.world.grid.events.ASIBeegPlaceEvent;
import actually.portals.ActuallySize.world.grid.fluids.ASIWorldFluid;
import actually.portals.ActuallySize.world.mixininterfaces.BeegBreaker;
import actually.portals.ActuallySize.world.mixininterfaces.DitzDestroyer;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * This refers to a block that belongs to a beeg grid,
 * in essence allows beegs to build and break blocks
 * their size.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIBeegBlock {

    /**
     * The scale of this beeg block
     *
     * @since 1.0.0
     */
    final int scale;

    /**
     * The position of this beeg block in the beeg grid
     *
     * @since 1.0.0
     */
    @NotNull final Vec3i beegPos;

    /**
     * @since 1.0.0
     */
    public @NotNull Vec3i getBeegPos() { return beegPos; }

    /**
     * @since 1.0.0
     */
    public int getScale() { return scale; }

    /**
     * @param scale The scale of this beeg block
     * @param beegX The X position of this beeg block in the beeg grid
     * @param beegY The Y position of this beeg block in the beeg grid
     * @param beegZ The Z position of this beeg block in the beeg grid
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIBeegBlock(int scale, int beegX, int beegY, int beegZ) {
        this(scale, new Vec3i(beegX, beegY, beegZ));
    }

    /**
     * @param scale The scale of this beeg block
     * @param beegPos The position of this beeg block in the beeg grid
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIBeegBlock(int scale, @NotNull Vec3i beegPos) {
        this.scale = scale;
        this.beegPos = beegPos;
    }

    /**
     * @param scale The scale of the beeg block you seek, it gets rounded-up to an integer
     * @param worldPos The world position you have, in normal-sized block units
     *
     * @return The beeg block containing this world position
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public static ASIBeegBlock containing(double scale, @NotNull Vec3 worldPos) {

        // Ceil scale
        int S = OotilityNumbers.ceil(scale);

        // Sense world position
        int sx = worldPos.x >= 0 ? 1 : -1;
        int sy = worldPos.y >= 0 ? 1 : -1;
        int sz = worldPos.z >= 0 ? 1 : -1;

        // Floor world position
        int x = OotilityNumbers.floor(worldPos.x * sx);
        int y = OotilityNumbers.floor(worldPos.y * sy);
        int z = OotilityNumbers.floor(worldPos.z * sz);

        if (sx < 0) { x += S; }
        if (sy < 0) { y += S; }
        if (sz < 0) { z += S; }

        // Transform to beeg grid
        int beegX = (x - (x % S)) / S;
        int beegY = (y - (y % S)) / S;
        int beegZ = (z - (z % S)) / S;

        // Done
         return new ASIBeegBlock(S, beegX * sx, beegY * sy, beegZ * sz);
    }

    /**
     * @return The maximum world X encompassed by this beeg block, exclusive.
     *         Essentially, it encloses up to this number minus 0.00000000001
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public int maxX() { return minX() + getScale(); }

    /**
     * @return The minimum world X encompassed by this beeg block, inclusive.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public int minX() { return getScale() * getBeegPos().getX(); }
    /**
     * @return The maximum world Y encompassed by this beeg block, exclusive.
     *         Essentially, it encloses up to this number minus 0.00000000001
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public int maxY() { return minY() + getScale(); }

    /**
     * @return The minimum world Y encompassed by this beeg block, inclusive.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public int minY() { return getScale() * getBeegPos().getY(); }

    /**
     * @return The maximum world Z encompassed by this beeg block, exclusive.
     *         Essentially, it encloses up to this number minus 0.00000000001
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public int maxZ() { return minZ() + getScale(); }

    /**
     * @return The minimum world Z encompassed by this beeg block, inclusive.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public int minZ() { return getScale() * getBeegPos().getZ(); }

    /**
     * @return A constructor that spans this beeg block
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public ASIGConstructor getConstructor() {
        if (constructor == null) { constructor = new ASIGCShelled(getScale(), new Vec3(minX(), minY(), minZ())); }
        return constructor;
    }

    /**
     * @param acceptReplace When true, liquids and grass and other
     *                      blocks you can replace while building will
     *                      be considered empty.
     *
     * @return true if there are no solid blocks, with block placing in mind
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public boolean isEmpty(@NotNull ServerLevel world, boolean acceptReplace, @Nullable BlockSnapshot ignored) {
        boolean ign = ignored != null;

        // First failure ends method
        for (int x = minX(); x < maxX(); x++) {
            for (int y = minY(); y < maxY(); y++) {
                for (int z = minZ(); z < maxZ(); z++) {
                    if (ign) { if (ignored.getPos().getX() == x && ignored.getPos().getY() == y && ignored.getPos().getZ() == z) { continue; } }

                    // Find block at this coordinate
                    BlockState at = world.getBlockState(BlockPos.containing(x + 0.2D, y + 0.2D, z + 0.2D));
                    if (at.isAir()) { continue; }
                    if (acceptReplace && at.canBeReplaced()) { continue; }

                    // Obstruction found
                    return false;
                }
            }
        }

        // No obstruction found
        return true;
    }

    /**
     * @param dir The direction in which to move
     *
     * @return The beeg block adjacent to this
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull ASIBeegBlock getAdjacent(@NotNull Direction dir) {
        return new ASIBeegBlock(getScale(), getBeegPos().relative(dir));
    }

    /**
     * A constructor that spans this beeg block
     *
     * @since 1.0.0
     */
    @Nullable ASIGConstructor constructor;

    /**
     * This will send out a proper event and cancel if cancelable,
     * as well as testing each individual block for a place event
     *
     * @param results The array to be filled
     * @param input The block snapshot being placed, will be reverted on failure
     * @param block The block-state being placed
     * @param against The direction in which this block was placed
     * @param world The world in which this block was placed
     * @param counts The number of items consumed to place this block
     *
     * @return If the block was successfully placed down
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public boolean tryBeegBuild(@NotNull List<BlockSnapshot> results, @NotNull BlockSnapshot input, @NotNull BlockState block, @Nullable Direction against, @NotNull Entity placer, @NotNull ServerLevel world, int counts) {

        // When empty, use this chunk
        if (isEmpty(world, true, input)) {

            // Get limit from player inventory
            int limit;
            if (counts < 1) { limit = 2048; } else { limit = counts * getScale() * getScale(); }

            // Prepare indices
            ASIGConstructor constructor = getConstructor();
            ArrayList<Vec3> indices = constructor.elaborate(0, limit);

            // Track changes
            world.captureBlockSnapshots = true;
            for (Vec3 index : indices) {
                world.setBlock(BlockPos.containing(index), block, 3); }
            world.captureBlockSnapshots = false;

            // Compound results
            results.addAll(world.capturedBlockSnapshots);
            world.capturedBlockSnapshots.clear();
            boolean ret = false;
            if (!results.isEmpty()) {
                BlockSnapshot snap = results.get(0);
                BlockState placedAgainst = world.getBlockState(snap.getPos().relative((against == null ? Direction.UP : against).getOpposite()));
                ASIBeegPlaceEvent event = new ASIBeegPlaceEvent(results, placedAgainst, placer, this);
                ret = !MinecraftForge.EVENT_BUS.post(event); }

            // Event failed, undo input
            if (!ret) {
                world.restoringBlockSnapshots = true;
                input.restore(true, false);
                world.restoringBlockSnapshots = false; }
            return ret;

        // Else, try to move directionally
        } else if (against != null) {
            boolean ret = getAdjacent(against).tryBeegBuild(results, input, block,null, placer, world, counts);

            // Input is undone no matter what
            world.restoringBlockSnapshots = true;
            input.restore(true, false);
            world.restoringBlockSnapshots = false;

            return ret;
        // Fail
        } else {
            return false;
        }
    }

    /**
     * This will send out a proper event and cancel if cancelable,
     * as well as testing each individual block for a break event
     *
     * @param original The block break that triggered the Beeg Break
     * @param player Player that beeg broke this beeg block
     * @param world The world where it happened
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void tryBeegBreak(@Nullable ASIWorldBlock original, @NotNull ServerPlayer player, @NotNull ServerLevel world) {

        // Identify original block position
        BlockPos input = original == null ? null : original.getPos();
        boolean ign = input != null;

        // Identify tool
        ItemStack withHeld = player.getMainHandItem();
        boolean ditz = withHeld.getItem() instanceof DitzDestroyer;
        boolean tool = withHeld.isDamageableItem();
        int nonInstantMines = 0;

        // Build list of mined blocks
        ArrayList<ASIWorldBlock> toDestroy = new ArrayList<>();
        for (int x = minX(); x < maxX(); x++) {
            for (int y = minY(); y < maxY(); y++) {
                for (int z = minZ(); z < maxZ(); z++) {

                    // Input block was already broken anyway
                    if (ign) { if (input.getX() == x && input.getY() == y && input.getZ() == z) { continue; } }

                    // Find target block
                    BlockPos target = BlockPos.containing(x + 0.2D, y + 0.2D, z + 0.2D);
                    BlockState state = world.getBlockState(target);
                    if (state.isAir()) { continue; }

                    // Check tool to be the correct one
                    if (ditz) { if (!((DitzDestroyer) withHeld.getItem()).actuallysize$canDitzDestroy(withHeld, state)) { continue; } }
                    if (tool) { if (state.getDestroySpeed(world, target) > 0) { nonInstantMines++; } }

                    // Break it with this players' authority
                    ASIWorldBlock block = new ASIWorldBlock(state, target, world);
                    toDestroy.add(block);
                }
            }
        }

        // Reduce damage taken by scale squared. Might even reach zero, but one durability damage may be taken
        // from breaking the original block event that will always go through independently.
        if (nonInstantMines > 0) {
            double scale = ASIUtilities.getEntityScale(player);
            double buff = 1D / scale;
            nonInstantMines = OotilityNumbers.floor(nonInstantMines * buff * buff); }

        // Run Beeg Break Event, cancel if cancelled
        ASIBeegBreakEvent event = new ASIBeegBreakEvent(this, toDestroy, original, player, nonInstantMines);
        if (MinecraftForge.EVENT_BUS.post(event)) { return; }

        // Begin beeg breaking
        ItemStack beeg = event.getTool();
        BeegBreaker breaker = ((BeegBreaker) (Object) beeg);
        try {
            breaker.actuallysize$setBeegBreaking(true);

            // Break those blocks
            for (ASIWorldBlock des : toDestroy) { player.gameMode.destroyBlock(des.getPos()); }
        } finally { breaker.actuallysize$setBeegBreaking(false); }

        // Finalize beeg breaking
        beeg.hurtAndBreak(event.getExpectedDurabilityDamage(), player, who -> who.broadcastBreakEvent(EquipmentSlot.MAINHAND));
    }

    /**
     * This will send out a proper event and cancel if cancelable
     *
     * @param original The bucket drain that triggered the Beeg Drain
     * @param player Player that beeg drained this beeg block
     * @param world The world where it happened
     * @param match The fluid to drain, if {@link Fluids#EMPTY} this will drain EVERY FLUID
     * @param emptyBucket The bucket before going in to this
     * @param filledBucket The bucket after going in to this
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void tryBeegDrain(@Nullable ASIWorldBlock original,
                             @NotNull ServerPlayer player,
                             @NotNull ServerLevel world,
                             @NotNull Fluid match,
                             @NotNull ItemStack emptyBucket,
                             @NotNull ItemStack filledBucket) {

        // Identify original block position
        BlockPos input = original == null ? null : original.getPos();
        boolean ign = input != null;
        boolean mch = match != Fluids.EMPTY;
        int fluidTotal = 0;

        // Build list of mined blocks
        ArrayList<ASIWorldFluid> toDrain = new ArrayList<>();
        for (int x = minX(); x < maxX(); x++) {
            for (int y = minY(); y < maxY(); y++) {
                for (int z = minZ(); z < maxZ(); z++) {

                    // Input block will already be drained
                    if (ign) { if (input.getX() == x && input.getY() == y && input.getZ() == z) { continue; } }

                    // Find target block
                    BlockPos target = BlockPos.containing(x + 0.2D, y + 0.2D, z + 0.2D);
                    BlockState state = world.getBlockState(target);
                    ASIWorldFluid block = new ASIWorldBlock(state, target, world).toFluid();

                    // We only care about blocks that can handle fluid
                    if (!block.hasFluidCapabilities()) { continue; }

                    // Check tool to be the correct one
                    if (mch) { if (!block.getFluid().isSame(match)) { continue; } }

                    // Drain it with this players' authority
                    toDrain.add(block);
                    fluidTotal += block.getFluidAmount();
                }
            }
        }
        int maxDrain = getScale() * getScale() * getScale() * FluidType.BUCKET_VOLUME;

        // Run Beeg Break Event, cancel if cancelled
        ASIBeegDrainEvent event = new ASIBeegDrainEvent(this, toDrain, original, player, match, emptyBucket, filledBucket, fluidTotal);
        if (MinecraftForge.EVENT_BUS.post(event)) { return; }

        // Begin beeg draining
        BeegBreaker breaker = ((BeegBreaker) (Object) emptyBucket);
        try {
            breaker.actuallysize$setBeegBreaking(true);
            for (ASIWorldFluid des : toDrain) { maxDrain -= des.dry(maxDrain); }

        } finally { breaker.actuallysize$setBeegBreaking(false); }
    }

    /**
     * This will send out a proper event and cancel if cancelable
     *
     * @param original The bucket drain that triggered the Beeg Fill
     * @param player Player that beeg filled this beeg block
     * @param world The world where it happened
     * @param pour The fluid to fill empty space with
     * @param emptyBucket The bucket after going in to this
     * @param filledBucket The bucket before going in to this
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void tryBeegFill(@Nullable ASIWorldBlock original,
                             @NotNull ServerPlayer player,
                             @NotNull ServerLevel world,
                             @NotNull Fluid pour,
                             @NotNull ItemStack emptyBucket,
                             @NotNull ItemStack filledBucket) {

        // Nothing to fill when filling empty
        if (pour == Fluids.EMPTY) { return; }

        // Identify original block position
        BlockPos input = original == null ? null : original.getPos();
        boolean ign = input != null;
        int fluidTotal = 0;

        // Build list of mined blocks
        ArrayList<ASIWorldFluid> toFill = new ArrayList<>();
        for (int x = minX(); x < maxX(); x++) {
            for (int y = minY(); y < maxY(); y++) {
                for (int z = minZ(); z < maxZ(); z++) {

                    // Input block will already be drained
                    if (ign) { if (input.getX() == x && input.getY() == y && input.getZ() == z) { continue; } }

                    // Find target block
                    BlockPos target = BlockPos.containing(x + 0.2D, y + 0.2D, z + 0.2D);
                    BlockState state = world.getBlockState(target);
                    ASIWorldFluid block = new ASIWorldBlock(state, target, world).toFluid();

                    // We only care about blocks that can handle fluid
                    if (!block.hasFluidCapabilities()) { continue;  }

                    // If there is already fluid there, it has to be the same we are pouring
                    if (block.getFluidAmount() > 0) { if (!block.getFluid().isSame(pour)) { continue; }}

                    // Fill it with this players' authority
                    toFill.add(block);
                    fluidTotal += block.getMaximumFluidAmount() - block.getFluidAmount();
                }
            }
        }
        int maxFill = getScale() * getScale() * getScale() * FluidType.BUCKET_VOLUME;

        // Run Beeg Break Event, cancel if cancelled
        ASIBeegFillEvent event = new ASIBeegFillEvent(this, toFill, original, player, pour, emptyBucket, filledBucket, fluidTotal);
        if (MinecraftForge.EVENT_BUS.post(event)) { return; }

        // Begin beeg draining
        BeegBreaker breaker = ((BeegBreaker) (Object) emptyBucket);
        try {
            breaker.actuallysize$setBeegBreaking(true);
            for (ASIWorldFluid des : toFill) { maxFill -= des.fill(maxFill, pour); }

        } finally { breaker.actuallysize$setBeegBreaking(false); }
    }
}
