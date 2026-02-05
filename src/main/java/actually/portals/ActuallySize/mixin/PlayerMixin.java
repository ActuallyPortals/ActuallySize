package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallyServerConfig;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityActivationAction;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityEscapeAction;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityFluxAction;
import actually.portals.ActuallySize.pickup.consumption.ASIPSCInstantKill;
import actually.portals.ActuallySize.pickup.consumption.ASIPSCKiss;
import actually.portals.ActuallySize.pickup.consumption.ASIPSPlayerConsumption;
import actually.portals.ActuallySize.pickup.events.ASIPSConsumeMobEvent;
import actually.portals.ActuallySize.pickup.events.ASIPSConsumeTinyEvent;
import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSHoldPointRegistry;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSRegisterableHoldPoint;
import actually.portals.ActuallySize.pickup.holding.pose.ASIPSTinyPosedHold;
import actually.portals.ActuallySize.pickup.holding.pose.smol.ASIPSTinyPoseProfile;
import actually.portals.ActuallySize.pickup.item.ASIPSHeldEntityItem;
import actually.portals.ActuallySize.pickup.mixininterfaces.*;
import actually.portals.ActuallySize.world.mixininterfaces.BeegBreaker;
import actually.portals.ActuallySize.world.mixininterfaces.PreferentialOptionable;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Either;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemExplorerStatement;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
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
import java.util.Map;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements HoldPointConfigurable, GraceImpulsable, PreferentialOptionable, BeegBreaker {

    @Shadow public abstract FoodData getFoodData();

    @Shadow @Nullable private Pose forcedPose;

    protected PlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) { super(pEntityType, pLevel); }

    @Unique @NotNull ASIPSHoldPointRegistry actuallysize$localRegistry = new ASIPSHoldPointRegistry();

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        if (actuallysize$graceImpulseTicks > 0) { actuallysize$graceImpulseTicks--; }
    }

    @Override
    public @NotNull ASIPSHoldPointRegistry actuallysize$getLocalHoldPoints() {
        return actuallysize$localRegistry;
    }

    @Override
    public void actuallysize$setLocalHoldPoints(@NotNull ASIPSHoldPointRegistry reg) {

        // Update registry while keeping a reference to the old
        ASIPSHoldPointRegistry old = actuallysize$localRegistry;
        actuallysize$localRegistry = reg;

        // When these change in the server, they must be synced to clients and flux
        if (!level().isClientSide) {
            ItemEntityDualityHolder asHolder = (ItemEntityDualityHolder) this;

            // Compare the new point to the old point
            for (Map.Entry<ItemExplorerStatement<?,?>, ASIPSRegisterableHoldPoint> newPoint : reg.getRegisteredPoints().entrySet()) {
                ASIPSRegisterableHoldPoint oldPoint = old.getHoldPoint(newPoint.getKey());

                // If it existed and was different
                if (oldPoint != null && !oldPoint.equals(newPoint)) {
                    EntityDualityCounterpart entityCounterpart = asHolder.actuallysize$getHeldItemEntityDuality(oldPoint);

                    // If an entity was previously held in this hold point
                    if (entityCounterpart != null) {
                        ItemStack itemCounterpart = entityCounterpart.actuallysize$getItemCounterpart();
                        ItemStackLocation<? extends Entity> stackLocation = entityCounterpart.actuallysize$getItemStackLocation();
                        ASIPSDualityEscapeAction escape = new ASIPSDualityEscapeAction(entityCounterpart);

                        // Escape in failure
                        if (itemCounterpart == null || stackLocation == null) {
                            escape.tryResolve();
                        } else {

                            // Flux ready
                            ASIPSDualityFluxAction flux = new ASIPSDualityFluxAction(
                                    new ASIPSDualityEscapeAction(entityCounterpart),
                                    new ASIPSDualityActivationAction(stackLocation, itemCounterpart, (Entity) entityCounterpart)
                            );

                            // Escape if failure
                            if (!flux.tryResolve()) { escape.tryResolve(); }
                        }
                    }
                }
            }
        }
    }

    @Unique int actuallysize$graceImpulseTicks = 0;

    @Override
    public boolean actuallysize$isInGraceImpulse() {
        return actuallysize$graceImpulseTicks > 0;
    }

    @Override
    public void actuallysize$addGraceImpulse(int ticks) {
        actuallysize$graceImpulseTicks += ticks;
    }

    @Inject(method = "startSleepInBed", at = @At(value = "HEAD"), cancellable = true)
    public void onStartRiding(BlockPos pAt, CallbackInfoReturnable<Either<Player.BedSleepingProblem, Unit>> cir) {

        // If held, you cannot sleep
        EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) this;
        if (dualityEntity.actuallysize$isHeld()) {
            cir.setReturnValue(Either.left(Player.BedSleepingProblem.OTHER_PROBLEM));
            cir.cancel();
        }
    }

    @Inject(method = "tryToStartFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;startFallFlying()V"), cancellable = true)
    public void onTryFallFly(CallbackInfoReturnable<Boolean> cir) {

        // Okay, so we are about to glide. Are we held?
        EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) this;
        ASIPSHoldPoint hold = dualityEntity.actuallysize$getHoldPoint();
        ItemEntityDualityHolder holder = dualityEntity.actuallysize$getItemEntityHolder();
        if (hold != null && holder != null) {

            // Can we glide off?
            if (hold.canBeGlidedOff(holder, dualityEntity)) {

                // Escape
                dualityEntity.actuallysize$escapeDuality();

            // Cannot be glided off, cancel glide
            } else {
                cir.cancel();
                cir.setReturnValue(false);
            }
        }
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isPassenger()Z"))
    public boolean onRidingPreventCrit(Player instance, Operation<Boolean> original) {

        // When held, we cannot crit
        if (((EntityDualityCounterpart) instance).actuallysize$isHeld()) { return true; }

        // Otherwise, ASI has no business with this operation
        return original.call(instance);
    }

    @Nullable @Unique Entity actuallysize$lastAttackKb;

    @WrapMethod(method = "attack")
    public void onAttack(Entity pTarget, Operation<Void> original) {

        // Remember the last attacked entity momentarily
        actuallysize$lastAttackKb = pTarget;
        original.call(pTarget);
        actuallysize$lastAttackKb = null;
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"))
    public void onAttackKnockback(LivingEntity instance, double pStrength, double dx, double dy, Operation<Void> original) {
        //ATT//ActuallySizeInteractions.Log("ASI &9 PLX-ATT &7 Knockback cause " + this.getScoreboardName() + " initial &e @" + pStrength);

        // Adjust kb strength based on scale
        double cookedStrength = pStrength;
        if (actuallysize$lastAttackKb != null && ActuallyServerConfig.strongBeegs) {
            double myScale = ASIUtilities.getEntityScale(this);
            double rsc = ASIUtilities.getEntityScale(actuallysize$lastAttackKb) / myScale; // Scale Relative to Victim

            // When beeg, the knockback method naturally decreases kb, we must undo this factor to take over it
            double myScaleAmp = 1;
            if (ActuallyServerConfig.tankyBeegs) {
                if (myScale > 1) { myScaleAmp = ASIUtilities.beegBalanceResist(myScale, 2, 0); }
            }

            /*
             * More KB when smol, less KB when beeg.
             *
             * Affects scale and not effective size because when designing large entities you
             * already account for their size in their ability to deal knockback, such that
             * using effective size is overkill.
             */
            double combatScaleAmp = ASIUtilities.beegBalanceResist(rsc * rsc, 2, 0);;

            // Amplify by combat but also undo the kb operation beeg tankiness
            cookedStrength = cookedStrength * combatScaleAmp / myScaleAmp;
            //ATT//ActuallySizeInteractions.Log("ASI &9 PLX-ATT &7 Pre-adjusted {x" + rsc + "} to &6 @" + cookedStrength + " &r &e x" + combatScaleAmp + " /" + myScaleAmp);
        }

        // Continue
        original.call(instance, cookedStrength, dx, dy);
    }

    @WrapMethod(method = "eat")
    public ItemStack onEatCall(Level world, ItemStack itemCounterpart, Operation<ItemStack> original) {
        Player thisEntity = (Player) (Object) this;

        // Preconditions
        if (world == null) {return original.call(world, itemCounterpart);}
        if (itemCounterpart == null) { return original.call(world, itemCounterpart); }
        ((PlayerBound) this.getFoodData()).actuallysize$setBoundPlayer(thisEntity);
        if (!(thisEntity instanceof ServerPlayer)) {

            if (thisEntity.isShiftKeyDown() && itemCounterpart.getItem() instanceof ASIPSHeldEntityItem && ((ASIPSHeldEntityItem) itemCounterpart.getItem()).isPlayer()) { itemCounterpart.setCount(2); }
            return original.call(world, itemCounterpart);
        }
        if (!itemCounterpart.isEdible()) {return original.call(world, itemCounterpart);}
        if (!(itemCounterpart.getItem() instanceof ASIPSHeldEntityItem)) {return original.call(world, itemCounterpart);}

        // Identify
        ASIPSHeldEntityItem asASIItem = (ASIPSHeldEntityItem) itemCounterpart.getItem();
        ServerPlayer beeg = (ServerPlayer) thisEntity;
        Entity entityCounterpart = asASIItem.counterpartOrRebuild(world, itemCounterpart, beeg.getAbilities().instabuild, false);
        EntityDualityCounterpart entityDuality = (EntityDualityCounterpart) entityCounterpart;
        if (entityCounterpart == null) {return original.call(world, itemCounterpart);}
        //FOO//ActuallySizeInteractions.Log("ASI &1 PMX-FOO &r Edacious mob &6 " + entityCounterpart.getScoreboardName() + " " + entityCounterpart.getClass().getSimpleName());

        // Run event
        ASIPSConsumeMobEvent mobEvent = new ASIPSConsumeMobEvent(beeg, entityCounterpart, itemCounterpart);
        boolean cancelled = MinecraftForge.EVENT_BUS.post(mobEvent);
        if (cancelled) {return itemCounterpart;}
        Edacious asASI = ((Edacious) (Object) itemCounterpart);

        // When not consuming a player, not much happens
        if (!(entityCounterpart instanceof ServerPlayer)) {
            //FOO//ActuallySizeInteractions.Log("ASI &1 PMX-FOO &r Edacious &9 NON-PLAYER");

            // If there is no event, or it is not set to consume, ASI is done.
            if (!mobEvent.isConsumeMob()) {return original.call(world, itemCounterpart);}

            // Break the entity-duality link between item and entity
            ASIPSDualityEscapeAction action = new ASIPSDualityEscapeAction(entityDuality);
            action.setAndRemoveItem(false);
            action.tryResolve();
            Edacious eda = null;

            // Tell the entity who is dealing damage to them
            if (entityCounterpart instanceof LivingEntity) {
                LivingEntity eatenLiving = (LivingEntity) entityCounterpart;
                eda = (Edacious) entityCounterpart;
                eatenLiving.setLastHurtByPlayer(beeg);
            }

            // Deal fatal amount of damage and record the food properties of all this mobs' drops
            asASI.actuallysize$setEdaciousProperties(null);
            if (eda != null) {
                eda.actuallysize$setWasConsumed(true);
            }
            entityCounterpart.kill();
            if (eda != null) {
                asASI.actuallysize$setEdaciousProperties(eda.actuallysize$getEdaciousProperties());
                if (!(eda instanceof Slime)) {eda.actuallysize$setWasConsumed(false);}
            }
            asASIItem.resetFoodTick();
            return original.call(world, itemCounterpart);
        }
        //FOO//ActuallySizeInteractions.Log("ASI &1 PMX-FOO &r Edacious &5 PLAYER");

        // Consuming a player, that's special
        ASIPSPlayerConsumption consumer = beeg.isShiftKeyDown() ? new ASIPSCKiss() : new ASIPSCInstantKill();
        ServerPlayer tiny = (ServerPlayer) entityCounterpart;
        ASIPSConsumeTinyEvent playerEvent = new ASIPSConsumeTinyEvent(beeg, tiny, itemCounterpart, consumer);
        boolean canceled = MinecraftForge.EVENT_BUS.post(playerEvent);
        if (canceled) { return itemCounterpart; }

        // Snack on the drops that were consumed
        asASI.actuallysize$setEdaciousProperties(((Edacious) tiny).actuallysize$getEdaciousProperties());
        asASIItem.resetFoodTick();
        ItemStack ret = itemCounterpart;
        if (playerEvent.getConsumer().eats()) { ret = original.call(world, itemCounterpart); }

        // Apply consumer
        playerEvent.getConsumer().snack(beeg, tiny);

        // Done
        return ret;
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    public void onReadSaveData(CompoundTag pCompound, CallbackInfo ci) {
        this.actuallysize$preferredOptionsApplied = pCompound.getDouble("ASILastLoginPrefs");
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    public void onAddSaveData(CompoundTag pCompound, CallbackInfo ci) {
        pCompound.putDouble("ASILastLoginPrefs", actuallysize$preferredOptionsApplied);
    }

    @Unique
    double actuallysize$preferredOptionsApplied;

    @Override
    public boolean actuallysize$isPreferredOptionsApplied(double latest) { return OotilityNumbers.round(actuallysize$preferredOptionsApplied, 4) == OotilityNumbers.round(latest, 4); }

    @Override
    public void actuallysize$setPreferredOptionsApplied(double state) { actuallysize$preferredOptionsApplied = state; }

    @Inject(method = "updatePlayerPose", at = @At("HEAD"), cancellable = true)
    public void onForcedPose(CallbackInfo ci) {

        // Identify self
        Player me = (Player) (Object) this;
        EntityDualityCounterpart entityDuality = (EntityDualityCounterpart) me;

        // ASI Yields priority to other forced poses
        if (forcedPose == null && entityDuality.actuallysize$isHeld()) {

            // Currently held? Query held pose
            ASIPSHoldPoint holdPoint = entityDuality.actuallysize$getHoldPoint();
            if (holdPoint instanceof ASIPSTinyPosedHold) {

                // Override pose with that defined in the pose profile
                ASIPSTinyPoseProfile profile = ((ASIPSTinyPosedHold) holdPoint).getTinyPose(me);
                if (profile != null) { if (profile.applyPose(me)) { ci.cancel(); } }
            }
        }
    }

    @Unique boolean actuallysize$isBeegBreaking;

    @Override
    public boolean actuallysize$isBeegBreaking() { return actuallysize$isBeegBreaking; }

    @Override
    public void actuallysize$setBeegBreaking(boolean is) { actuallysize$isBeegBreaking = is; if (!is) { actuallysize$beegBreakingDrops.clear(); } }

    @Unique
    @NotNull ArrayList<ItemStack> actuallysize$beegBreakingDrops = new ArrayList<>();

    @Override
    public void actuallysize$addBeegBreakingDrop(@NotNull ItemStack drop) { actuallysize$beegBreakingDrops.add(drop); }

    @Override
    public @NotNull ArrayList<ItemStack> actuallysize$getBeegBreakingDrops() { return actuallysize$beegBreakingDrops; }

    @Override
    public void actuallysize$setBeegBreaker(@Nullable ServerPlayer beeg) { }

    @Override
    public @Nullable ServerPlayer actuallysize$getBeegBreaker() { return null; }

    @WrapOperation(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"))
    public void OnTravelFoodExhaust(Player instance, float pExhaustion, Operation<Void> original) {

        // Giants get reduced travel hunger, they are traveling much more easily after all
        double size = ASIUtilities.getEffectiveSize(instance);
        if (size > 1) {
            size = 1D / size;
            pExhaustion *= (float) (size);
            original.call(instance, pExhaustion);
            return; }

        // Otherwise, normal breaking
        original.call(instance, pExhaustion);
    }
}
