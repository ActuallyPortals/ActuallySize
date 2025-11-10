package actually.portals.ActuallySize.pickup.mixininterfaces;

/**
 * When this has a time duration associated with it
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface TimeDurationModifiable {

    /**
     * @param duration A modified duration
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$setDuration(int duration);

    /**
     * @return The duration
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    int actuallysize$getDuration();
}
