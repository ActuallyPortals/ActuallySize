package actually.portals.ActuallySize.pickup.holding.pose.smol;

import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A simple pose profile that uses a vanilla
 * minecraft pose, gives the ability of crouching
 * to the tiny, and maybe sitting or whatever.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIPSVanillaPoseProfile implements ASIPSTinyPoseProfile {

    /**
     * The pose effective when the tiny is standing
     *
     * @since 1.0.0
     */
    @Nullable Pose standingPose;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @Nullable Pose getStandingPose() { return standingPose; }

    /**
     * The pose effective when the tiny is crouching
     *
     * @since 1.0.0
     */
    @Nullable Pose crouchingPose;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @Nullable Pose getCrouchingPose() { return crouchingPose; }

    /**
     * @param standingPose The pose effective when the tiny is standing
     * @param crouchingPose The pose effective when the tiny is crouching
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSVanillaPoseProfile(@Nullable Pose standingPose, @Nullable Pose crouchingPose) {
        this.standingPose = standingPose;
        this.crouchingPose = crouchingPose;
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override public boolean updatesVanillaPose() { return true; }

    /**
     * @param tiny The player being held by a beeg
     *
     * @return The crouching pose if crouching, or standing pose if standing
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable public Pose getEffectivePose(@NotNull Player tiny) { return tiny.isCrouching() ? crouchingPose : standingPose; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public boolean applyPose(@NotNull Player tiny) {

        // Ignored if null
        Pose pose = getEffectivePose(tiny);
        if (pose == null) { return false; }
        tiny.setPose(pose);
        return true;
    }
}
