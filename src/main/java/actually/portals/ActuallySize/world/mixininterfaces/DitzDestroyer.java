package actually.portals.ActuallySize.world.mixininterfaces;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Something with the capability of harvesting certain
 * blocks under some conditions but not necessarily
 * always.
 * <br><br>
 * Usually you'd check for the actual conditions with
 * {@link net.minecraftforge.common.ForgeHooks#isCorrectToolForDrops(BlockState, Player)}
 * but I want something a little rougher so that you
 * may sometimes break blocks you are not supposed to
 * break because you are big and clumsy ditz.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface DitzDestroyer {

    /**
     * @param block The block to be destroyed
     * @param tool The tool that is being used
     *
     * @return If this tool can destroy this block because it is
     *         the "correct tool" though it may not be the correct
     *         tier or correct conditions to harvest.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    boolean actuallysize$canDitzDestroy(@NotNull ItemStack tool, @NotNull BlockState block);
}
