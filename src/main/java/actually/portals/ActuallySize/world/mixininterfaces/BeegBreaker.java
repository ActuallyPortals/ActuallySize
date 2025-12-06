package actually.portals.ActuallySize.world.mixininterfaces;

/**
 * When something is in the middle of breaking beeg,
 * this flag becomes set so as for some systems to
 * behave differently
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface BeegBreaker {

    /**
     * @return If this is currently beeg breaking
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    boolean actuallysize$isBeegBreaking();

    /**
     * @param is If this is currently beeg breaking
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$setBeegBreaking(boolean is);
}
