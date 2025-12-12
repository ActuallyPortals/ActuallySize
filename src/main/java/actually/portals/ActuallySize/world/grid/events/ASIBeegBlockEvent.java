package actually.portals.ActuallySize.world.grid.events;

import actually.portals.ActuallySize.world.grid.ASIBeegBlock;
import org.jetbrains.annotations.NotNull;

/**
 * An event that deals with beeg blocks
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface ASIBeegBlockEvent {

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull ASIBeegBlock getBeegBlock();
}
