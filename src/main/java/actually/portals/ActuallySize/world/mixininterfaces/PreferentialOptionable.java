package actually.portals.ActuallySize.world.mixininterfaces;

/**
 * Whether this entity / player has preferred options,
 * and most importantly, if they have been applied.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface PreferentialOptionable {

    /**
     * @return If the preferred options for this entity have been applied
     *
     * @author Actually Portals
     */
    boolean actuallysize$isPreferredOptionsApplied(double latest);

    /**
     * @param state If the preferred options for this entity have been applied
     *
     * @author Actually Portals
     */
    void actuallysize$setPreferredOptionsApplied(double state);
}
