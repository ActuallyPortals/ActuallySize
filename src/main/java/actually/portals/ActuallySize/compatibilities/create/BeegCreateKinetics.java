package actually.portals.ActuallySize.compatibilities.create;

import org.jetbrains.annotations.Nullable;

/**
 * A mixin interface for Create kinetic blocks
 * that account for the strength of giants.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface BeegCreateKinetics {

    /**
     * @param beeg The player who last used this kinetic,
     *             and whose size must be accounted for.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$setKineticBeeg(@Nullable BeegCreateMechanics beeg);

    /**
     * @return The player who last used this kinetic,
     *         and whose size must be accounted for.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable BeegCreateMechanics actuallysize$getKineticBeeg();
}
