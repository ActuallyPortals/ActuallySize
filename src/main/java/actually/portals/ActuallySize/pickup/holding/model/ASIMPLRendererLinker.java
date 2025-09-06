package actually.portals.ActuallySize.pickup.holding.model;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.netcode.ASINetworkManager;
import actually.portals.ActuallySize.netcode.packets.serverbound.ASINSModelPartCoordinateSync;
import actually.portals.ActuallySize.pickup.mixininterfaces.*;
import com.mojang.blaze3d.vertex.PoseStack;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
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
    public ASIMPLRendererLinker(@NotNull String part) {
        modelPartName = part;
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public boolean appliesTo(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityCounterpart, @NotNull Object context) {
        return holder instanceof Entity && context instanceof ModelPart && ((VASIModelPart) (Object) context).actuallysize$getModelName().equals(getModelPartName());
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
        double pitch = Math.asin(- basis.y); // Normally  Math.asin(- basis.y / basis.length), but it's been normalized
        double lenXZ = Math.sqrt((basis.x * basis.x) + (basis.z * basis.z));
        double yaw;
        if (lenXZ > 0) {
            yaw = Math.acos(basis.z / lenXZ);

            // Arccosine only works for half of the circle
            if (basis.x > 0) { yaw = -yaw; }

        /*
         * The entire component of the basis is in the Y-direction, that means we cant use
         * it to determine the yaw. However, the sense vector is then flat horizontal and
         * ripe to be used for calculating the yaw
         */
        } else {
            lenXZ = Math.sqrt((sense.x * sense.x) + (sense.z * sense.z));
            yaw = Math.acos(sense.z / lenXZ);
            if (basis.y > 0) { yaw = yaw + Math.PI; } // The vertical direction is 180° off when forward points up

            // Arccosine only works for half of the circle
            if (sense.x > 0) { yaw = -yaw; }

        }
        if (sense.y < 0) { pitch = Math.PI - pitch; yaw = yaw + Math.PI; }

        // Calculate roll based on the side vector
        Vec3 side = sense.cross(basis);
        Vec3 sideFlat = new Vec3(side.x, 0, side.z).normalize();
        double dot = OotilityNumbers.round((side.x * sideFlat.x) + (side.z * sideFlat.z), 6);
        double roll = Math.acos(dot); // From dot product with its horizontal-plane (XZ) projection
        if (side.y < 0) { roll = -roll; }

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
