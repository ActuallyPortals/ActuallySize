package actually.portals.ActuallySize.pickup.holding.points;

import actually.portals.ActuallySize.pickup.holding.pose.ASIPSTinyPosedHold;
import actually.portals.ActuallySize.pickup.holding.pose.smol.ASIPSTinyPoseProfile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The standard ASI implementation of hold points, with all the
 * capabilities ASI is expected to provide of them.
 */
public abstract class ASIPSImplementedHoldPoint extends ASIPSRegisterableHoldPoint implements ASIPSTinyPosedHold {

    /**
     * @param namespacedKey The name of this hold point
     * @author Actually Portals
     *
     * @param tinyPose The pose of players held in this hold point
     *
     * @since 1.0.0
     */
    public ASIPSImplementedHoldPoint(@NotNull ResourceLocation namespacedKey, @Nullable ASIPSTinyPoseProfile tinyPose) { super(namespacedKey); this.tinyPose = tinyPose; }

    /**
     * The pose that players held in this hold point have
     *
     * @since 1.0.0
     */
    @Nullable ASIPSTinyPoseProfile tinyPose;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public @Nullable ASIPSTinyPoseProfile getTinyPose(@NotNull Player tiny) { return tinyPose; }
}
