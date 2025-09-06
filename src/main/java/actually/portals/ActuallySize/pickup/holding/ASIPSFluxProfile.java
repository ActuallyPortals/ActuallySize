package actually.portals.ActuallySize.pickup.holding;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.pickup.ASIPickupSystemManager;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityAction;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityDeactivationAction;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityEscapeAction;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityFluxAction;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Collects the probable flux data of a specific entity, indexed by UUID
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIPSFluxProfile {

    /**
     * Entity that is or will be the entity counterpart of the duality flux
     *
     * @since 1.0.0
     */
    @NotNull Entity entityCounterpart;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public Entity getEntityCounterpart() { return entityCounterpart; }

    /**
     * The actions in flux for this frame
     *
     * @since 1.0.0
     */
    @NotNull ArrayList<ASIPSDualityAction> actions = new ArrayList<>();

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public ArrayList<ASIPSDualityAction> getActions() { return actions; }

    /**
     * Includes this action in the actions list
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void add(@NotNull ASIPSDualityAction action) { actions.add(action); }

    /**
     * Adds all these actions to the actions list
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void addAll(@NotNull Collection<ASIPSDualityAction> action) { actions.addAll(action); }

    /**
     * @param entityCounterpart Entity that is or will be the entity counterpart of the duality flux
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSFluxProfile(@NotNull Entity entityCounterpart) {
        this.entityCounterpart = entityCounterpart;
    }

    /**
     * The origin of the flux action
     *
     * @since 1.0.0
     */
    @Nullable ASIPSDualityAction from;

    /**
     * The destination of the flux action
     *
     * @since 1.0.0
     */
    @Nullable ASIPSDualityAction to;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable public ASIPSDualityAction getFrom() { return from; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable public ASIPSDualityAction getTo() { return to; }

    /**
     * Evaluates all the actions to choose which is the origin and which is the destination
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void computeFlux() {

        // Changes in flux
        ASIPSDualityAction two = null;

        // Calculate the ultimate changes
        for (ASIPSDualityAction act : actions) {
            /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "RDF", " + &7 {0} &f {1}", act.getClass().getSimpleName(), (act.getStackLocation() == null ? "null" : act.getStackLocation().getStatement()));
            boolean isFrom = (act instanceof ASIPSDualityDeactivationAction) || (act instanceof ASIPSDualityEscapeAction);

            // This action is removing the entity from somewhere
            if (isFrom) {

                /*
                 * If this removes an immediate "to" of the same slot,
                 * it's kinda silly that they ever were added to that
                 * slot in the first place.
                 */
                if (to != null && act.getStackLocation() != null && act.getStackLocation().equals(to.getStackLocation())) {

                    // Invalidate to and act, since act is cancelling to
                    to.logAttempt(); act.logAttempt();
                    to.logAttempt(); act.logAttempt();

                    // Restore "to" to its previous state and ignore this interaction completely
                    to = two;
                    continue;
                }

                // When we haven't decided where we are from, only the very first ever logged in this cycle is accepted
                if (from == null) { from = act; } else { act.logAttempt(); act.logAttempt(); }

                // This action is placing the entity somewhere (activating it)
            } else {

                // Two is now invalidated
                if (two != null) { two.logAttempt(); two.logAttempt(); }

                // Even if we know where we are going, the final destination gets overwritten
                two = to;
                to = act;
            }
        }
    }

    /**
     * After computing flux, this will actually evaluate the flux action
     * if it could be built. If not, this will evaluate the components
     * separately, and at the very least defer them 1 frame into the future
     * in case the other flux component is accepted next frame.
     *
     * @return Residual or deferred actions that may be resolved next frame
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public ArrayList<ASIPSDualityAction> resolveFlux() {
        ArrayList<ASIPSDualityAction> flex = new ArrayList<>();
        /*HDA*/ActuallySizeInteractions.LogHDA(ASIPickupSystemManager.class, "RDF", "[FROM] &6 {0}", (from == null ? "null" : (from.getStackLocation() == null ? "UNKNOWN" : from.getStackLocation().getStatement())));
        /*HDA*/ActuallySizeInteractions.LogHDA(ASIPickupSystemManager.class, "RDF", "[TO] &e {0}", (to == null ? "null" : (to.getStackLocation() == null ? "UNKNOWN" : to.getStackLocation().getStatement())));

        // There is no destination?
        if (to == null) {

            // At least resolve from if it exists
            if (from != null) {

                // Give it one more pass and then let it resolve
                if (from.getAttempts() < 1) {
                    /*HDA*/ActuallySizeInteractions.LogHDA(ASIPickupSystemManager.class, "RDF", "Deferred [FROM] &f {0}", (from == null ? "null" : (from.getStackLocation() == null ? "UNKNOWN" : from.getStackLocation().getStatement())));
                    flex.add(from);
                    from.logAttempt();

                    // Time to resolve
                } else if (from.isAllowed()) { from.resolve(); }
            }

            // There is a destination
        } else {

            /*
             * Sometimes, an active entity duality swaps slot with another, then
             * both are registered to their [To] slots and no [From] slot. This
             * is undesirable, check if this entity is already active and thus
             * has a [From] slot.
             *
             * But also, this only makes sense if it will be allowed and verified.
             * Otherwise, we get a random deactivation event later on that is
             * extraneous.
             *
             * We can assume it is verified since it was accepted in probable flux.
             */
            if (from == null && to.isAllowed()) {
                EntityDualityCounterpart entityDuality = (EntityDualityCounterpart) entityCounterpart;
                if (entityDuality.actuallysize$isActive()) { from = new ASIPSDualityDeactivationAction(entityDuality); }
            }

            // At least resolve to, since it existed
            if (from == null) {

                // Give it one more pass and then let it resolve
                if (to.getAttempts() < 1) {
                    /*HDA*/ActuallySizeInteractions.LogHDA(ASIPickupSystemManager.class, "RDF", "Deferred [TO] &f {0}", (to == null ? "null" : (to.getStackLocation() == null ? "UNKNOWN" : to.getStackLocation().getStatement())));
                    flex.add(to);
                    to.logAttempt();

                    // Time to resolve
                } else if (to.isAllowed()) { to.resolve(); }

                // Both to and from exist
            } else {

                // Turn this into a FLUX action and run it
                ASIPSDualityFluxAction action = new ASIPSDualityFluxAction(from, to);
                if (action.isAllowed()) {
                    action.resolve();

                // If the flux fails to resolve, resolve the two parts individually
                } else {
                    from.tryResolve();
                    to.tryResolve();
                }
            }
        }

        return flex;
    }
}
