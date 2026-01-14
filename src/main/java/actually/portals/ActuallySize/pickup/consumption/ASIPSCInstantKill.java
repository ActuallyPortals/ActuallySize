package actually.portals.ActuallySize.pickup.consumption;

import actually.portals.ActuallySize.pickup.actions.ASIPSDualityEscapeAction;
import actually.portals.ActuallySize.pickup.mixininterfaces.Edacious;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;

/**
 * A consumption effect that instantly kills the player
 * being consumed, even bypassing creative mode because
 * that is crazy lmao
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIPSCInstantKill implements ASIPSPlayerConsumption {

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public void snack(@NotNull ServerPlayer beeg, @NotNull ServerPlayer tiny) {

        // Break the entity-duality link between item and entity
        ASIPSDualityEscapeAction action = new ASIPSDualityEscapeAction((EntityDualityCounterpart) tiny);
        action.setAndRemoveItem(false);
        action.tryResolve();
        Edacious edacious = (Edacious) tiny;

        // Deal fatal amount of damage and record the food properties of all this player' drops
        tiny.setLastHurtByPlayer(beeg);
        edacious.actuallysize$setWasConsumed(true);
        tiny.hurt(new DamageSource(tiny.damageSources().genericKill().typeHolder(), beeg), 512 * tiny.getMaxHealth());
        edacious.actuallysize$setWasConsumed(false);
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override public boolean eats() { return true; }
}
