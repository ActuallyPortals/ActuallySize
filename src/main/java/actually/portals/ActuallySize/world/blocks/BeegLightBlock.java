package actually.portals.ActuallySize.world.blocks;

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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

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
        builder.add(SCALE, SPREAD);
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
        if (spreadingLightBlock.getWorld().isClientSide) { return; }
        if (!(spreadingLightBlock.getState().getBlock() instanceof BeegLightBlock)) { return; }

        // List the blocks to which we will spread
        ArrayList<ASIWorldBlock> spill = new ArrayList<>();

        // Identify
        BlockPos pos = spreadingLightBlock.getPos();
        Level world = spreadingLightBlock.getWorld();
        int myLightLevel = spreadingLightBlock.getState().getValue(LightBlock.LEVEL);
        int myScale = spreadingLightBlock.getState().getValue(SCALE);
        int mySpread = spreadingLightBlock.getState().getValue(SPREAD);

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
                .setValue(SPREAD, spreadLevel);
        for (ASIWorldBlock spread : spill) {
            world.setBlock(spread.getPos(), spillState, Block.UPDATE_CLIENTS);
            world.scheduleTick(spread.getPos(), this, LIGHT_PROPAGATION_TICK);
            world.updateNeighborsAt(spread.getPos(), this); }
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
        if (state.isAir()) {return true;}

        // Other blocks cannot be spread into
        if (!(state.getBlock() instanceof LightBlock)) { return false; }
        int level = state.getValue(LightBlock.LEVEL);

        // If light is trying to spread itself
        if (!isSource && state.getBlock() instanceof BeegLightBlock) {

            // If the light level is too low for a Beeg Light Block, then it is a DESISTED light block
            if (level < MINIMUM_LIGHT_SPREAD) { return false; }
        }

        // Light blocks must emit less light
        if (mySpread <= 1) { level++; } // If this is the last block of this light level, the other's level must be equalized
        if (level > myLightLevel) {return false;}
        if (!(state.getBlock() instanceof BeegLightBlock)) { return true; }

        // I cannot spread upstream or to the same spread
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
        if (desistingBlock.getWorld().isClientSide) { return false; }
        if (!(desistingBlock.getState().getBlock() instanceof BeegLightBlock)) { return false; }

        // Identify
        BlockPos pos = desistingBlock.getPos();
        Level world = desistingBlock.getWorld();
        int myLightLevel = desistingBlock.getState().getValue(LightBlock.LEVEL);
        int mySpread = desistingBlock.getState().getValue(SPREAD);

        // Final desist
        if (myLightLevel < MINIMUM_LIGHT_SPREAD) { world.setBlock(desistingBlock.getPos(), Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS); return true; }

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
                int level = state.getValue(LightBlock.LEVEL);
                if (level > myLightLevel) { return false; }     // Qualified as light source

                // Check for spread
                int spread = state.getValue(SPREAD);

                // When I am the last block at this light level
                if (mySpread == 1 && level == (myLightLevel - 1)) {

                    // This is the first block of that next light level, spill to desist
                    if (spread == state.getValue(SCALE)) { spill.add(new ASIWorldBlock(state, blockpos, world)); }
                }

                // Not our light source, ignored
                if (level < myLightLevel) { continue; }

                // Same light level but their spread is higher?
                if (spread > mySpread) { return false; }    // Qualified as light source

                // Are they the next spread level? Spill to desist
                if (spread == (mySpread - 1)) { spill.add(new ASIWorldBlock(state, blockpos, world)); }
            }

            // The other support for beeg light is a beeg light source itself
            if (state.getBlock() instanceof BeegLightSource) {
                int level = ((BeegLightSource) state.getBlock()).getLight();
                if (level >= myLightLevel) { return false; }   // Qualified as light source
            }
        }

        // Not a single source was found, delete this
        BlockState desistState = defaultBlockState()
                .setValue(LightBlock.LEVEL, 0)
                .setValue(SCALE, 1)
                .setValue(SPREAD, 1);
        world.setBlock(desistingBlock.getPos(), desistState, Block.UPDATE_CLIENTS);
        world.scheduleTick(desistingBlock.getPos(), this, LIGHT_PROPAGATION_TICK * 8);
        for (ASIWorldBlock spread : spill) { world.scheduleTick(spread.getPos(), this, randomPropagationTick()); }
        return true;
    }

    /**
     * Called for this light source to update
     * itself and delete or spread as needed
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    void lightTick(@NotNull ASIWorldBlock worldBlock) {

        // Desist if standalone
        if (desist(worldBlock)) { return; }

        // Spread as usual
        spread(worldBlock);
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
        pLevel.scheduleTick(pPos, this, randomPropagationTick());
        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }
}
