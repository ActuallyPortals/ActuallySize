package actually.portals.ActuallySize.pickup.holding.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A wrapper for the information of a model part, because the
 * server-side does not have this at all (rendering code only
 * exists in the client dist, then it has to be synced over
 * the network) and because there's a TON of different model
 * part sources wtf
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIPSModelPartInfo {

    /**
     * The entity that owns this model part
     *
     * @since 1.0.0
     */
    @NotNull Entity modelEntity;

    /**
     * @param parent The entity that owns this model part
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSModelPartInfo(@NotNull Entity parent) {
        this.modelEntity = parent;
    }

    /**
     * The overridden origin, set during rendering
     *
     * @since 1.0.0
     */
    @Nullable Vec3 origin;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable public Vec3 getOrigin() { return origin; }

    /**
     * The overridden pitch, in radians, set during rendering when the model is drawn
     *
     * @since 1.0.0
     */
    double pitch;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public double getPitch() { return pitch; }

    /**
     * The overridden yaw, in radians, set during rendering when the model is drawn
     *
     * @since 1.0.0
     */
    double yaw;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public double getYaw() { return yaw; }

    /**
     * The overridden roll, in radians, set during rendering when the model is drawn
     *
     * @since 1.0.0
     */
    double roll;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public double getRoll() { return roll; }

    /**
     * @param position The origin of this model part
     * @param pitch The rotation this makes to the horizontal plane, in radians
     * @param yaw The rotation around the vertical axis, in radians
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void updateModelPart(@NotNull Vec3 position, double pitch, double yaw) {
        updateModelPart(position, pitch, yaw, 0);
    }

    /**
     * @param position The origin of this model part
     * @param pitch The rotation this makes to the horizontal plane, in radians
     * @param yaw The rotation around the vertical axis, in radians
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void updateModelPart(@NotNull Vec3 position, double pitch, double yaw, double roll) {
        this.origin = position;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }
}
