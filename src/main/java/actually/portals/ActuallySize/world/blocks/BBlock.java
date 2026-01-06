package actually.portals.ActuallySize.world.blocks;

import actually.portals.ActuallySize.world.ASIWorldSystemManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.NotNull;

/**
 * A block that stores scale information, so that
 * it can delete itself and its related blocks
 * when broken
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class BBlock extends Block implements SimpleBeegBlock {

    /**
     * @since 1.0.0
     */
    public static final IntegerProperty SCALE = ASIWorldSystemManager.BEEG_SCALE;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public BBlock(Properties pProperties) { super(pProperties); }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SCALE);
    }
}
