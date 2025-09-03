package actually.portals.ActuallySize.pickup.mixininterfaces;

import org.jetbrains.annotations.NotNull;

/**
 * When this can be combined with another of its own kind
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface Combinable<T> {

    /**
     * @param other The other of this to combine it with
     *
     * @return These but combined into one yay. It will be a
     *         brand-new instance, like a clone and then combine.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull T actuallysize$combineWith(@NotNull T other);
}
