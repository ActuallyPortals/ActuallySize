package actually.portals.ActuallySize.pickup.mixininterfaces;

/**
 * Pehkui normalizes the scale of entities rendered in
 * inventory to 1.0 for compatibility purposes (otherwise,
 * they would look gigantic or tiny). This however makes
 * their true scale inaccessible for other purposes, which
 * ASI cares about A LOT.
 * <p>
 * Then, this interface allows entities to remember their
 * true scale while it is being overridden by some other
 * process.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface RenderNormalizable {

    /**
     * @return The true scale data for this entity, before it was
     *         normalized for rendering or other purposes.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    double actuallysize$getPreNormalizedScale();

    /**
     * @return If the entity is currently normalized and its scale
     *         is being overridden by some sort of fake cache.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    boolean actuallysize$isScaleNormalized();

    /**
     * This method will go through the scales of this entity and save
     * them to a local storage, where they will persist until released.
     * Ideally this will be called right when the Entity starts/stops
     * overriding its scales.
     *
     * @param release If instead of capturing them, we are clearing it.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$setPreNormalizedScale(boolean release);
}
