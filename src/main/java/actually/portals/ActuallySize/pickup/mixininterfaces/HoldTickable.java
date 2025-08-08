package actually.portals.ActuallySize.pickup.mixininterfaces;

/**
 * Ads the method 'holdTick()' meant to be called when an
 * Entity ticks while held by someone, similar to rideTick()
 * but better.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface HoldTickable {

    /**
     * Called every world tick when ticking this held entity,
     * guaranteed to tick after the holder who is holding this entity.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$holdTick();
}
