package actually.portals.ActuallySize.pickup.holding.points;

import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ASIPSRelativeFacingHold extends RelativeCoordinateHold {

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
    public ASIPSRelativeFacingHold(@NotNull ResourceLocation nk, double sideOffset, double verticalOffset, double forwardOffset, double xOffset, double yOffset, double zOffset) {
        super(nk, sideOffset, verticalOffset, forwardOffset, xOffset, yOffset, zOffset);
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
    public ASIPSRelativeFacingHold(@NotNull ResourceLocation nk, double sideOffset, double verticalOffset, double forwardOffset, double levelOffset, double xOffset, double yOffset, double zOffset) {
        super(nk, sideOffset, verticalOffset, forwardOffset, levelOffset, xOffset, yOffset, zOffset);
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
