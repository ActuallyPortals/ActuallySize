package actually.portals.ActuallySize.world.blocks;

import actually.portals.ActuallySize.world.ASIWorldSystemManager;
import actually.portals.ActuallySize.world.grid.ASIWorldBlock;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Essentially a copy of the Light Block
 *
 * @author Actually Portals
 * @since 1.0.0
 */
public class BeegLightBlock extends LightBlock implements SimpleBeegBlock {

    /**
     * @since 1.0.0
     */
    public static final IntegerProperty SCALE = ASIWorldSystemManager.BEEG_SCALE;

    /**
     * @since 1.0.0
     */
    public static final IntegerProperty SPREAD = ASIWorldSystemManager.BEEG_SPREAD;

    /**
     * @since 1.0.0
     */
    public static final IntegerProperty SPREADING = ASIWorldSystemManager.BEEG_SPREADING;

    /**
     * Tick delay between light propagation
     *
     * @since 1.0.0
     */
    public static int LIGHT_PROPAGATION_TICK = 2;

    /**
     * The last light level that will be spread
     *
     * @since 1.0.0
     */
    public static int MINIMUM_LIGHT_SPREAD = 3;

    /**
     * The desisted light level that blocks light spread
     *
     * @since 1.0.0
     */
    public static int BLOCK_LIGHT_SPREAD = 1;

    /**
     * The desisted light level that allows light spread
     *
     * @since 1.0.0
     */
    public static int ALLOW_LIGHT_SPREAD = 0;

    /**
     * The spreading value in which light blocks spread
     *
     * @since 1.0.0
     */
    public static int SPREADING_YES = 1;

    /**
     * The spreading value in which light blocks do not spread
     *
     * @since 1.0.0
     */
    public static int SPREADING_NO = 0;

    /**
     * The spreading value in which light blocks may not
     * be updated by nearby blocks to begin spreading
     *
     * @since 1.0.0
     */
    public static int SPREADING_FROZEN = 2;

    /**
     * The spreading value in which light blocks may not
     * be updated by nearby blocks to begin spreading
     *
     * @since 1.0.0
     */
    public static final int LIGHT_BLOCK_SILENT = Block.UPDATE_KNOWN_SHAPE;

    /**
     * The spreading value in which light blocks may not
     * be updated by nearby blocks to begin spreading
     *
     * @since 1.0.0
     */
    public static final int LIGHT_BLOCK_CLIENTS = Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_CLIENTS;

    /**
     * Essentially a copy of the Light Block
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public BeegLightBlock(@NotNull Properties pProperties) {
        super(pProperties);
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SCALE, SPREAD, SPREADING);
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return context.isHoldingItem(ASIWorldSystemManager.BEEG_LIGHT_BLOCK.getAsItem().get()) ? Shapes.block() : Shapes.empty();
    }

    /**
     * Causes this light to propagate in all directions
     * that have less light and where a light block may
     * be placed.
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public void spread(@NotNull ASIWorldBlock tickBlock, @NotNull ServerLevel world, int myLightLevel, int mySpread, int mySpreading, int myScale) {

        // List the blocks to which we will spread
        ArrayList<ASIWorldBlock> spill = new ArrayList<>();

        // I have now spread
        BlockState spreadState = tickBlock.getState().setValue(SPREADING, SPREADING_NO);
        world.setBlock(tickBlock.getPos(), spreadState, LIGHT_BLOCK_SILENT);
        if (myLightLevel < MINIMUM_LIGHT_SPREAD) { return; }

        // For every direction
        for(Direction direction : Direction.values()) {
            BlockPos blockpos = tickBlock.getPos().relative(direction);
            BlockState state = world.getBlockState(blockpos);

            // Air blocks are immediately spread onto
            if (canSpreadTo(state, myLightLevel, mySpread, mySpreading, myScale, false)) {
                spill.add(new ASIWorldBlock(state, blockpos, world)); }
        }

        // Nowhere to spread? End
        if (spill.isEmpty()) { return; }

        // Calculate Light and Spread
        int lightLevel = myLightLevel;
        int spreadLevel = mySpread - 1;
        if (spreadLevel <= 0) { spreadLevel = myScale; lightLevel--; }
        if (lightLevel < MINIMUM_LIGHT_SPREAD) { return; }

        // Well then, spill
        BlockState spillState = defaultBlockState()
                .setValue(LightBlock.LEVEL, lightLevel)
                .setValue(SCALE, myScale)
                .setValue(SPREAD, spreadLevel)
                .setValue(SPREADING, BeegLightBlock.SPREADING_YES);
        for (ASIWorldBlock spread : spill) {
            world.setBlock(spread.getPos(), spillState, LIGHT_BLOCK_CLIENTS);
            world.scheduleTick(spread.getPos(), this, LIGHT_PROPAGATION_TICK); }

    }

    /**
     * @param state The block state to spread to
     *
     * @param myLightLevel The light level of this light block
     * @param myScale The scale of this light block
     * @param mySpread The spread of this light block
     * @param mySpreading The spreading state of this light block
     *
     * @param isSource If this light is emitted from a source block that may
     *                 forcibly fill areas where light is fading
     *
     * @return If this light may spread to this other block
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public static boolean canSpreadTo(@NotNull BlockState state, int myLightLevel, int mySpread, int mySpreading, int myScale, boolean isSource) {

        // Air blocks are immediately spread onto
        if (isAirForReal(state)) { return true; }

        // The only other block we can spread into, is Light Blocks. Read their light level.
        if (!(state.getBlock() instanceof LightBlock)) { return false; }
        int level = state.getValue(LightBlock.LEVEL);
        if (level > myLightLevel) { return false; }

        // Specific light level of Beeg Light block will block all light spread
        if (!isSource && state.getBlock() instanceof BeegLightBlock) {

            // If the light level is too low for a Beeg Light Block, then it is a DESISTED light block
            if (level < MINIMUM_LIGHT_SPREAD) { return level == ALLOW_LIGHT_SPREAD; } }

        // For normal light blocks, any lower light level allows spreading
        if (!(state.getBlock() instanceof BeegLightBlock)) { return level < myLightLevel; }

        // If this block is my spill, we do not spread.
        // We kinda already did no so point in redoing it.
        if (isMySpill(state, myLightLevel, mySpread, mySpreading, myScale)) { return false; }

        // Lower light level can always spread
        if (level < myLightLevel) { return true; }

        // Okay now read its spread value
        return state.getValue(SPREAD) < mySpread;
    }

    /**
     * @param state The block state you are testing
     *
     * @return If this is air, other than a Beeg Light Block.
     *         For the purposes of Beeg Light Blocks, Beeg
     *         Light Blocks are NOT air.
     */
    public static boolean isAirForReal(@NotNull BlockState state) { return !(state.getBlock() instanceof BeegLightBlock) && state.isAir(); }

    /**
     * Removes this light if there is no source feeding it
     *
     * @return true if the light block was removed due to having no light source
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public boolean desist(@NotNull ASIWorldBlock tickBlock, @NotNull ServerLevel world, int myLightLevel, int mySpread, int mySpreading, int myScale) {

        // Final desist
        if (myLightLevel < MINIMUM_LIGHT_SPREAD) { world.setBlock(tickBlock.getPos(), Blocks.AIR.defaultBlockState(), LIGHT_BLOCK_CLIENTS); return true; }

        // Find sources to sixteen blocks
        ASIWorldBlock source = deepFindMySource(tickBlock, world, myLightLevel, mySpread, mySpreading, myScale, 16);
        if (source != null) { return false; }

        // Source was not found. Desist
        ArrayList<ASIWorldBlock> spill = findMySpill(tickBlock, world, myLightLevel, mySpread, mySpreading, myScale);

        // Not a single source was found, delete this
        BlockState desistState = defaultBlockState().setValue(LightBlock.LEVEL, BLOCK_LIGHT_SPREAD).setValue(SPREADING, SPREADING_FROZEN).setValue(SCALE, 1);
        world.setBlock(tickBlock.getPos(), desistState, LIGHT_BLOCK_CLIENTS);
        world.scheduleTick(tickBlock.getPos(), this, randomPropagationTick() * 2);
        for (ASIWorldBlock spread : spill) {
            world.setBlock(spread.getPos(), spread.getState().setValue(SPREADING, SPREADING_FROZEN), LIGHT_BLOCK_SILENT);
            world.scheduleTick(spread.getPos(), this, randomPropagationTick() * 2);
        }
        return true;
    }

    @Nullable public static ASIWorldBlock deepFindMySource(@NotNull ASIWorldBlock tickBlock, @NotNull ServerLevel world, int myLightLevel, int mySpread, int mySpreading, int myScale, int iterations) {
        if (iterations < 1) { return null; }

        // First iteration
        ASIWorldBlock source = findMySource(tickBlock, world, myLightLevel, mySpread, mySpreading, myScale);
        if (source == null) { return null; }

        // Iterate to find source
        for (int i = 2; i <= iterations; i ++) {

            // Reached the root? Default success
            if (source.getState().getBlock() instanceof BeegLightSource) { return source; }

            // Go one level deeper
            source = findMySource(source, world, source.getState().getValue(LEVEL), source.getState().getValue(SPREAD), source.getState().getValue(SPREADING), myScale);

            // The chain was broken? Failure
            if (source == null) { return null; }
        }

        // This is the result
        return source;
    }

    /**
     * @param state The light block being checked
     * @param myLightLevel The light level of this light block
     * @param myScale The scale of this light block
     * @param mySpread The spread of this light block
     * @param mySpreading The spreading state of this light block
     *
     * @return If the target block could have spawned me
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public static boolean isMySource(@NotNull BlockState state, int myLightLevel, int mySpread, int mySpreading, int myScale) {
        if (!(state.getBlock() instanceof SimpleBeegBlock)) { return false; }
        int scale = state.getValue(SCALE);
        if (!(scale == myScale)) { return false; }

        // Special case for beeg light is a beeg light source itself
        if (state.getBlock() instanceof BeegLightSource) {
            int level = ((BeegLightSource) state.getBlock()).getLight();
            return mySpread == myScale && level == myLightLevel; }

        // Not a beeg light block, not my source
        if (!(state.getBlock() instanceof BeegLightBlock)) { return false; }

        // Identify the other light block
        int level = state.getValue(LightBlock.LEVEL);
        int spread = state.getValue(SPREAD);

        // Special case when I am the first of my light level
        if (mySpread == myScale) {
            return (level == myLightLevel + 1) && (spread == 1);

        // Otherwise, they must be in the same light level but 1 spread before
        } else {
            return (level == myLightLevel) && (spread == mySpread + 1);
        }
    }

    /**
     * @param state The light block being checked
     * @param myLightLevel The light level of this light block
     * @param myScale The scale of this light block
     * @param mySpread The spread of this light block
     * @param mySpreading The spreading state of this light block
     *
     * @return If I could have spawned the target block
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public static boolean isMySpill(@NotNull BlockState state, int myLightLevel, int mySpread, int mySpreading, int myScale) {
        if (!(state.getBlock() instanceof BeegLightBlock)) { return false; }
        int scale = state.getValue(SCALE);
        if (!(scale == myScale)) { return false; }

        // Identify the other light block
        int level = state.getValue(LightBlock.LEVEL);
        int spread = state.getValue(SPREAD);

        // Special case when I am the last of my light level
        if (mySpread == 1) {
            return (level + 1 == myLightLevel) && (spread == myScale);

        // Otherwise, they must be in the same light level but 1 spread before
        } else {
            return (level == myLightLevel) && (spread + 1 == mySpread);
        }
    }

    /**
     * @param tickBlock The light block being evaluated
     * @param world The world where it is being ticked
     * @param myLightLevel The light level of this light block
     * @param myScale The scale of this light block
     * @param mySpread The spread of this light block
     * @param mySpreading The spreading state of this light block
     *
     * @return The block that sourced this light block
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @Nullable public static ASIWorldBlock findMySource(@NotNull ASIWorldBlock tickBlock, @NotNull ServerLevel world, int myLightLevel, int mySpread, int mySpreading, int myScale) {

        // Check every adjacent direction
        for (Direction direction : Direction.values()) {

            // Check this adjacent block
            BlockPos blockpos = tickBlock.getPos().relative(direction);
            BlockState state = world.getBlockState(blockpos);
            if (isAirForReal(state)) { continue; }

            // Is this my source? Done
            if (isMySource(state, myLightLevel, mySpread, mySpreading, myScale)) {
                return new ASIWorldBlock(state, blockpos, world); }
        }

        // Source not found
        return null;
    }

    /**
     * @param tickBlock The light block being evaluated
     * @param world The world where it is being ticked
     * @param myLightLevel The light level of this light block
     * @param myScale The scale of this light block
     * @param mySpread The spread of this light block
     * @param mySpreading The spreading state of this light block
     *
     * @return The block that sourced this light block
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public static ArrayList<ASIWorldBlock> findMySpill(@NotNull ASIWorldBlock tickBlock, @NotNull ServerLevel world, int myLightLevel, int mySpread, int mySpreading, int myScale) {

        // Check every adjacent direction
        ArrayList<ASIWorldBlock> ret = new ArrayList<>();
        for (Direction direction : Direction.values()) {

            // Check this adjacent block
            BlockPos blockpos = tickBlock.getPos().relative(direction);
            BlockState state = world.getBlockState(blockpos);
            if (isAirForReal(state)) { continue; }

            // Is this my spill? Collect
            if (isMySpill(state, myLightLevel, mySpread, mySpreading, myScale)) {
                ret.add(new ASIWorldBlock(state, blockpos, world)); }
        }

        // Done
        return ret;
    }

    @NotNull public static String logLight(@NotNull BlockState lightBlock) {
        if (!(lightBlock.getBlock() instanceof BeegLightBlock)) { return lightBlock.getBlock().getClass().getSimpleName(); }
        return "[L=" + lightBlock.getValue(LEVEL) + ", P=" + lightBlock.getValue(SPREAD) + ", Z=" + lightBlock.getValue(SPREADING) + "]"; }

    /**
     * Called for this light source to update
     * itself and delete or spread as needed
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    void lightTick(@NotNull ASIWorldBlock worldBlock) {
        if (worldBlock.getWorld().isClientSide) { return; }
        if (!(worldBlock.getState().getBlock() instanceof BeegLightBlock)) { return; }

        // Identify metrics
        ServerLevel world = (ServerLevel) worldBlock.getWorld();
        int myLightLevel = worldBlock.getState().getValue(LightBlock.LEVEL);
        int mySpread = worldBlock.getState().getValue(SPREAD);
        int mySpreading = worldBlock.getState().getValue(SPREADING);
        int myScale = worldBlock.getState().getValue(SCALE);

        // Desist if standalone
        if (desist(worldBlock, world, myLightLevel, mySpread, mySpreading, myScale)) { return; }
        if (myLightLevel < MINIMUM_LIGHT_SPREAD) { return; }

        // Spread if spreading
        if (mySpreading == SPREADING_YES) { spread(worldBlock, world, myLightLevel, mySpread, mySpreading, myScale); }
        if (mySpreading == SPREADING_FROZEN) { world.setBlock(worldBlock.getPos(), worldBlock.getState().setValue(SPREADING, SPREADING_NO), LIGHT_BLOCK_SILENT); }
    }

    /**
     * Adds a random effect to light spread so it doesn't look
     * too procedural and maybe looks a little more organic
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    int randomPropagationTick() {
        if (OotilityNumbers.rollSuccess(0.2)) { return LIGHT_PROPAGATION_TICK; }
        if (OotilityNumbers.rollSuccess(0.6)) { return LIGHT_PROPAGATION_TICK * 2; }
        return LIGHT_PROPAGATION_TICK * 3;
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public boolean canPlaceLiquid(@NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, @NotNull Fluid pFluid) {

        // All liquids may flow into this block
        return true;
    }

    @Override
    public boolean placeLiquid(@NotNull LevelAccessor pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, @NotNull FluidState pFluidState) {
        pLevel.setBlock(pPos, pFluidState.createLegacyBlock(), Block.UPDATE_ALL);
        pLevel.scheduleTick(pPos, pFluidState.getType(), pFluidState.getType().getTickDelay(pLevel));
        return true;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        if (!pLevel.isClientSide && !pPlayer.canUseGameMasterBlocks()) { return InteractionResult.PASS; }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public void tick(@NotNull BlockState pState, @NotNull ServerLevel pLevel, @NotNull BlockPos pPos, @NotNull RandomSource pRandom) {
        super.tick(pState, pLevel, pPos, pRandom);
        lightTick(new ASIWorldBlock(pState, pPos, pLevel));
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState pState, @NotNull Direction pDirection, @NotNull BlockState pNeighborState, @NotNull LevelAccessor pLevel, @NotNull BlockPos pPos, @NotNull BlockPos pNeighborPos) {
        //BLB//"&8 =========== SHAPING ["+ pPos.toShortString() + "] " + logLight(pState) + " by " + logLight(pNeighborState) +  " ===========");

        // Light blocks do not cause SPREADING or updates
        if (pNeighborState.getBlock() instanceof BeegLightBlock || pLevel.isClientSide()) {
            //BLB//"&8 =========== NONE: LIGHT ===========");
            return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
        }

        int myLightLevel = pState.getValue(LEVEL);
        if (myLightLevel < MINIMUM_LIGHT_SPREAD) {
            //BLB//"&8 =========== NONE: OFF LIGHT ===========");
            return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
        }

        // It can only spread again once replaced, if it has reached state 2, or is already spreading
        int mySpreading = pState.getValue(SPREADING);
        if (mySpreading == BeegLightBlock.SPREADING_FROZEN) {
            //BLB//"&8 =========== NONE: FROZEN ===========");
            return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
        }

        // Tick
        pLevel.scheduleTick(pPos, this, randomPropagationTick());

        // Only the [NO] state may be changed to [YES] via this method
        if (mySpreading != BeegLightBlock.SPREADING_NO) {
            //BLB//"&8 =========== NONE: SPREADING ===========");
            return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
        }

        //BLB//"&8 =========== &2 SPREAD ENABLED &7 ===========");
        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos).setValue(SPREADING, BeegLightBlock.SPREADING_YES);
    }
}
