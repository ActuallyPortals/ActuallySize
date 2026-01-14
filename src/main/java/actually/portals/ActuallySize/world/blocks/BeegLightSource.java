package actually.portals.ActuallySize.world.blocks;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.world.ASIWorldSystemManager;
import actually.portals.ActuallySize.world.grid.ASIWorldBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * A Beeg Block that supports Beeg Light Blocks coming out of it
 *
 * @author Actually Portals
 * @since 1.0.0
 */
public class BeegLightSource extends BBlock {

    /**
     * The light intensity emitted by this beeg block
     *
     * @since 1.0.0
     */
    final int light;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public int getLight() { return light; }

    /**
     * @param intensity The light level of this block, from 1 to 15
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public BeegLightSource(@NotNull Properties pProperties, int intensity) { super(pProperties.lightLevel((state) -> intensity)); light = intensity; }

    /**
     * Causes this light source to spread its light
     *
     * @param sourceBlock The ASI Beeg Light Source spreading light
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public void illuminate(@NotNull ASIWorldBlock sourceBlock) {
        if (sourceBlock.getWorld().isClientSide) { return; }
        if (!(sourceBlock.getState().getBlock() instanceof BeegLightSource)) { return; }
        int myScale = sourceBlock.getState().getValue(SCALE);
        if (myScale <= 1) { return; }

        // Identify
        BlockPos pos = sourceBlock.getPos();
        Level world = sourceBlock.getWorld();
        int myLightLevel = getLight();
        if (myLightLevel < 1) { return; }

        // List the blocks to which we will spread
        ArrayList<ASIWorldBlock> spill = new ArrayList<>();
        for(Direction direction : Direction.values()) {
            BlockPos blockpos = pos.relative(direction);
            BlockState state = world.getBlockState(blockpos);

            // Air blocks are immediately spread onto
            if (BeegLightBlock.canSpreadTo(state, myLightLevel, myScale, myScale, true)) {
                spill.add(new ASIWorldBlock(state, blockpos, world)); }
        }

        // Nowhere to spread? End
        if (spill.isEmpty()) { return; }

        // Well then, spill
        BlockState spillState = ASIWorldSystemManager.BEEG_LIGHT_BLOCK.getAsBlock().get().defaultBlockState()
                .setValue(LightBlock.LEVEL, myLightLevel)
                .setValue(BeegLightBlock.SCALE, myScale)
                .setValue(BeegLightBlock.SPREAD, myScale)
                .setValue(BeegLightBlock.SPREADING, BeegLightBlock.SPREADING_YES);
        for (ASIWorldBlock spread : spill) {
            //BLB//ActuallySizeInteractions.Log("SOURCE ["+ sourceBlock.getPos().toShortString() + "] Spread to " + spread.getPos().toShortString() + ", L=" + myLightLevel + ", P=" + myScale);
            world.setBlock(spread.getPos(), spillState, Block.UPDATE_CLIENTS);
            world.scheduleTick(spread.getPos(), ASIWorldSystemManager.BEEG_LIGHT_BLOCK.getAsBlock().get(), BeegLightBlock.LIGHT_PROPAGATION_TICK);
            //world.updateNeighborsAt(spread.getPos(), ASIWorldSystemManager.BEEG_LIGHT_BLOCK.getAsBlock().get());
        }
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public void tick(@NotNull BlockState pState, @NotNull ServerLevel pLevel, @NotNull BlockPos pPos, @NotNull RandomSource pRandom) {
        super.tick(pState, pLevel, pPos, pRandom);
        illuminate(new ASIWorldBlock(pState, pPos, pLevel));
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
        pLevel.scheduleTick(pPos, this, BeegLightBlock.LIGHT_PROPAGATION_TICK);
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState pState, @NotNull Direction pDirection, @NotNull BlockState pNeighborState, @NotNull LevelAccessor pLevel, @NotNull BlockPos pPos, @NotNull BlockPos pNeighborPos) {
        pLevel.scheduleTick(pPos, this, BeegLightBlock.LIGHT_PROPAGATION_TICK);
        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }
}
