package actually.portals.ActuallySize.pickup.mixininterfaces;

import actually.portals.ActuallySize.pickup.holding.points.ASIPSHoldPointRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * Ads the method to set this entity's Hold Point overrides
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface HoldPointConfigurable {

    /**
     * @return The local hold point overrides that will be checked before global ones
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull ASIPSHoldPointRegistry actuallysize$getLocalHoldPoints();

    /**
     * @param reg The local hold point overrides that will be checked before global ones
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$setLocalHoldPoints(@NotNull ASIPSHoldPointRegistry reg);
}
