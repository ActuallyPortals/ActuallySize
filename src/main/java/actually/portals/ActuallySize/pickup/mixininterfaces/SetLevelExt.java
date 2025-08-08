package actually.portals.ActuallySize.pickup.mixininterfaces;

import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Allows to change the level variable that is normally protected :B
 * "Only use if you really know what you're doing"
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface SetLevelExt {

    /**
     * "Only use if you really know what you're doing"
     *
     * @param world The world to set in this class' normally-protected level variable
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$SetWorld(@NotNull Level world);
}
