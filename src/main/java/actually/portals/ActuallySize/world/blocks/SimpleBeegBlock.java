package actually.portals.ActuallySize.world.blocks;

import actually.portals.ActuallySize.world.ASIWorldSystemManager;
import actually.portals.ActuallySize.world.grid.ASIWorldBlock;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * An interface not for ASI Beeg Building "Beeg Blocks" but
 * for vanilla blocks that may have a scale associated with
 * them.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface SimpleBeegBlock {

    /**
     * @param worldBlock The world block we want to check its scale
     *
     * @return The scale of this block if it is a SimpleBeegBlock,
     *         or 1 for all other blocks.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    default int getScale(@NotNull ASIWorldBlock worldBlock) {
        if (!(worldBlock.getState().getBlock() instanceof SimpleBeegBlock)) { return 1; }
        if (worldBlock.getState().hasProperty(ASIWorldSystemManager.BEEG_SCALE)) { return 1; }
        return worldBlock.getState().getValue(ASIWorldSystemManager.BEEG_SCALE);
    }

    /**
     * Sets the scale property of the specified Simple Beeg Block
     *
     * @param worldBlock The world block we want to check its scale
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    default void  setScale(@NotNull ASIWorldBlock worldBlock, int scale) {
        if (!(worldBlock.getState().getBlock() instanceof SimpleBeegBlock)) { return; }
        if (worldBlock.getWorld().isClientSide) { return; }
        worldBlock.getWorld().setBlock(worldBlock.getPos(), worldBlock.getState().setValue(ASIWorldSystemManager.BEEG_SCALE, scale), Block.UPDATE_ALL);
    }
}
