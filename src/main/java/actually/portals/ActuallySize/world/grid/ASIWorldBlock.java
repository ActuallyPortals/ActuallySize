package actually.portals.ActuallySize.world.grid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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
    @NotNull BlockState state;

    /**
     * The position in the world
     *
     * @since 1.0.0
     */
    @NotNull BlockPos pos;

    /**
     * The world
     *
     * @since 1.0.0
     */
    @NotNull Level world;

    /**
     * @param state The Block State
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
     * @since 1.0.0
     * @author Actually Portals
     */
    public @NotNull Level getWorld() {
        return world;
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public void setWorld(@NotNull Level world) {
        this.world = world;
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @NotNull BlockPos getPos() {
        return pos;
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public void setPos(@NotNull BlockPos pos) {
        this.pos = pos;
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @NotNull BlockState getState() {
        return state;
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public void setState(@NotNull BlockState state) {
        this.state = state;
    }
}
