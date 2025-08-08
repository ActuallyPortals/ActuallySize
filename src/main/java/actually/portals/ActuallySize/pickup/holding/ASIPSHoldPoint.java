package actually.portals.ActuallySize.pickup.holding;

import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import org.jetbrains.annotations.NotNull;

/**
 * <a href="https://discord.com/channels/643305700319297536/1301375936725254237/1319103823545110619">"There are other places for a smol..."</a>
 * <p>
 * A hold point is a position and rotation relative to the holder's
 * position and rotation, with various settings, to adjust the rendering
 * of the tiny held in that hold point.
 *
 * @author Actually Portals
 * @since 1.0.0
 */
public interface ASIPSHoldPoint {

    /**
     * Positions the held entity in the world relative to
     * their holder, whatever that means at this moment.
     * Called every world tick when the entity is ticked.
     *
     * @param holder The entity doing the holding
     * @param entityDuality The entity being held
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    void positionHeldEntity(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityDuality);

    /**
     * Positions the held entity in the world one last time
     * and grants them speed or velocity appropriate of being
     * thrown from this slot.
     *
     * @param holder The entity doing the holding
     * @param entityDuality The entity being held
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    void throwHeldEntity(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityDuality);

    /**
     * @param holder The entity doing the holding
     * @param entityDuality The entity being held
     *
     * @return If the tiny held in this slot can escape it by riding
     *         a nearby entity, such as a boat. If this is set to false,
     *         riding of entities will be blocked.
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    default boolean canBeEscapedByRiding(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityDuality) { return false; }

    /**
     * It is convention that server operators and admins should be able
     * to always yoink people off others. That's because it is funny that
     * an admin should be able to grab players out of other players' pockets.
     *
     * @param holder The entity doing the holding
     * @param entityDuality The entity being held
     * @param pickpocket The entity trying to steal this held entity
     *
     * @return If the tiny held in this slot can be picked up by
     *         that other entity (presumably a player!) trying to
     *         YOINK them.
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    default boolean canBeEscapedByStealing(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityDuality, @NotNull ItemEntityDualityHolder pickpocket) { return true; }

    /**
     * Some "Hold points" don't actually force the entity to be held in your person, but vaguely mean your
     * claim to their ownership, that they exist as items in your inventory. In those cases, it is a "virtual"
     * hold point since you are holding their item but not their entity.
     *
     * @return If this is a hold point only for Item-Entity-Duality activation system and not to actually be held
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    default boolean isVirtualHoldPoint() { return false; }
}
