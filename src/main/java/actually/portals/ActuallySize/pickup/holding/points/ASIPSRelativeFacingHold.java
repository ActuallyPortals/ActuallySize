package actually.portals.ActuallySize.pickup.holding.points;

import actually.portals.ActuallySize.pickup.holding.pose.smol.ASIPSTinyPoseProfile;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.SVFLBit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ASIPSRelativeFacingHold extends RelativeCoordinateHold {

    /**
     * A ride point relative to the holder's pitch, yaw, and world coordinates.
     *
     * @param nk The namespaced key to name this slot
     * @param svf The SVF coordinate information
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIPSRelativeFacingHold(@NotNull ResourceLocation nk, @Nullable ASIPSTinyPoseProfile tinyPose, @NotNull SVFLBit svf) {
        super(nk, tinyPose, svf);
    }

    @Override
    public Vec3 getOrigin(@NotNull Entity holder, @NotNull EntityDualityCounterpart entityDuality) {
        return holder.position();
    }

    @Override
    public double getPitch(@NotNull Entity holder, @NotNull EntityDualityCounterpart entityDuality) {
        return Math.toRadians(holder.getXRot());
    }

    @Override
    public double getYaw(@NotNull Entity holder, @NotNull EntityDualityCounterpart entityDuality) {
        return Math.toRadians(holder.getYRot());
    }
}
