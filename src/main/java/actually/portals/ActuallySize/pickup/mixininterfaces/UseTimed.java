package actually.portals.ActuallySize.pickup.mixininterfaces;

/**
 * A class with a use time associated with it
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface UseTimed {

    /**
     * @return The ticks this item will be in use
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    int actuallysize$getUseTimeTicks();

    /**
     * @param ticks The ticks this item will be in use
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$setUseTimeTicks(int ticks);
}
