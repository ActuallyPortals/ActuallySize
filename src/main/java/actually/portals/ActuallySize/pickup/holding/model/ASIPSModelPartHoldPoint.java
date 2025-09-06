package actually.portals.ActuallySize.pickup.holding.model;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSRelativeFacingHold;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ModelPartHoldable;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityVectors;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.SVFLBit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * A hold point that puts the held entity at a specific set of
 * coordinates relative to the holder's specified model part.
 *
 * @author Actually Portals
 * @since 1.0.0
 */
public class ASIPSModelPartHoldPoint extends ASIPSRelativeFacingHold {

    /**
     * If the Position of the holder modifies this model part (usually true)
     *
     * @since 1.0.0
     */
    boolean relativePosition = true;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public boolean isRelativePosition() { return relativePosition; }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public ASIPSModelPartHoldPoint setIsRelativePosition(boolean relative) { this.relativePosition = relative; return this; }

    /**
     * If the Pitch of the holder modifies this model part (usually false)
     *
     * @since 1.0.0
     */
    boolean relativePitch = false;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public boolean isRelativePitch() { return relativePitch; }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public ASIPSModelPartHoldPoint setIsRelativePitch(boolean relative) { this.relativePitch = relative; return this; }

    /**
     * If the Yaw of the holder modifies this model part (usually false)
     *
     * @since 1.0.0
     */
    boolean relativeYaw = false;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public boolean isRelativeYaw() { return relativeYaw; }

    /**
     * If the Roll of the holder modifies this model part (usually false)
     *
     * @since 1.0.0
     */
    boolean relativeRoll = false;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public boolean isRelativeRoll() { return relativeRoll; }

    /**
     * Before any model part can be calculated, this transformation
     * is applied to the holder coordinates to approximate where the
     * model part should be.
     * <br></br>
     * Essentially, the yaw and pitch of the holder are used, as is
     * the position of the holder (usually their feet, in minecraft
     * entity terms). So this SVF transformation is applied to the
     * position of the holder to provide a new origin on top of
     * which the actual coordinates of this part are applied.
     *
     * @since 1.0.0
     */
    @NotNull final SVFLBit defaultOrigin;

    /**
     * An empty bit that really should be ZERO
     *
     * @since 1.0.0
     */
    @NotNull private final static SVFLBit ZERO = new SVFLBit();

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public SVFLBit getDefaultOrigin() { return defaultOrigin; }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public ASIPSModelPartHoldPoint setIsRelativeYaw(boolean relative) { this.relativeYaw = relative; return this; }

    /**
     * A ride point relative to the holder's pitch, yaw, and world coordinates.
     *
     * @param nk The namespaced key to name this slot
     * @param svf The SVF coordinate information
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIPSModelPartHoldPoint(@NotNull ResourceLocation nk, @NotNull SVFLBit svf, @NotNull ASIMPLRendererLinker... linkers) {
        this(nk, svf, new SVFLBit(), linkers);
    }

    /**
     * A ride point relative to the holder's pitch, yaw, and world coordinates.
     *
     * @param nk The namespaced key to name this slot
     * @param svf The SVF coordinate information
     *
     * @param defaultOrigin Before any model part can be calculated, this transformation
     *                      is applied to the holder coordinates to approximate where the
     *                      model part should be.
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIPSModelPartHoldPoint(@NotNull ResourceLocation nk, @NotNull SVFLBit svf, @NotNull SVFLBit defaultOrigin, @NotNull ASIMPLRendererLinker... linkers) {
        super(nk, svf);
        this.defaultOrigin = defaultOrigin;
        for (ASIMPLRendererLinker linker : linkers) { registerPartLink(linker); }
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public Vec3 getOrigin(@NotNull Entity holder, @NotNull EntityDualityCounterpart entityDuality) {

        // When unset, resort to super method
        ASIPSModelPartInfo model = ((ModelPartHoldable) entityDuality).actuallysize$getHeldModelPart();
        if (model == null || model.getOrigin() == null) {

            /*
             * When we do not know the true origin of the model,
             * we must estimate using the default origin provided
             */
            return super.getOrigin(holder, entityDuality).add(
                    OotilityVectors.transformSVFL(getPitch(holder, entityDuality), getYaw(holder, entityDuality), getDefaultOrigin()).scale(ASIUtilities.getEntityScale(holder)));
        }

        // May be relative
        if (isRelativePosition()) {
            return model.getOrigin().add(super.getOrigin(holder, entityDuality));
        } else {
            return model.getOrigin();
        }
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public double getPitch(@NotNull Entity holder, @NotNull EntityDualityCounterpart entityDuality) {

        // When unset, resort to super method
        ASIPSModelPartInfo model = ((ModelPartHoldable) entityDuality).actuallysize$getHeldModelPart();
        if (model == null || model.getOrigin() == null) { return super.getPitch(holder, entityDuality); }

        // May be relative
        if (isRelativePitch()) {
            return model.getPitch() + super.getPitch(holder, entityDuality);
        } else { return model.getPitch(); }
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public double getYaw(@NotNull Entity holder, @NotNull EntityDualityCounterpart entityDuality) {

        // When unset, resort to super method
        ASIPSModelPartInfo model = ((ModelPartHoldable) entityDuality).actuallysize$getHeldModelPart();
        if (model == null || model.getOrigin() == null) { return super.getYaw(holder, entityDuality); }

        // May be relative
        if (isRelativeYaw()) {
            return model.getYaw() + super.getYaw(holder, entityDuality);
        } else { return model.getYaw(); }
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public double getRoll(@NotNull Entity holder, @NotNull EntityDualityCounterpart entityDuality) {

        // When unset, resort to super method
        ASIPSModelPartInfo model = ((ModelPartHoldable) entityDuality).actuallysize$getHeldModelPart();
        if (model == null || model.getOrigin() == null) { return super.getRoll(holder, entityDuality); }

        // May be relative
        if (isRelativeRoll()) {
            return model.getRoll() + super.getRoll(holder, entityDuality);
        } else { return model.getRoll(); }
    }

    @Override
    public @NotNull SVFLBit getCoordinates(@NotNull Entity holder, @NotNull EntityDualityCounterpart entityDuality) {

        // When the model information is unset, we may not apply any transformation
        ASIPSModelPartInfo model = ((ModelPartHoldable) entityDuality).actuallysize$getHeldModelPart();
        if (model == null || model.getOrigin() == null) {

            /*
             * When we do not know the true origin of the model,
             * we must estimate using the default origin provided
             */
            return ZERO;
        }

        // When set, do use the real one
        return super.getCoordinates(holder, entityDuality);
    }
}
