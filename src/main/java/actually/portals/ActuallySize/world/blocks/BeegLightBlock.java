package actually.portals.ActuallySize.world.blocks;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.world.ASIWorldSystemManager;
import actually.portals.ActuallySize.world.grid.ASIWorldBlock;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
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
    public void spread(@NotNull ASIWorldBlock spreadingLightBlock) {

        // List the blocks to which we will spread
        ArrayList<ASIWorldBlock> spill = new ArrayList<>();

        // Identify
        BlockPos pos = spreadingLightBlock.getPos();
        Level world = spreadingLightBlock.getWorld();
        int myLightLevel = spreadingLightBlock.getState().getValue(LightBlock.LEVEL);
        int myScale = spreadingLightBlock.getState().getValue(SCALE);
        int mySpread = spreadingLightBlock.getState().getValue(SPREAD);

        // I have now spread
        BlockState spreadState = spreadingLightBlock.getState().setValue(SPREADING, SPREADING_NO);
        //BLB//"LIGHT ["+ spreadingLightBlock.getPos().toShortString() + "] &3 Spread from " + logLight(spreadingLightBlock.getState()) + " -> " + logLight(spreadState));
        world.setBlock(spreadingLightBlock.getPos(), spreadState, LIGHT_BLOCK_SILENT);
        if (myLightLevel < MINIMUM_LIGHT_SPREAD) { return; }

        // For every direction
        for(Direction direction : Direction.values()) {
            BlockPos blockpos = pos.relative(direction);
            BlockState state = world.getBlockState(blockpos);

            // Air blocks are immediately spread onto
            if (canSpreadTo(state, myLightLevel, myScale, mySpread, false)) {
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
            //BLB//"LIGHT ["+ spread.getPos().toShortString() + "] &b Spreading ticking <" + spread.getPos().toShortString() + "> " + logLight(spread.getState()) + " -> " + logLight(spillState));
            world.setBlock(spread.getPos(), spillState, LIGHT_BLOCK_CLIENTS);
            world.scheduleTick(spread.getPos(), this, LIGHT_PROPAGATION_TICK);
            //world.updateNeighborsAt(spread.getPos(), this);
        }

    }

    /**
     * @param state The block state to spread to
     *
     * @param myLightLevel Light level of block trying to spread
     * @param myScale Scale of block trying to spread
     * @param mySpread Spread level of block trying to spread
     *
     * @param isSource If this light is emitted from a source block that may
     *                 fill areas where light is fading
     *
     * @return If this light may spread to this other block
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public static boolean canSpreadTo(@NotNull BlockState state, int myLightLevel, int myScale, int mySpread, boolean isSource) {

        // Air blocks are immediately spread onto
        if (state.isAir()) { return true; }

        // Other blocks cannot be spread into
        if (!(state.getBlock() instanceof LightBlock)) { return false; }
        int level = state.getValue(LightBlock.LEVEL);

        // If light is trying to spread itself
        if (!isSource && state.getBlock() instanceof BeegLightBlock) {

            // If the light level is too low for a Beeg Light Block, then it is a DESISTED light block
            if (level < MINIMUM_LIGHT_SPREAD) { return level == ALLOW_LIGHT_SPREAD; }
        }

        // Light blocks must emit less light
        if (mySpread <= 1) { level++; } // If this is the last block of this light level, the other's level must be equalized
        if (level > myLightLevel) { return false; }
        if (!(state.getBlock() instanceof BeegLightBlock)) { return true; }

        // If I still have higher light level, automatically true can spread
        if (level < myLightLevel) { return true; }

        // I cannot spread upstream or at the same light level
        int spread = state.getValue(SPREAD);
        if (spread >= mySpread) { return false; }

        // And also, if I already spread there... no point in spreading again,
        // so spread + 1 = the target must have much lower spread than me
        return spread + 1 < mySpread;
    }

    /**
     * Removes this light if there is no source feeding it
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public boolean desist(@NotNull ASIWorldBlock desistingBlock) {

        // Identify
        BlockPos pos = desistingBlock.getPos();
        Level world = desistingBlock.getWorld();
        int myLightLevel = desistingBlock.getState().getValue(LightBlock.LEVEL);
        int mySpread = desistingBlock.getState().getValue(SPREAD);
        int myScale = desistingBlock.getState().getValue(SCALE);

        // Final desist
        if (myLightLevel < MINIMUM_LIGHT_SPREAD) {
            //BLB//"LIGHT ["+ desistingBlock.getPos().toShortString() + "] &e Desisting from " + logLight(desistingBlock.getState()) + " -> AIR");
            //world.setBlock(desistingBlock.getPos(), Blocks.AIR.defaultBlockState(), LIGHT_BLOCK_CLIENTS);

            BlockState desistState = defaultBlockState()
                    .setValue(LightBlock.LEVEL, ALLOW_LIGHT_SPREAD)
                    .setValue(SCALE, 1)
                    .setValue(SPREAD, BeegLightBlock.SPREADING_FROZEN);
            world.setBlock(desistingBlock.getPos(), desistState, LIGHT_BLOCK_CLIENTS);
            return true;
        }

        // For every direction
        ArrayList<ASIWorldBlock> spill = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            BlockPos blockpos = pos.relative(direction);
            BlockState state = world.getBlockState(blockpos);

            // The other block must strictly be a Beeg Block
            if (state.isAir()) { continue; }
            if (!(state.getBlock() instanceof SimpleBeegBlock)) { continue; }

            // When dealing with light itself, the adjacent block must have greater light level or spread
            if (state.getBlock() instanceof BeegLightBlock) {
                int spreading = state.getValue(SPREADING);
                int level = state.getValue(LightBlock.LEVEL);
                int spread = state.getValue(SPREAD);

                if (level < MINIMUM_LIGHT_SPREAD) { continue; } // This light block has desisted, ignore
                if (level > myLightLevel) { return !(spread == 1 && mySpread == myScale && (level == myLightLevel + 1)); }     // Qualified as light source

                // When I am the last block at this light level
                if ((mySpread == 1) && (level == (myLightLevel - 1))) {

                    // This is the first block of that next light level, spill to desist
                    if ((spread == state.getValue(SCALE)) && (spreading < 2)) { spill.add(new ASIWorldBlock(state, blockpos, world)); continue; }
                }

                // Not our light source, ignored
                if (level < myLightLevel) { continue; }

                // Same light level but their spread is higher?
                if (spread > mySpread) { return !(spread == mySpread + 1); }    // Qualified as light source

                // Are they the next spread level? Spill to desist
                if ((spread == (mySpread - 1)) && (spreading < 2)) { spill.add(new ASIWorldBlock(state, blockpos, world)); }
                continue;
            }

            // The other support for beeg light is a beeg light source itself
            if (state.getBlock() instanceof BeegLightSource) {
                int level = ((BeegLightSource) state.getBlock()).getLight();
                if (level == myLightLevel) { return mySpread != myScale; }   // Qualified as light source
            }
        }

        // Not a single source was found, delete this
        //BlockState desistState = defaultBlockState()
                //.setValue(LightBlock.LEVEL, BLOCK_LIGHT_SPREAD)
                //.setValue(SCALE, 1)
                //.setValue(SPREAD, BeegLightBlock.SPREADING_FROZEN);
        //BLB//"LIGHT ["+ desistingBlock.getPos().toShortString() + "] &6 Desisting from " + logLight(desistingBlock.getState()) + " -> " + logLight(desistState));
        //world.setBlock(desistingBlock.getPos(), desistState, LIGHT_BLOCK_CLIENTS);
        //world.scheduleTick(desistingBlock.getPos(), this, LIGHT_PROPAGATION_TICK * 8);
        //world.setBlock(desistingBlock.getPos(), Blocks.AIR.defaultBlockState(), LIGHT_BLOCK_CLIENTS);
        for (ASIWorldBlock spread : spill) {
            BlockState tickedState = spread.getState().setValue(SPREADING, BeegLightBlock.SPREADING_FROZEN);
            //BLB//"LIGHT ["+ desistingBlock.getPos().toShortString() + "] Desist ticking <" + spread.getPos().toShortString() + "> " + logLight(spread.getState()) + " -> " + logLight(tickedState));

            // No more updating this block
            world.setBlock(spread.getPos(), tickedState, LIGHT_BLOCK_SILENT);
            world.scheduleTick(spread.getPos(), this, randomPropagationTick() * 2); }
        return true;
    }

    /**
     * @param sourcingBlock The light block whose source you are looking for
     *
     * @return The block that sourced this light block
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @Nullable public ASIWorldBlock traceSource(@NotNull ASIWorldBlock sourcingBlock) {

        // Identify
        BlockPos pos = sourcingBlock.getPos();
        Level world = sourcingBlock.getWorld();
        int myLightLevel = sourcingBlock.getState().getValue(LightBlock.LEVEL);
        int mySpread = sourcingBlock.getState().getValue(SPREAD);
        int myScale = sourcingBlock.getState().getValue(SCALE);

        // For every direction
        ArrayList<ASIWorldBlock> spill = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            BlockPos blockpos = pos.relative(direction);
            BlockState state = world.getBlockState(blockpos);

            // The other block must strictly be a Beeg Block
            if (state.isAir()) { continue; }
            if (!(state.getBlock() instanceof SimpleBeegBlock)) { continue; }

            // When dealing with light itself, the adjacent block must have greater light level or spread
            if (state.getBlock() instanceof BeegLightBlock) {
                int spreading = state.getValue(SPREADING);
                int level = state.getValue(LightBlock.LEVEL);
                int spread = state.getValue(SPREAD);

                if (level < MINIMUM_LIGHT_SPREAD) { continue; } // This light block has desisted, ignore
                if (spread == 1 && mySpread == myScale && (level == myLightLevel + 1)) { return new ASIWorldBlock(state, blockpos, world); }     // Qualified as light source

                // Not our light source, ignored
                if (level < myLightLevel) { continue; }

                // Same light level but their spread is higher?
                if (spread == mySpread + 1) { return new ASIWorldBlock(state, blockpos, world); }    // Qualified as light source

                continue;
            }

            // The other support for beeg light is a beeg light source itself
            if (state.getBlock() instanceof BeegLightSource) {
                int level = ((BeegLightSource) state.getBlock()).getLight();
                if ((level == myLightLevel) && (mySpread == myScale)) { return new ASIWorldBlock(state, blockpos, world); }   // Qualified as light source
            }
        }

        return null;
    }

    @NotNull String logLight(@NotNull BlockState lightBlock) {
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
        //BLB//"&8 =========== TICKING ["+ worldBlock.getPos().toShortString() + "] " + logLight(worldBlock.getState()) + " ===========");

        // Desist if standalone
        if (desist(worldBlock)) {
            //BLB//"&8 =========== DESISTED ===========");
            return;
        }

        // Spread if spreading, clear special spread flag
        int spreading = worldBlock.getState().getValue(SPREADING);
        if (spreading == 2) {
            BlockState thawState = worldBlock.getState().setValue(SPREADING, BeegLightBlock.SPREADING_NO);
            //BLB//"LIGHT ["+ worldBlock.getPos().toShortString() + "] &3 Thawed from " + logLight(worldBlock.getState()) + " -> " + logLight(thawState));
            worldBlock.getWorld().setBlock(worldBlock.getPos(), thawState, LIGHT_BLOCK_SILENT);
            //BLB//"&8 =========== THAWED ===========");
            return;
        }
        if (spreading == 1) {
            spread(worldBlock);
            //BLB//"&8 =========== SPREAD ===========");
            return;
        }
        //BLB//"&8 =========== NONE ===========");
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
        if (myLightLevel <= MINIMUM_LIGHT_SPREAD) {
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
