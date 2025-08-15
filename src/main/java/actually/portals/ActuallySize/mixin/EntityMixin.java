package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallyServerConfig;
import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.pickup.ASIPickupSystemManager;
import actually.portals.ActuallySize.pickup.actions.*;
import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSHoldPointRegistry;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSRegisterableHoldPoint;
import actually.portals.ActuallySize.pickup.mixininterfaces.*;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemExplorerElaborator;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackExplorer;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import gunging.ootilities.GungingOotilitiesMod.scheduling.SchedulingManager;
import net.minecraft.commands.CommandSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Shadow public abstract void setSwimming(boolean pSwimming);

    @Shadow public abstract boolean isSwimming();

    @Shadow public abstract boolean isPassenger();

    @Shadow @javax.annotation.Nullable public abstract Entity getVehicle();

    @Shadow public abstract void unRide();

    protected EntityMixin(Class<Entity> baseClass) { super(baseClass); }

    @Unique
    @Nullable ItemStack actuallysize$itemCounterpart;
    @Unique
    @Nullable ItemEntityDualityHolder actuallysize$dualityHolderCounterpart;

    @Unique
    @Nullable Double actuallysize$preNormalizedScale = null;

    @WrapOperation(method = "handleNetherPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isPassenger()Z"))
    public boolean onRidePreventPortal(Entity instance, Operation<Boolean> original) {

        // When held, we cannot nether-portal
        if (((EntityDualityCounterpart) instance).actuallysize$isHeld()) { return true; }

        // Otherwise, ASI has no business with this operation
        return original.call(instance);
    }

    @Inject(method = "updateSwimming", at = @At("HEAD"), cancellable = true)
    public void onUpdateSwimming(CallbackInfo ci) {
        //actuallysize.Log("ASI &3 ORE &7 Base Tick for " + getScoreboardName() + ", Active? " + actuallysize$isActive() + ", Held? " + actuallysize$isHeld());

        // When held, dangling means swimming
        EntityDualityCounterpart dualityEntity = this;
        ASIPSHoldPoint hold = dualityEntity.actuallysize$getHoldPoint();
        ItemEntityDualityHolder holder = dualityEntity.actuallysize$getItemEntityHolder();
        if (hold != null && holder != null) {

            // Swimming state is controlled by slot
            boolean shouldSwim = hold.isDangling(holder, dualityEntity);
            if (isSwimming() != shouldSwim) {
                setSwimming(hold.isDangling(holder, dualityEntity)); }

            // Swim state controlled by hold
            ci.cancel();
        }
    }

    @Inject(method = "restoreFrom", at = @At("RETURN"))
    public void onRestoreEntity(Entity pEntity, CallbackInfo ci) {

        // Only server handles held entity flux
        Level world = level();
        if (!(world instanceof ServerLevel)) { return; }
        /*HDA*/ActuallySizeInteractions.Log("ASI &6 EMX-HDA &7 (" + getClass().getSimpleName() + ") &5 Restoring in server after dimension change");

        // Players get force-synced, they will request active dualities themselves
        Entity restoredEntity = (Entity) (Object) this;
        if (restoredEntity instanceof ServerPlayer) {

            // Transfer server-side hold point configuration
            HoldPointConfigurable asConfigurableOld = (HoldPointConfigurable) pEntity;
            HoldPointConfigurable asConfigurableNew = (HoldPointConfigurable) restoredEntity;
            asConfigurableNew.actuallysize$setLocalHoldPoints(asConfigurableOld.actuallysize$getLocalHoldPoints());
            /*HDA*/ActuallySizeInteractions.Log("ASI &6 EMX-HDA &7 (" + getClass().getSimpleName() + ") Copied over hold points &e x" + asConfigurableNew.actuallysize$getLocalHoldPoints().getRegisteredPoints().size());

            // Sync hold point configurations to client
            ASIPSHoldingSyncAction syncing = new ASIPSHoldingSyncAction((ServerPlayer) restoredEntity);
            syncing.withConfigurables();
            syncing.withBroadcast(asConfigurableOld.actuallysize$getLocalHoldPoints().getRegisteredPoints());
            syncing.resolve();
        }

        // Copy over held entities
        ItemEntityDualityHolder oldAsHolder = (ItemEntityDualityHolder) pEntity;
        for (Map.Entry<ASIPSHoldPoint, EntityDualityCounterpart> held : oldAsHolder.actuallysize$getHeldEntityDualities().entrySet()) {

            // What location of my inventory is the item?
            ItemStackLocation oldLocation = held.getValue().actuallysize$getItemStackLocation();
            Entity heldAsEntity = (Entity) held.getValue();
            if (oldLocation == null) { continue; }
            /*HDA*/ActuallySizeInteractions.Log("ASI &6 EMX-HDA &7 (" + getClass().getSimpleName() + ") Transmigrating held entity &e " + oldLocation.getStatement());

            // Rebuild the item stack location
            ItemExplorerElaborator elaborator = oldLocation.getStatement().prepareElaborator(this);
            ItemStackExplorer explorer = oldLocation.getStatement().prepareExplorer();
            ItemStackLocation newLocation = explorer.realize(elaborator);

            // Change held entity dimension
            Entity reprepared = heldAsEntity.changeDimension((ServerLevel) world);
            held.getValue().actuallysize$deactivateDuality();

            // Activate in new dimension
            ASIPSDualityActivationAction transfer = new ASIPSDualityActivationAction(newLocation, newLocation.getItemStack(), reprepared);
            transfer.tryResolve();
        }
    }

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

        // Server-sided ticks only
        if (level.isClientSide) { return; }

        // Tick size of entity vs ridden
        if (SchedulingManager.getServerTicks() % 10 == 0) {

            // Too big for my vehicle? Dismount
            if (isPassenger()) {
                if (ASIUtilities.meetsScaleRequirement((Entity) (Object) this, getVehicle(), ActuallyServerConfig.scaleLimitRider)) { unRide(); } }

            // Same thing for being held, too big? Escape
            EntityDualityCounterpart dualityEntity = this;
            ASIPSHoldPoint hold = dualityEntity.actuallysize$getHoldPoint();
            ItemEntityDualityHolder holder = dualityEntity.actuallysize$getItemEntityHolder();
            if (hold != null && holder != null && !hold.canSustainHold(holder, dualityEntity)) { dualityEntity.actuallysize$escapeDuality(); }
        }

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

        //todo HELD-RIDE Maybe someday allow riding of held entities

        // Riding held entities is unsupported
        EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) pVehicle;
        if (dualityEntity.actuallysize$isHeld()) { return false; }

        // If it exceeds the riding scale limit... then no lol
        if (ASIUtilities.meetsScaleRequirement((Entity) (Object) this, pVehicle, ActuallyServerConfig.scaleLimitRider)) { return false; }

        // Not held not our business
        return original.call(pVehicle);
    }
    @WrapMethod(method = "isPushable")
    public boolean whenPushed(Operation<Boolean> original) {

        // While held, you cannot be pushed
        if (actuallysize$isHeld()) { return false; }

        // Not held not our business
        return original.call();
    }

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At(value = "HEAD"), cancellable = true)
    public void onStartRiding(Entity pVehicle, boolean pForce, CallbackInfoReturnable<Boolean> cir) {

        // Riding held entities is unsupported
        EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) pVehicle;
        if (dualityEntity.actuallysize$isHeld()) { cir.setReturnValue(false); cir.cancel(); return; }

        //todo HELD-RIDE Maybe someday allow riding of held entities

        // Ask the hold point if this slot can be escaped this way
        ASIPSHoldPoint hold = actuallysize$getHoldPoint();
        ItemEntityDualityHolder holder = actuallysize$getItemEntityHolder();
        if (hold == null || holder == null || hold.canBeEscapedByRiding(holder, this)) { return; }

        // We cannot escape while riding
        cir.setReturnValue(false);
        cir.cancel();
    }

    @WrapOperation(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isPassenger()Z"))
    public boolean onMovementEmissionsIsPassenger(Entity instance, Operation<Boolean> original) {

        // If held, then we basically are a passenger
        if (actuallysize$isHeld()) { return true; }

        // Otherwise just call the original
        return original.call(instance);
    }

    @WrapMethod(method = "isIgnoringBlockTriggers")
    public boolean onBlockTriggers(Operation<Boolean> original) {

        // When held, block triggers are ignored
        if (actuallysize$isHeld()) { return true; }

        // Otherwise, ASI has no business with this operation
        return original.call();
    }

    @WrapMethod(method = "isInWall")
    public boolean onSuffocate(Operation<Boolean> original) {

        // When held, we are never in walls
        if (actuallysize$isHeld()) { return false; }

        // Otherwise, ASI has no business with this operation
        return original.call();
    }
}
