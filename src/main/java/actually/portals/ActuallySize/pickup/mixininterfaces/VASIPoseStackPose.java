package actually.portals.ActuallySize.pickup.mixininterfaces;

import com.mojang.blaze3d.vertex.PoseStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/**
 * Allows access to Pose inner class in Pose Stack class
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface VASIPoseStackPose {

    /**
     * @return An identity Pose Stack Pose
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull PoseStack.Pose actuallysize$identity();

    /**
     * @return An identity Pose Stack Pose
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull PoseStack.Pose actuallysize$dupe();

    /**
     * @return The 4f Matrix
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull Matrix4f actuallysize$pose();

    /**
     * @return The 3f Matrix
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull Matrix3f actuallysize$normal();
}
