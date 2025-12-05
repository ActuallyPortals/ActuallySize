package actually.portals.ActuallySize.world.grid.construction;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Placing down blocks in higher grid sizes has to be
 * done with finesse and style, which is why I don't use
 * a simple indexed fill and rather this elegant algorithm
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface ASIGConstructor {

    /**
     * @return The scale of the grid, presumably the ceil of the
     *         player's scale who is building this so that it
     *         is a non-zero natural number.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    int getGridScale();

    /**
     * The ordered list of indices that fill this grid construction
     *
     * @param depth The level of contrivance of this grid segment
     * @param limit The number of vectors we are looking for
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull ArrayList<Vec3> elaborate(int depth, int limit);

}
