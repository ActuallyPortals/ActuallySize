package actually.portals.ActuallySize.world.mixininterfaces;

/**
 * When something is picked up by other player
 * sizes and undergoes non-conservation of mass
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface BeegPicker {

    /**
     * This is guaranteed to be called once before beeg-ness
     * affects item count, so the original count may be
     * remembered then if it is unknown
     *
     * @return The count of this item before being affected by beeg-ness
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    int actuallysize$getOriginalCount();

    /**
     * @return The count of this item after being affected by beeg-ness
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    int actuallysize$getSizedCount();

    /**
     * @param count The count of this item after being affected by beeg-ness
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$setSizedCount(int count);
}
