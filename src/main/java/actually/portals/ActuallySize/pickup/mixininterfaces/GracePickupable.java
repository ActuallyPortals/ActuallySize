package actually.portals.ActuallySize.pickup.mixininterfaces;

/**
 * Allows entities to escape pickup from giants
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface GracePickupable {

    /**
     * @param ticks Ticks that this player will ignore fall damage
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$addGracePickup(int ticks);

    /**
     * @return true if this has grace landing counters.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    boolean actuallysize$isInGracePickup();
}
