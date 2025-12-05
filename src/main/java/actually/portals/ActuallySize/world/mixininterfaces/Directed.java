package actually.portals.ActuallySize.world.mixininterfaces;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

/**
 * When something has a Direction
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface Directed {

    /**
     * @return The direction involved in this
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable Direction actuallysize$getDirection();

    /**
     * @param direction The direction involved in this
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$setDirection(@Nullable Direction direction);
}
