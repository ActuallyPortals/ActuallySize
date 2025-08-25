package actually.portals.ActuallySize.pickup.mixininterfaces;

import actually.portals.ActuallySize.pickup.holding.model.ASIPSModelPartInfo;
import org.jetbrains.annotations.Nullable;

/**
 * The capability of a held entity to know the model part
 * it is held in, of its holder, if applicable for the
 * hold point it is held by.
 *
 * @author Actually Portals
 * @since 1.0.0
 */
public interface ModelPartHoldable {

    /**
     * @return The model part associated with this entity's hold point
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @Nullable ASIPSModelPartInfo actuallysize$getHeldModelPart();
}
