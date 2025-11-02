package actually.portals.ActuallySize.pickup.holding.pose.smol;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Vanilla poses do not offer enough configuration
 * as required by ASI, for some reason, anyway so
 * this interface allows any pose compatibility
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface ASIPSTinyPoseProfile {

    /**
     * @return If this pose is applied when vanilla pose
     *         is updated every tick in {@link Player#tick()}
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    boolean updatesVanillaPose();

    /**
     * @param tiny The player to apply this pose to.
     *             This will be called in {@link Player#tick()}
     *             if {@link #updatesVanillaPose()} returns true.
     *
     * @return If it was applied successfully
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    boolean applyPose(@NotNull Player tiny);
}
