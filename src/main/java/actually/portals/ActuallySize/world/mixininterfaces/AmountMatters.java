package actually.portals.ActuallySize.world.mixininterfaces;

import org.jetbrains.annotations.Nullable;

/**
 * When something has a relevant amount
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface AmountMatters {

    /**
     * @param amount The amount that matters
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$setAmount(@Nullable Double amount);

    /**
     * @return The amount that matters
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable Double actuallysize$getAmount();
}
