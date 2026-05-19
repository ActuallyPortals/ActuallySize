package actually.portals.ActuallySize.pickup.mixininterfaces;

/**
 * Allows entities to ignore fall damage for a while
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface GraceLanding {

    /**
     * @param ticks Ticks that this player will ignore fall damage
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$addGraceLanding(int ticks);

    /**
     * @return true if this has grace landing counters.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    boolean actuallysize$isInGraceLanding();
}
