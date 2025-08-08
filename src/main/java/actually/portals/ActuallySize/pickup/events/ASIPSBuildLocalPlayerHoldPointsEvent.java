package actually.portals.ActuallySize.pickup.events;

import actually.portals.ActuallySize.pickup.holding.points.ASIPSHoldPointRegistry;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

/**
 * An event that runs whenever the LOCAL PLAYER's hold points
 * are rebuilt, anytime that hold point registry is requested
 * from anywhere.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIPSBuildLocalPlayerHoldPointsEvent extends Event {

    /**
     * The hold point registry being built
     *
     * @since 1.0.0
     */
    @NotNull final ASIPSHoldPointRegistry registry;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public ASIPSHoldPointRegistry getRegistry() { return registry; }

    /**
     * @param registry The hold point registry being built
     */
    public ASIPSBuildLocalPlayerHoldPointsEvent(@NotNull ASIPSHoldPointRegistry registry) {
        this.registry = registry;
    }
}
