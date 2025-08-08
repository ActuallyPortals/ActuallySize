package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.pickup.ASIPickupSystemManager;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityDeactivationAction;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityEscapeAction;
import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSHoldPointRegistry;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSRegisterableHoldPoint;
import actually.portals.ActuallySize.pickup.item.ASIPSHeldEntityItem;
import actually.portals.ActuallySize.pickup.mixininterfaces.*;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import net.minecraft.commands.CommandSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityMixin extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity> implements Nameable, EntityAccess, CommandSource, net.minecraftforge.common.extensions.IForgeEntity, ItemEntityDualityHolder, EntityDualityCounterpart, SetLevelExt, RenderNormalizable, HoldTickable {

    @Shadow private Level level;

    @Shadow public abstract Level level();

    @Shadow public abstract @NotNull UUID getUUID();

    @Shadow public abstract void remove(Entity.RemovalReason pReason);

    @Shadow public abstract void setDeltaMovement(Vec3 pDeltaMovement);

    @Shadow public abstract void tick();

    @Shadow public abstract String getScoreboardName();

    protected EntityMixin(Class<Entity> baseClass) { super(baseClass); }

    @Unique
    @Nullable ItemStack actuallysize$itemCounterpart;
    @Unique
    @Nullable ItemEntityDualityHolder actuallysize$dualityHolderCounterpart;

    @Unique
    @Nullable Double actuallysize$preNormalizedScale = null;

    @Override
    public @Nullable ItemStack actuallysize$getItemCounterpart() {
        return actuallysize$itemCounterpart;
    }

    @Override
    public @Nullable ItemEntityDualityHolder actuallysize$getItemEntityHolder() {
        return actuallysize$dualityHolderCounterpart;
    }

    @Override
    public @NotNull ItemEntityDualityHolder actuallysize$getRootDualityHolder() {

        // I have no holder? Then I am the root
        ItemEntityDualityHolder root = actuallysize$getItemEntityHolder();
        if (root == null) { return this; }

        // My holder is not an Entity Counterpart? Then that has to be the root
        if (!(root instanceof EntityDualityCounterpart)) { return root; }

        // My holder is an Entity Counterpart, ask them who is their root
        return ((EntityDualityCounterpart) root).actuallysize$getRootDualityHolder();
    }

    @Override
    public void actuallysize$setItemCounterpart(@Nullable ItemStack who) {
        actuallysize$itemCounterpart = who;
    }

    @Override
    public void actuallysize$setItemEntityHolder(@Nullable ItemEntityDualityHolder who) {
        if (who == null) { actuallysize$holdPoint = null; }
        actuallysize$dualityHolderCounterpart = who;
    }

    @Inject(method = "baseTick", at = @At("HEAD"))
    public void onRideEscape(CallbackInfo ci) {
        //actuallysize.Log("ASI &3 ORE &7 Base Tick for " + getScoreboardName() + ", Active? " + actuallysize$isActive() + ", Held? " + actuallysize$isHeld());

        // If I am being held
        ItemEntityDualityHolder holder = actuallysize$getItemEntityHolder();
        if (holder instanceof Entity) {

            // Escape if the holder agonized
            if (((Entity) holder).isRemoved()) { actuallysize$escapeDuality(); }
        }
    }

    @Inject(method = "rideTick", at = @At("HEAD"), cancellable = true)
    public void onRideTick(CallbackInfo ci) {

        // When it is held, this is a HOLD TICK not a RIDE TICK
        if (actuallysize$isHeld()) {

            // Redirect this call to Hold Tick
            actuallysize$holdTick();
            ci.cancel();
        }
    }

    @Override
    public void actuallysize$holdTick() {

        // Tick self. If the holder died this also unregisters itself from being held
        if (canUpdate()) { this.tick(); }

        // Identify check
        ASIPSHoldPoint holdPoint = actuallysize$getHoldPoint();
        ItemEntityDualityHolder holder = actuallysize$getItemEntityHolder();
        if (holdPoint == null || holder == null) { return; }

        // Position tick
        holdPoint.positionHeldEntity(holder, this);
    }

    @Override
    public boolean actuallysize$isActive() {
        return actuallysize$getItemCounterpart() != null && actuallysize$getHoldPoint() != null;
    }

    @Unique
    @NotNull HashMap<ASIPSHoldPoint, EntityDualityCounterpart> actuallysize$heldItemCounterparts = new HashMap<>();

    @Override
    public @NotNull Map<ASIPSHoldPoint, EntityDualityCounterpart> actuallysize$getHeldEntityDualities() {
        return actuallysize$heldItemCounterparts;
    }

    @Override
    public @Nullable ASIPSHoldPoint actuallysize$getHoldPoint(@Nullable Object index) {
        //HDA//ActuallySizeInteractions.Log("ASI &6 EMX-HDA &7 (" + getClass().getSimpleName() + ") Looking for matching hold point for " + (index == null ? "" : index.getClass().getSimpleName() + " ") + index);

        // First try local registry if possible
        if (this instanceof HoldPointConfigurable) {
            ASIPSHoldPointRegistry reg = ((HoldPointConfigurable) this).actuallysize$getLocalHoldPoints();
            ASIPSRegisterableHoldPoint tried = reg.getHoldPoint(index);
            //HDA//ActuallySizeInteractions.Log("ASI &6 EMX-HDA &7 As configurable... &f " + (tried == null ? "" : tried.getClass().getSimpleName() + " ") + (tried == null ? "null" : tried.getNamespacedKey()));
            //HDR//reg.log();

            if (tried != null) { return tried; }
        }

        // By default, find this hold point in the indices
        return ASIPickupSystemManager.HOLD_POINT_REGISTRY.getHoldPoint(index);
    }

    @Unique
    @Nullable ItemStackLocation<? extends Entity> actuallysize$dualityStackLocation;

    @Override
    public @Nullable ItemStackLocation<? extends Entity> actuallysize$getItemStackLocation() {
        return actuallysize$dualityStackLocation;
    }
    @Override
    public void actuallysize$setItemStackLocation(@Nullable ItemStackLocation<? extends Entity> who) {

        // Set
        actuallysize$dualityStackLocation = who;
    }

    @Override
    public void actuallysize$setHeldItemEntityDuality(@NotNull ASIPSHoldPoint slot, @Nullable EntityDualityCounterpart dualityEntity) {

        /*
         * #2   Deactivate the previous Item-Entity in this slot of the holder
         */
        EntityDualityCounterpart oldEntityCounterpart = actuallysize$getHeldItemEntityDuality(slot);
        if (oldEntityCounterpart != null) {

            /*
             *  Remove from my map, to prevent infinite looping actually
             *
             *  Calling the ASIHDDualityDeactivationAction method below will
             *  attempt to call this again, recursively, except now this has
             *  been removed, and thus it will not find an old duality.
             */
            actuallysize$heldItemCounterparts.remove(slot);

            //HDA//ActuallySizeInteractions.Log("ASI &6EMX-HDA &r Removing old duality in this same slot... ");

            // Remove the old one from everywhere... else
            ASIPSDualityDeactivationAction oldHandler = new ASIPSDualityDeactivationAction(oldEntityCounterpart);
            if (oldHandler.tryResolve()) {

                //HDA//ActuallySizeInteractions.Log("ASI &6EMX-HDA &r Removed old. ");
            } else {

                //HDA//ActuallySizeInteractions.Log("ASI &6EMX-HDA &c Did not remove old. ");
            }
        }

        /*
         * #3   Register this Item-Entity duality onto the holder
         */
        if (dualityEntity == null) { return; }

        //HDA//ActuallySizeInteractions.Log("ASI &6EMX-HDA &r Activating new &b " + slot + " &7 " + ((Entity) dualityEntity).getScoreboardName());

        // Remember in slot
        actuallysize$heldItemCounterparts.put(slot, dualityEntity);
    }

    @Override
    public @Nullable EntityDualityCounterpart actuallysize$getHeldItemEntityDuality(@NotNull ASIPSHoldPoint slot) {
        return actuallysize$heldItemCounterparts.get(slot);
    }

    @ModifyExpressionValue(method = "shouldBeSaved", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isPassenger()Z"))
    public boolean onShouldSavePassengers(boolean original) {

        // If it is a passenger already, no need to evaluate further
        if (original) { return true; }

        // Return true if I am active.
        return actuallysize$isActive();
    }

    @ModifyExpressionValue(method = "save", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isPassenger()Z"))
    public boolean onSavePassengers(boolean original) {

        // If it is a passenger already, no need to evaluate further
        if (original) { return true; }

        // Return true if I am active.
        return actuallysize$isActive();
    }

    @Override
    public boolean actuallysize$isIndirectlyHolding(@NotNull EntityDualityCounterpart who) {

        // Is that entity even being held? No? Then I cannot be holding it
        if (!who.actuallysize$isActive()) { return false; }

        // Okay check their holder
        ItemEntityDualityHolder holder = who.actuallysize$getItemEntityHolder();

        // If their holder is not an entity, then it cannot be me
        if (!(holder instanceof EntityDualityCounterpart)) { return false; }

        // Is that me? Then yes I am holding that entity
        if (holder == this) { return true; }

        // Maybe I am holding the holder, who knows
        return actuallysize$isIndirectlyHolding((EntityDualityCounterpart) holder);
    }

    @Override
    public @NotNull Level actuallysize$getHolderWorld() {
        Level lev = level;
        if (lev == null) { throw new UnsupportedOperationException("ItemEntities can only be held by an Entity that is currently spawned on the world. "); }
        return lev;
    }

    @Unique
    CompoundTag actuallysize$entityDualityTags;

    @Nullable @Unique ASIPSHoldPoint actuallysize$holdPoint;

    @Override
    public void actuallysize$escapeDuality() {
        if (!actuallysize$isActive()) { return; }

        /*
         * Literally just call the ASI Pickup System Duality Escape action
         *
         * This action will handle everything needed for the entity to stop
         * being held by whatever is currently holding them, delete its item
         * counterpart
         */
        ASIPSDualityEscapeAction action = new ASIPSDualityEscapeAction(this);
        action.tryResolve();
    }

    @Override
    public void actuallysize$deactivateDuality() {
        if (!actuallysize$isActive()) { return; }

        /*
         * Literally just call the ASI Pickup System Duality Deactivation action
         *
         * This action will handle everything needed for the entity to return
         * to the item it is represented by in whoever's inventory
         */
        ASIPSDualityDeactivationAction action = new ASIPSDualityDeactivationAction(this);
        action.tryResolve();
    }

    @Override
    public void actuallysize$escapeAllDualities() {
        ArrayList<EntityDualityCounterpart> held = new ArrayList<>(actuallysize$heldItemCounterparts.values());
        for (EntityDualityCounterpart entityDuality : held) { entityDuality.actuallysize$escapeDuality(); }
    }

    @Override
    public void actuallysize$deactivateAllDualities() {
        ArrayList<EntityDualityCounterpart> held = new ArrayList<>(actuallysize$heldItemCounterparts.values());
        for (EntityDualityCounterpart entityDuality : held) { entityDuality.actuallysize$deactivateDuality(); }
    }

    @Override
    public boolean actuallysize$isHeld() { return actuallysize$isActive() && !actuallysize$getHoldPoint().isVirtualHoldPoint(); }

    @Override
    public @Nullable ASIPSHoldPoint actuallysize$getHoldPoint() { return actuallysize$holdPoint; }

    @Override
    public void actuallysize$setHoldPoint(@Nullable ASIPSHoldPoint point) { actuallysize$holdPoint = point; }

    @Override
    public @NotNull CompoundTag actuallysize$getEntityDualityTags() {
        if (actuallysize$entityDualityTags == null) { actuallysize$entityDualityTags = new CompoundTag(); }
        return actuallysize$entityDualityTags;
    }

    @Override
    public void actuallysize$SetWorld(@NotNull Level world) {
        this.level = world;
    }

    @Override
    public void actuallysize$setPreNormalizedScale(boolean release) {

        // Begin by clear it
        actuallysize$preNormalizedScale = null;

        // Done if releasing
        if (release) { return; }

        // Store the scale as dictated by the ASI Utilities
        actuallysize$preNormalizedScale = ASIUtilities.getEntityScale((Entity) (Object) this);
    }

    @Override
    public boolean actuallysize$isScaleNormalized() {
        return actuallysize$preNormalizedScale != null;
    }

    @Override
    public double actuallysize$getPreNormalizedScale() {
        return actuallysize$preNormalizedScale == null ? ASIUtilities.getEntityScale((Entity) (Object) this) : actuallysize$preNormalizedScale;
    }

    @ModifyExpressionValue(method = "isInWall", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;noPhysics:Z"))
    public boolean onSuffocateInBlockHasPhysics(boolean original) {

        /*
         *  This returns TRUE if suffocation in the wall is avoided.
         *
         *  Being held by a beeg results in immunity to suffocation
         */

        return original || actuallysize$isHeld();
    }

    @WrapMethod(method = "isPassengerOfSameVehicle")
    public boolean isPassengerOfSameVehicle(Entity pEntity, Operation<Boolean> original) {

        // It is passenger of the same vehicle or held by the same beeg
        return original.call(pEntity) || (actuallysize$getRootDualityHolder() == ((EntityDualityCounterpart) pEntity).actuallysize$getRootDualityHolder());
    }

    @WrapMethod(method = "canRide")
    public boolean onTryRide(Entity pVehicle, Operation<Boolean> original) {

        // Ask the hold point if this slot can be escaped this way
        ASIPSHoldPoint hold = actuallysize$getHoldPoint();
        ItemEntityDualityHolder holder = actuallysize$getItemEntityHolder();
        if (hold == null || holder == null || hold.canBeEscapedByRiding(holder, this)) { return original.call(pVehicle); }

        // Hold point says you cannot ride away!
        return false;
    }

    @WrapOperation(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;stopRiding()V"))
    public void onStartRiding(Entity instance, Operation<Void> original) {

        // Stop riding and escape are called here
        actuallysize$escapeDuality();
        original.call(instance);
    }

    @WrapOperation(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isPassenger()Z"))
    public boolean onMovementEmissionsIsPassenger(Entity instance, Operation<Boolean> original) {

        // If held, then we basically are a passenger
        if (actuallysize$isHeld()) { return true; }

        // Otherwise just call the original
        return original.call(instance);
    }
}
