package actually.portals.ActuallySize.pickup.mixininterfaces;

import org.jetbrains.annotations.Nullable;

/**
 * Allows ASI to finesse its way with vanilla Model Parts
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface VASIModelPart {

    /**
     * @return The name of this model part
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable String actuallysize$getModelName();

    /**
     * Changes the name of this model part
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$setModelName(@Nullable String name);
}
