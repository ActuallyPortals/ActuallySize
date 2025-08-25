package actually.portals.ActuallySize.pickup.events;

import actually.portals.ActuallySize.pickup.holding.points.ASIPSStaticHoldRegistry;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Event fired during server startup or client startup
 * when hold points are first registered to the registry.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIHoldPointRegistryEvent extends Event {

    /**
     * The registry where hold points are registered to
     *
     * @since 1.0.0
     */
    @NotNull ASIPSStaticHoldRegistry registry;

    /**
     * @param registry The registry where hold points are registered to
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIHoldPointRegistryEvent(@NotNull ASIPSStaticHoldRegistry registry) {
        this.registry = registry;
    }
}
