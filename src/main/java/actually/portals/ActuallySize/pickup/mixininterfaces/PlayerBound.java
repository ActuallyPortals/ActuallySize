package actually.portals.ActuallySize.pickup.mixininterfaces;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * A class related to a player
 *
 * @author Actually Portals
 * @since 1.0.0
 */
public interface PlayerBound {

    /**
     * @return The player bound to this object
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @Nullable Player actuallysize$getBoundPlayer();

    /**
     * @param player The player bound to this object
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    void actuallysize$setBoundPlayer(@Nullable Player player);
}
