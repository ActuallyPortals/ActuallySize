package actually.portals.ActuallySize.pickup.holding.pose;

import actually.portals.ActuallySize.pickup.holding.pose.smol.ASIPSTinyPoseProfile;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A Hold Point that also modifies the pose of the
 * tiny while held in this Hold Point of the beeg
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface ASIPSTinyPosedHold {

    /**
     * Null is allowed, it means this hold point will not
     * override the player pose, which means the tiny will
     * most likely be in the normal vanilla standing pose
     *
     * @return The pose of the tiny while held in this hold point.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable ASIPSTinyPoseProfile getTinyPose(@NotNull Player tiny);
}
