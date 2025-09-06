package actually.portals.ActuallySize.pickup.holding.model;

import actually.portals.ActuallySize.ASIUtilities;
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
     * How often a model part information is synced to the server
     *
     * @since 1.0.0
     */
    public static final long PACKET_INTERVAL = 30;

    /**
     * After how long of no updates does a model part info invalidate
     *
     * @since 1.0.0
     */
    public static final long TIMEOUT_INTERVAL = 60;

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
     * The overridden origin, set during rendering. This
     * is the origin of the model part, in world coordinates,
     * relative to the position of the model entity.
     * <br><br>
     * Essentially, the true world coordinates would be obtained
     * by adding the position of the model entity to this.
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
     * @return If there is no information in this Model Part Info
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public boolean isEmpty() { return origin == null; }

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

    /**
     * Deletes all model part information in this model part info
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void reset() {
        origin = null;
        pitch = 0;
        yaw = 0;
        roll = 0;
    }

    /**
     * Any of the clients has sent us this, as their interpretation, of where
     * the model part is. Technically, this can be hijacked into free-flight
     * exploits and whatnot. Then the server must have the ability to reject
     * suspicious-looking information.
     *
     * @param position The alleged position of the model part
     * @param pitch The alleged pitch of the model part
     * @param yaw The alleged yaw of the model part
     * @param roll The alleged roll of the model part
     *
     * @return If this information was accepted to this Model Part Info
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public boolean serversideUpdateModelPart(@NotNull Vec3 position, double pitch, double yaw, double roll) {

        /*
         * The position is expectedly relative to the holder,
         * to be honest just as long as they are not too far
         * it is fine.
         *
         * Translated down by half of its eye height, because
         * having the origin be at the feet instead of the
         * middle between feet and eyes means a lot of underground
         * is accepted and very little overhead is.
         */
        double lenSqr = position.add(new Vec3(0, -0.5 * modelEntity.getEyeHeight(), 0)).lengthSqr();

        // Tinies and normal people have 3 blocks of reach space by default, scales the bigger you are
        double size = ASIUtilities.getEffectiveSize(modelEntity, false);
        double sizeSqr = 9;
        if (size > 1) { sizeSqr *= size * size; }

        // Cancel if this is too far
        if (lenSqr > sizeSqr) { return false; }

        // Accept
        updateModelPart(position, pitch, yaw, roll);
        return true;
    }
}
