package actually.portals.ActuallySize.pickup.holding.model;

import actually.portals.ActuallySize.pickup.holding.points.ASIPSRelativeFacingHold;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ModelPartHoldable;
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
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public ASIPSModelPartHoldPoint setIsRelativeYaw(boolean relative) { this.relativeYaw = relative; return this; }

    /**
     * A ride point relative to the holder's pitch, yaw, and world coordinates.
     *
     * @param nk The namespaced key to name this slot
     * @param sideOffset Relative sideways offset
     * @param verticalOffset Relative vertical offset
     * @param forwardOffset Relative forward offset
     * @param xOffset Absolute X offset
     * @param yOffset Absolute Y offset
     * @param zOffset Absolute Z offset
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIPSModelPartHoldPoint(
            @NotNull ResourceLocation nk,
            double sideOffset,
            double verticalOffset,
            double forwardOffset,
            double xOffset,
            double yOffset,
            double zOffset,
            @NotNull ASIMPLRendererLinker... linkers) {
        this(nk, sideOffset, verticalOffset, forwardOffset, 0, xOffset, yOffset, zOffset, linkers);
    }

    /**
     * A ride point relative to the holder's pitch, yaw, and world coordinates.
     *
     * @param nk The namespaced key to name this slot
     * @param sideOffset Relative sideways offset
     * @param verticalOffset Relative vertical offset
     * @param forwardOffset Relative forward offset
     * @param levelOffset Relative level offset
     * @param xOffset Absolute X offset
     * @param yOffset Absolute Y offset
     * @param zOffset Absolute Z offset
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIPSModelPartHoldPoint(
            @NotNull ResourceLocation nk,
            double sideOffset,
            double verticalOffset,
            double forwardOffset,
            double levelOffset,
            double xOffset,
            double yOffset,
            double zOffset,
            @NotNull ASIMPLRendererLinker... linkers) {
        super(nk, sideOffset, verticalOffset, forwardOffset, levelOffset, xOffset, yOffset, zOffset);
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
        if (model == null || model.getOrigin() == null) { return super.getOrigin(holder, entityDuality); }

        // May be relative
        if (isRelativePosition()) {
            return model.getOrigin().add(super.getOrigin(holder, entityDuality));
        } else { return model.getOrigin(); }
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
}
