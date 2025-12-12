package actually.portals.ActuallySize.pickup.consumption;

import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * A consumption effect that instantly teleports the
 * tiny back to their home, somewhat of a watered-down
 * kill effect I guess lol.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIPSCRespawn implements ASIPSPlayerConsumption {

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public void snack(@NotNull ServerPlayer beeg, @NotNull ServerPlayer tiny) {

        // Escape
        ((EntityDualityCounterpart) tiny).actuallysize$escapeDuality();

        // Respawn lul
        tiny.server.getPlayerList().respawn(tiny, true);
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override public boolean eats() { return true; }
}
