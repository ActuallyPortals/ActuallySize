package actually.portals.ActuallySize.pickup.consumption;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * An interface that deals when player item-entities are consumed.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface ASIPSPlayerConsumption {

    /**
     * @param beeg The player doing the consuming
     * @param tiny The player being consumed
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void snack(@NotNull ServerPlayer beeg, @NotNull ServerPlayer tiny);
}
