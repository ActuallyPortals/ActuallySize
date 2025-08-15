package actually.portals.ActuallySize.pickup.actions;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallyServerConfig;
import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.pickup.ASIPickupSystemManager;
import actually.portals.ActuallySize.pickup.events.ASIPSPickupToInventoryEvent;
import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import gunging.ootilities.GungingOotilitiesMod.exploring.entities.ISEEntityLocation;
import gunging.ootilities.GungingOotilitiesMod.exploring.entities.ISEExplorerStatements;
import gunging.ootilities.GungingOotilitiesMod.exploring.players.ISPExplorerStatements;
import gunging.ootilities.GungingOotilitiesMod.exploring.players.ISPPlayerLocation;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.APIFriendlyProcess;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The way this is used by design starts with capturing the
 * Player Interact Event:
 * <p>
 * (1) Capture the PlayerInteractEvent event
 * <p>
 * (2) Check that the player intended to pickup something with this event
 * <p>
 * (3) Check that the entity picked up can be picked up
 * <p>
 * (4) Run cancellable event to see if anything cancels it
 * <p>
 * (5) Actually resolve the pickup action
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIPSPickupAction implements APIFriendlyProcess {

    /**
     * If created with an event, the event it was created with
     *
     * @since 1.0.0
     */
    @Nullable PlayerInteractEvent.EntityInteract event;

    /**
     * The entity doing the picking up
     *
     * @since 1.0.0
     */
    @NotNull Entity beeg;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public Entity getBeeg() { return beeg; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public void setBeeg(@NotNull Player who) { beeg = who; }

    /**
     * The entity being picking up
     *
     * @since 1.0.0
     */
    @NotNull Entity tiny;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public Entity getTiny() { return tiny; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public void setTiny(@NotNull Entity who) { this.tiny = who; }

    /**
     * The slot where the entity is picked up to
     *
     * @since 1.0.0
     */
    @NotNull ItemStackLocation<? extends Entity> stackLocation;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public ItemStackLocation<? extends Entity> getStackLocation() { return stackLocation; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public void setStackLocation(@NotNull ItemStackLocation<? extends Entity> isl) { stackLocation = isl; }

    /**
     * If this pickup action should also trigger an Item-Entity duality activation,
     * and thus chain into it so that the entity is not deleted and rebuilt for no
     * reason (and also allowing Players to be Item-Entity dualities since those
     * cannot be destroyed and rebuilt)
     *
     * @since 1.0.0
     */
    boolean pickupAndHold = true;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public boolean isPickupAndHold() { return pickupAndHold; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public void setPickupAndHold(boolean andHold) { pickupAndHold = andHold; }

    /**
     * @param event A raw unfiltered event of a Player (presumably) right-clicking another entity
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSPickupAction(@NotNull PlayerInteractEvent.EntityInteract event) {
        this.event = event;

        // Identify entities
        this.beeg = event.getEntity();
        this.tiny = event.getTarget();

        // Build Item Stack Location corresponding to hand
        this.stackLocation = switch (event.getHand()) {
            case OFF_HAND -> new ISEEntityLocation(beeg, ISEExplorerStatements.OFFHAND);
            case MAIN_HAND -> new ISEEntityLocation(beeg, ISEExplorerStatements.MAINHAND);
        };
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSPickupAction(@NotNull ItemStackLocation<? extends Entity> location, @NotNull Entity tiny) {

        // Identify entities and location
        this.tiny = tiny;
        this.beeg = location.getHolder();
        this.stackLocation = location;
    }

    /**
     * @param beeg The entity doing the picking up
     * @param tiny The entity being picked up
     * @param hand Hand used to pick up this entity, a shorthand to easily choose hand
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSPickupAction(@NotNull Player beeg, @NotNull Entity tiny, @NotNull InteractionHand hand) {

        // Identify entities
        this.beeg = beeg;
        this.tiny = tiny;

        // Build Item Stack Location corresponding to hand
        this.stackLocation = switch (hand) {
            case OFF_HAND -> new ISPPlayerLocation(beeg, ISPExplorerStatements.OFFHAND);
            case MAIN_HAND -> new ISPPlayerLocation(beeg, ISPExplorerStatements.MAINHAND);
        };
    }

    /**
     * @param beeg The entity doing the picking up
     * @param tiny The entity being picked up
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSPickupAction(@NotNull Player beeg, @NotNull Entity tiny) {

        // Identify entities
        this.beeg = beeg;
        this.tiny = tiny;
        this.stackLocation = new ISPPlayerLocation(beeg, ISPExplorerStatements.MAINHAND);
    }

    /**
     * @return If an event was involved, checks event-specific information.
     *         If no event was involved this automatically passes true.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public boolean isEventAllowed() {
        if (event == null) { return true; }

        // Check that the caster is holding nothing in this hand
        return event.getItemStack().isEmpty();
    }

    /**
     * @return true, since all this requires to succeed is that the @NotNull annotations be respected... but please respect them!
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public boolean isVerified() { return true; }

    @Override
    public boolean isAllowed() {

        // Must be enabled in the config
        if (!ActuallyServerConfig.enableEntityPickup) { return false; }

        // Check against the event
        if (!isEventAllowed()) { return false; }

        //double relativeScale = ASIUtilities.inverseRelativeScale(caster, victim);
        //ActuallySizeInteractions.Log("&b&l Relativity: x" + (0.01 * Math.round((relativeScale * 100))));
        ItemEntityDualityHolder holder = (ItemEntityDualityHolder) beeg;
        ASIPSHoldPoint holdPoint = holder.actuallysize$getHoldPoint(stackLocation);

        // Must be big enough to pick up in the hold point
        if (holdPoint != null && !holdPoint.canSustainHold(holder, (EntityDualityCounterpart) tiny)) {
            //ActuallySizeInteractions.Log("&c&l Too small to pick up this tiny :pleading eyes:?");
            return false; }

        // Sanity check for range yes
        if (!ASIUtilities.inInteractionRange(getBeeg(), getTiny())) {
            //ActuallySizeInteractions.Log("&c&l Out ranged!");
            return false; }

        // If the tiny is currently being held
        EntityDualityCounterpart entityDuality = (EntityDualityCounterpart) tiny;
        ItemEntityDualityHolder otherBeeg = entityDuality.actuallysize$getItemEntityHolder();
        ASIPSHoldPoint otherBeegHoldPointOptions = entityDuality.actuallysize$getHoldPoint();
        if (otherBeegHoldPointOptions != null && otherBeeg != null) {

            // Kinda silly to pickup something you are already holding
            if (otherBeeg.equals(beeg)) { return false; }

            // The slot they are in might prevent them from being yoinked
            if (!otherBeegHoldPointOptions.canBeEscapedByStealing(otherBeeg, entityDuality, (ItemEntityDualityHolder) beeg)) {
                //ActuallySizeInteractions.Log("&c&l Yoink Security!");
                return false; }
        }

        // Run event so whoever cares about it may, you know, override it
        ASIPSPickupToInventoryEvent event = new ASIPSPickupToInventoryEvent(getBeeg(), getTiny(), getStackLocation());
        return !MinecraftForge.EVENT_BUS.post(event);
    }

    @Override
    public void resolve() {
        if (getTiny() instanceof Player) {
            resolveForPlayer((Player) getTiny());
        } else {
            resolveForNonPlayer();
        }
    }

    /**
     * The logic involved in preparing the item pickup for a
     * player, as well as picking up the actual player entity.
     *
     * @param tinyCast The tiny entity, cast as a player
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void resolveForPlayer(@NotNull Player tinyCast) {
        /*HDA*/ActuallySizeInteractions.Log("ASI &b HDP &7 Picking up player " + tiny.getScoreboardName());

        // Is it an entity duality? YOINK then? LMAO
        EntityDualityCounterpart entityDuality = (EntityDualityCounterpart) tiny;
        if (entityDuality.actuallysize$isActive()) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &b HDP &7 Player escaping from previous hold...");

            // It must escape before we can pick it up. Force escape this entity
            ASIPSDualityEscapeAction escape = new ASIPSDualityEscapeAction(entityDuality);
            if (escape.isVerified()) { escape.resolve(); }
        }

        // Create item and pop to slot
        ItemStack drop = ASIPickupSystemManager.generateHeldItem(tinyCast);
        stackLocation.setItemStack(drop);
        drop.setPopTime(5);
        /*HDA*/ActuallySizeInteractions.Log("ASI &b HDP &7 Given player item");

        // Delete the item if activation fails
        if (!chainIntoActivation(drop)) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &b HDP &c Cancelled player item");
            drop.setCount(0); }

        /*HDA*/ActuallySizeInteractions.Log("ASI &b HDP &a Entity pickup COMPLETED ");
        /*HDA*/ActuallySizeInteractions.Log("ASI &b HDP &a + Item: &f " + drop.getCount() + "x " + drop.getDisplayName().getString());
        /*HDA*/ActuallySizeInteractions.Log("ASI &b HDP &a + Tiny: &f " + tinyCast.getScoreboardName());
        /*HDA*/ActuallySizeInteractions.Log("ASI &b HDP &a + Beeg: &f " + getBeeg().getScoreboardName());
        /*HDA*/ActuallySizeInteractions.Log("ASI &b HDP &a + Tiny active: &f " + ((EntityDualityCounterpart) tinyCast).actuallysize$isActive());
        /*HDA*/ActuallySizeInteractions.Log("ASI &b HDP &a + Tiny held: &f " + ((EntityDualityCounterpart) tinyCast).actuallysize$isHeld());
        /*HDA*/ActuallySizeInteractions.Log("ASI &b HDP &a + Tiny root holder: &f " + ((Entity) ((EntityDualityCounterpart) tinyCast).actuallysize$getRootDualityHolder()).getScoreboardName());
        /*HDA*/ActuallySizeInteractions.Log("ASI &b HDP &a + Tiny item count: &f " + ((EntityDualityCounterpart) tinyCast).actuallysize$getItemCounterpart());
    }

    /**
     * The logic involved in preparing the item pickup for a
     * non-player entity, and optionally holding it too rather
     * than just an inventory item.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void resolveForNonPlayer() {
        /*HDA*/ActuallySizeInteractions.Log("ASI &b HDP &7 Picking up " + tiny.getScoreboardName());

        // Is it an entity duality? YOINK then? LMAO
        EntityDualityCounterpart entityDuality = (EntityDualityCounterpart) tiny;
        if (entityDuality.actuallysize$isActive()) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &b HDP &7 Escaping from previous hold...");

            // It must escape before we can pick it up. Force escape this entity
            ASIPSDualityEscapeAction escape = new ASIPSDualityEscapeAction(entityDuality);
            if (escape.isVerified()) { escape.resolve(); }
        }

        // Create item
        ItemStack drop = ASIPickupSystemManager.generateHeldItem(tiny);
        stackLocation.setItemStack(drop);
        drop.setPopTime(5);
        /*HDA*/ActuallySizeInteractions.Log("ASI &b HDP &7 Given drop!");

        // Delete the entity from the world (real)
        if (!isPickupAndHold() || !chainIntoActivation(drop)) {
            getTiny().remove(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        }
        /*HDA*/ActuallySizeInteractions.Log("ASI &b HDP &a Entity pickup COMPLETED ");
    }

    /**
     * Rather than destroying the entity, try to chain it into an Item-Entity duality
     * activation. This way we will not destroy it and immediately rebuild it lol, or
     * most importantly, we can pick up an entity that must not be destroyed.
     *
     * @return If it failed, in which case we will still destroy the Entity as normal.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    boolean chainIntoActivation(@NotNull ItemStack item) {

        // Build the action
        ASIPSDualityActivationAction action = new ASIPSDualityActivationAction(stackLocation, item, tiny);
        /*HDA*/ActuallySizeInteractions.Log("ASI &b HDP &7 Chaining activation...");

        // Verify it and run it
        return action.tryResolve();
    }
}
