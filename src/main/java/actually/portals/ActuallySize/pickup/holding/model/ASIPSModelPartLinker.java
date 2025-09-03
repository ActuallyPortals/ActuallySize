package actually.portals.ActuallySize.pickup.holding.model;

import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import org.jetbrains.annotations.NotNull;

/**
 * An implementation-dependent link between model part and hold point, because
 * model parts exist in many different contexts.
 * <br><br>
 * Take for example the main hand, for zombies and players it uses the humanoid
 * model class to figure out its origin and orientations, but when a Fox holds
 * an item it suddenly changes to a completely different part of the minecraft
 * code. Then, players may use Figura or some other avatar manager to modify
 * their mainhand, and it is again, completely different.
 * <br><br>
 * Basically, any number of external implementations may bid their model part
 * linker to each hold slot, and preferentially use them based on context.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface ASIPSModelPartLinker {

    /**
     * Only the first applicable part linker is ever returned, and if
     * the current context does not apply to it, it is not used yet.
     * To get the highest priority, register your model part linker earlier.
     *
     * @param holder The holder of this entity-item duality
     * @param entityCounterpart The entity being held in this hold slot
     *
     * @return If this part linker is applicable between these two.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
     boolean appliesTo(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityCounterpart, @NotNull Object context);
}
