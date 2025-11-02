package actually.portals.ActuallySize.pickup.actions;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoints;
import actually.portals.ActuallySize.pickup.item.ASIPSHeldEntityItem;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.GraceImpulsable;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import gunging.ootilities.GungingOotilitiesMod.exploring.entities.ISEEntityLocation;
import gunging.ootilities.GungingOotilitiesMod.exploring.entities.ISEExplorerStatements;
import gunging.ootilities.GungingOotilitiesMod.instants.GOOMPlayerMomentumSync;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This duality action handles the throwing of a
 * duality item that the player currently holds:
 * <br>
 * <br>(1) Escape duality action
 * <br>(2) Apply thrown velocity to entity
 * <br>(2) Delete item
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIPSDualityThrowAction extends ASIPSDualityAction {

    /**
     * @see #getStackLocation()
     * @since 1.0.0
     */
    @Nullable ItemStackLocation<? extends Entity> stackLocation;
    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public @Nullable ItemStackLocation<? extends Entity> getStackLocation() { return stackLocation; }
    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public void setStackLocation(@Nullable ItemStackLocation<? extends Entity> isl) { stackLocation = isl; }

    /**
     * @see #getEntityCounterpart()
     * @since 1.0.0
     */
    @Nullable Entity entityCounterpart;
    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public @Nullable Entity getEntityCounterpart() { return entityCounterpart; }
    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public void setEntityCounterpart(@Nullable Entity tiny) { entityCounterpart = tiny; }

    /**
     * @see #getItemCounterpart()
     * @since 1.0.0
     */
    @Nullable ItemStack itemCounterpart;
    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public @Nullable ItemStack getItemCounterpart() { return itemCounterpart; }
    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public void setItemCounterpart(@Nullable ItemStack item) { itemCounterpart = item; }

    /**
     * @see #getHoldPoint()
     * @since 1.0.0
     */
    @Nullable ASIPSHoldPoint holdPoint;
    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public @Nullable ASIPSHoldPoint getHoldPoint() { return holdPoint; }
    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public void setHoldPoint(@Nullable ASIPSHoldPoint hold) { holdPoint = hold; }

    /**
     * The player doing the throwing
     *
     * @since 1.0.0
     */
    @NotNull ServerPlayer beeg;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public ServerPlayer getBeeg() { return beeg; }

    /**
     * @param beeg The player doing the throwing
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSDualityThrowAction(@NotNull ServerPlayer beeg) {
        this.beeg = beeg;

        // Choose item and entity in main hand
        stackLocation = new ISEEntityLocation(beeg, ISEExplorerStatements.MAINHAND);
        itemCounterpart = stackLocation.getItemStack();
        if (itemCounterpart != null) {
            ItemDualityCounterpart itemDuality = (ItemDualityCounterpart) (Object) itemCounterpart;
            entityCounterpart = itemDuality.actuallysize$getEntityCounterpart();

            if (itemDuality.actuallysize$isDualityActive()) {
                entityCounterpart = itemDuality.actuallysize$getEntityCounterpart();

            } else {

                // Readies the enclosed entity for the non-player case
                Entity enclosed = itemDuality.actuallysize$getEnclosedEntity(beeg.level());
                if (enclosed != null) {

                    /*
                     * If it is added, then we'll pick it up in the next statement below,
                     * and if it is not picked up it will generate one with a brand-new
                     * UUID to be our entity counterpart.
                     *
                     * This statement will preferably create one with identical UUID to
                     * the enclosed entity, but this will conflict if it is already in
                     * the world.
                     */
                    if (!enclosed.isAddedToWorld()) { entityCounterpart = enclosed; }
                }

                // If it wasn't readied before, ready it now
                if (entityCounterpart == null) { entityCounterpart = itemDuality.actuallysize$readyEntityCounterpart(beeg.level()); }
            }
        }
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public boolean isVerified() {

        // Must have found the entity we will throw
        if (entityCounterpart == null) { return false; }
        if (beeg == null) { return false; }

        // Valid enough
        return true;
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public boolean isAllowed() {

        // Check the item to be valid
        if (itemCounterpart == null) { return false; }
        if (itemCounterpart.getCount() < 1) { return false; }
        if (itemCounterpart.getPopTime() > 0) { return false; }
        if (beeg.isCrouching()) { return false; }
        return true;
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public void resolve() {

        // Cant throw null entities
        if (entityCounterpart == null) { return; }
        boolean throwingPlayer = entityCounterpart instanceof Player;

        // Throw active entity duality
        ItemDualityCounterpart itemDuality = (ItemDualityCounterpart) (Object) itemCounterpart;
        EntityDualityCounterpart entityDuality = (EntityDualityCounterpart) itemDuality.actuallysize$getEntityCounterpart();
        if (entityDuality != null) {

            // Find its hold point
            ASIPSHoldPoint holdPoint = entityDuality.actuallysize$getHoldPoint();
            if (holdPoint == null) { holdPoint = ASIPSHoldPoints.RIGHT_HAND; }

            // Force new if creative (unless players, players cannot be duped)
            if (beeg.getAbilities().instabuild && !throwingPlayer) {
                Level level = beeg.level();
                Entity rebuilt = entityCounterpart;
                if (itemCounterpart != null && itemCounterpart.getItem() instanceof ASIPSHeldEntityItem) {
                    ASIPSHeldEntityItem asASI = (ASIPSHeldEntityItem) itemCounterpart.getItem();
                    rebuilt = asASI.counterpartOrRebuild(level, itemCounterpart, true, true); }
                if (rebuilt != null) { entityDuality = (EntityDualityCounterpart) rebuilt; } }

            // Throw from the hold point and escape
            holdPoint.throwHeldEntity((ItemEntityDualityHolder) beeg, entityDuality);
            entityDuality.actuallysize$escapeDuality();
            if (entityDuality instanceof Player) {

                // Add ticks of grace impulse
                GraceImpulsable imp = (GraceImpulsable) entityDuality;
                imp.actuallysize$addGraceImpulse(40);

                // If player, they must be momentum-notified
                GOOMPlayerMomentumSync sync = new GOOMPlayerMomentumSync((Player) entityDuality);
                sync.tryResolve();
            }

        // Rebuild entity and throw
        } else {

            // Rebuild entity
            Level level = beeg.level();
            Entity rebuilt = entityCounterpart;
            if (itemCounterpart != null && itemCounterpart.getItem() instanceof ASIPSHeldEntityItem) {
                ASIPSHeldEntityItem asASI = (ASIPSHeldEntityItem) itemCounterpart.getItem();
                rebuilt = asASI.counterpartOrRebuild(level, itemCounterpart, beeg.getAbilities().instabuild, true);
            }
            if (rebuilt == null) {
                //ActuallySizeInteractions.Log("PS SWING Rebuild Failure");
                return; }

            /*
             *  For whatever reason this item-entity is not active, even if
             *  thrown from the main hand. In that case, pretend this tiny
             *  was thrown from the main hand.
             */

            ASIPSHoldPoint simulation = ASIPSHoldPoints.RIGHT_HAND;
            simulation.throwHeldEntity((ItemEntityDualityHolder) beeg, (EntityDualityCounterpart) rebuilt);

            // Deploy (added to world before slot adjusts position)
            if (!rebuilt.isAddedToWorld()) { level.addFreshEntity(rebuilt); }
        }

        // Item count decrease when not in creative mode, or when throwing a player
        if (itemCounterpart != null) {
            if (!beeg.getAbilities().instabuild || throwingPlayer) { itemCounterpart.shrink(1); }
        }
    }
}
