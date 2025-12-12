package actually.portals.ActuallySize.pickup.holding.model;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.netcode.ASINetworkManager;
import actually.portals.ActuallySize.netcode.packets.serverbound.ASINSModelPartCoordinateSync;
import actually.portals.ActuallySize.pickup.mixininterfaces.*;
import com.mojang.blaze3d.vertex.PoseStack;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityVectors;
import gunging.ootilities.GungingOotilitiesMod.scheduling.SchedulingManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

/**
 * The renderer linker that deals with Living Entity models (isn't that neat?)
 *
 * @author Actually Portals
 * @since 1.0.0
 */
public abstract class ASIMPLRendererLinker implements ASIPSModelPartLinker {

    /**
     * the model part name this renderer linker is linking to
     *
     * @since 1.0.0
     */
    @NotNull String modelPartName;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public String getModelPartName() { return modelPartName; }

    /**
     * @param part The name of the vanilla model part this is linking to
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIMPLRendererLinker(@NotNull String part) { modelPartName = part; }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public boolean appliesTo(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityCounterpart, @NotNull Object context) {
        return holder instanceof Entity && context instanceof ModelPart && getModelPartName().equals(((VASIModelPart) (Object) context).actuallysize$getModelName());
    }

    /**
     * Runs whenever a vanilla model part is rendered, of an entity with actively-held dualities.
     * The renderer linker should check that this is the model part it is targeting first.
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public void linkModel(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityCounterpart, @NotNull ModelPart part, @NotNull PoseStack poseStack) {

        // Identify the variables we will use
        VASIPoseStack asASIPoseStack = (VASIPoseStack) poseStack;
        if (!asASIPoseStack.actuallysize$isMirroring()) { return; }    // Not mirroring? Cancel
        PoseStack.Pose lastPose = asASIPoseStack.actuallysize$mirrorLast();
        VASIPoseStackPose asASIPose = (VASIPoseStackPose) (Object) lastPose;
        Matrix4f pose = asASIPose.actuallysize$pose();
        ModelPartHoldable holdableTiny = (ModelPartHoldable) entityCounterpart;
        EntityRenderer renderer = asASIPoseStack.actuallysize$getRenderer();
        double size = ASIUtilities.getEntityScale((Entity) holder);

        // The origin is calculated in the Pose fourth quaternion, it is its first three components
        Vec3 origin = new Vec3(pose.m30(), pose.m31(), pose.m32());
        Vec3 offset = renderer.getRenderOffset((Entity) holder, 0);
        origin = origin.scale(size).add(offset);

        // Identify basis to read PITCH and YAW
        Vec3 basis = getBasis(part, poseStack, pose).normalize();
        Vec3 sense = getSense(part, poseStack, pose).normalize();
        Vec3 side = sense.cross(basis);
        double lenXZ = Math.sqrt((basis.x * basis.x) + (basis.z * basis.z));
        double pitch, yaw, roll;
        if (lenXZ > 0) {

            // basis = < #, #, # >
            // sense = < #, #, # >
            // side  = < #, #, # >

            // Extract yaw rotation
            yaw = - Math.atan2(basis.x, basis.z);
            Vec3 basis1 = OotilityVectors.yawRotate(basis, -yaw);   // = < 0, #, # >
            Vec3 sense1 = OotilityVectors.yawRotate(sense, -yaw);   // = < #, #, # >
            Vec3 side1 = OotilityVectors.yawRotate(side, -yaw);     // = < #, #, # >

            // Extract pitch rotation
            pitch = - Math.atan2(basis1.y, basis1.z);
            //Vec3 basis2 = OotilityVectors.pitchRotate(basis1, -pitch); // = < 0, 0, 1 >
            //Vec3 sense2 = OotilityVectors.pitchRotate(sense1, -pitch); // = < #, #, 0 >
            Vec3 side2 = OotilityVectors.pitchRotate(side1, -pitch);   // = < #, #, 0 >

            // Extract roll rotation
            roll = Math.atan2(side2.y, side2.x);
            //Vec3 basis3 = OotilityVectors.rollRotate(basis2, -roll); // = < 0, 0, 1 >
            //Vec3 sense3 = OotilityVectors.rollRotate(sense2, -roll); // = < 0, 1, 0 >
            //Vec3 side3 = OotilityVectors.rollRotate(side2, -roll);   // = < 1, 0, 0 >

        /*
         * The entire component of the basis is in the Y-direction, that means we cant use
         * it to determine the yaw. However, the sense vector is then flat horizontal and
         * ripe to be used for calculating the yaw
         */
        } else {

            // The idea here is to calculate yaw as the angle to revert side to align with the X-axis
            // basis = < 0, B, 0 >, where B is either +1 or -1
            // sense = < #, 0, # >
            // side  = < #, 0, # >

            // Extract yaw rotation
            yaw = Math.atan2(side.z, side.x);
            //Vec3 basis1 = OotilityVectors.yawRotate(basis, -yaw); // = < 0, B, 0 >, no change
            //Vec3 sense1 = OotilityVectors.yawRotate(sense, -yaw); // = < 0, 0, -B >
            //Vec3 side1 = OotilityVectors.yawRotate(side, -yaw);   // = < 1, 0, 0 >

            // Extract pitch rotation
            pitch = basis.y >= 0 ? (1.5 * Math.PI) : (0.5 * Math.PI); // = - Math.atan2(basis1.y, basis1.z) = - Math.atan2(basis1.y, 0)
            //Vec3 basis2 = OotilityVectors.pitchRotate(basis1, -pitch);  // = < 0, 0, 1 >
            //Vec3 sense2 = OotilityVectors.pitchRotate(sense1, -pitch);  // = < 0, 1, 0 >
            //Vec3 side2 = OotilityVectors.pitchRotate(side1, -pitch);    // = < 1, 0, 0 >, no change

            // Extract roll rotation
            roll = 0;
        }

        long lastUpdated = holdableTiny.actuallysize$getModelPartTime();
        if (SchedulingManager.getClientTicks() - lastUpdated > ASIPSModelPartInfo.PACKET_INTERVAL) {

            // Update client ticks
            holdableTiny.actuallysize$setModelPartTime(SchedulingManager.getClientTicks());

            // Send packet
            ASINetworkManager.playerToServer(new ASINSModelPartCoordinateSync((Entity) entityCounterpart, origin, pitch, yaw, roll));
        }

        // Skidush
        holdableTiny.actuallysize$getHeldModelPart().updateModelPart(origin, pitch, yaw, roll);
    }

    /**
     * The yaw and pitch must be calculated by choosing either of the basis vectors of the pose.
     * <br><br>
     * Ultimately it is the kind of thing that makes human sense more than mathematical sense,
     * as any of them really work and it is a matter of choosing a good convention or intuitive
     * set of axes.
     *
     * @return A vector that points in the FORWARD direction, no need to normalize it
     *
     * @param pose The pose in the last pose of the pose stack, most likely what you are interested in
     * @param part The model part, in case anything in there is needed
     * @param stack The pose stack, in case anything in there is needed
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull
    public abstract Vec3 getBasis(@NotNull ModelPart part, @NotNull PoseStack stack, @NotNull Matrix4f pose);

    /**
     * When reconstructing a pitch and a yaw from one vector, it is impossible to know if the
     * pitch "overflows" - the player camera is capped between 90° and -90°, but it is common
     * for other model parts to overflow this threshold. The forward vector is not affected,
     * but the side and vertical vectors have different signs.
     * <br><br>
     * We only really care about the Y component of it - if it is positive, the pitch has not
     * overflowed, and it has if the Y component of the vertical direction is negative.
     *
     * @return A vector that points in the VERTICAL direction, no need to normalize it
     *
     * @param pose The pose in the last pose of the pose stack, most likely what you are interested in
     * @param part The model part, in case anything in there is needed
     * @param stack The pose stack, in case anything in there is needed
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public abstract Vec3 getSense(@NotNull ModelPart part, @NotNull PoseStack stack, @NotNull Matrix4f pose);
}
