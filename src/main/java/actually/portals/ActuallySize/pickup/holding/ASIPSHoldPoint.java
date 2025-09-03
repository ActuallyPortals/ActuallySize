package actually.portals.ActuallySize.pickup.holding;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.pickup.holding.model.ASIPSModelPartLinker;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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
public abstract class ASIPSHoldPoint {

    /**
     * Overrides the true entity position with a local
     * "virtual" position if you may, so that we can
     * line up animations regardless of the true server
     * position of the entity.
     * <br><br>
     *
     * @param holder The entity doing the holding
     * @param entityDuality The entity being held
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public void clientsidePositionHeldEntity(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityDuality) { serversidePositionHeldEntity(holder, entityDuality); }

    /**
     * @return If the {@link #clientsidePositionHeldEntity(ItemEntityDualityHolder, EntityDualityCounterpart)} is
     * to be used in local client, and therefore the true entity position in the server is currently ignored.
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public boolean isClientsidePositionable() { return true; }

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
    public abstract void serversidePositionHeldEntity(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityDuality);

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
    public abstract void throwHeldEntity(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityDuality);

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
    public boolean canBeEscapedByRiding(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityDuality) { return false; }

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
    public boolean canBeEscapedByTeleporting(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityDuality, @NotNull EntityTeleportEvent event) { return true; }

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
    public boolean canBeEscapedByStealing(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityDuality, @NotNull ItemEntityDualityHolder pickpocket) { return true; }

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
    public boolean isVirtualHoldPoint() { return false; }

    /**
     * Some entities have a special falling or agitated animation, or
     * maybe it is funny to hold them upside down or something. The
     * point is that this will visually hold entities in a more precarious
     * manner than a secure/relaxed hold.
     *
     * @return If this hold point dangles the tiny haphazardly
     *
     * @param holder The entity doing the holding
     * @param entityDuality The entity being held
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public boolean isDangling(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityDuality) { return false; }

    /**
     * Fascinatingly, we can make it so that you can glide off a slot with ease just
     * by using an elytra and attempting "fall-flying" which is pretty cool mechanic.
     *
     * @return If attempting to use an elytra from this slot will immediately release you
     *
     * @param holder The entity doing the holding
     * @param entityDuality The entity being held
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public boolean canBeGlidedOff(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityDuality) { return false; }

    /**
     * This method is evaluated twice per second, and will immediately
     * stop the hold status if when it returns false. For example, consider
     * the held tiny growing too big and breaking free from the grip of
     * the beeg.
     *
     * @return If the holder can keep holding the held entity
     *
     * @param holder The entity doing the holding
     * @param entityDuality The entity being held
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public boolean canSustainHold(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityDuality) {
        return !ASIUtilities.meetsScaleRequirement((Entity) entityDuality, (Entity) holder, 0.25);
    }

    /**
     * The model part links registered onto this hold point, may be empty.
     *
     * @see ASIPSModelPartLinker
     *
     * @since 1.0.0
     */
    @NotNull ArrayList<ASIPSModelPartLinker> partLinks = new ArrayList<>();

    /**
     * @param linker The model part linker you are registering
     *
     * @see ASIPSModelPartLinker
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public void registerPartLink(@NotNull ASIPSModelPartLinker linker) { partLinks.add(linker); }

    /**
     * @param of The class of type linker you are looking for in this context
     *
     * @return The model linker, if any is registered onto this hold point, of this type
     *
     * @param <ModelLinkerType> The class of the model linker applicable in this context.
     *
     * @see ASIPSModelPartLinker
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public <ModelLinkerType> ModelLinkerType getModelPartLinker(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityDuality, @NotNull Object context, @NotNull Class<ModelLinkerType> of) {

        // Iterate the part links we do have
        for (ASIPSModelPartLinker linker : partLinks) {

            // If applicable, return this
            if (linker.appliesTo(holder, entityDuality, context)) {

                // But only if it matches the type
                if (of.isInstance(linker)) { //noinspection unchecked
                    return (ModelLinkerType) linker; } else { return null; }
            }
        }

        // None found
        return null;
    }
}
