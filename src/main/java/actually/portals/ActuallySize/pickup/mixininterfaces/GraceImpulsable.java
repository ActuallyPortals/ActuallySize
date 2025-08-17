package actually.portals.ActuallySize.pickup.mixininterfaces;

/**
 * When ASI needs to give players some impulse ticks
 * where they are allowed to exceed max velocities
 * before the server rubberbands them.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface GraceImpulsable {

    /**
     * @param ticks Ticks that this player will ignore the server velocity anti-cheat
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$addGraceImpulse(int ticks);

    /**
     * @return true if this has impulse counters.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    boolean actuallysize$isInGraceImpulse();
}
