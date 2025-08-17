package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallyServerConfig;
import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSHoldPointRegistry;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSRegisterableHoldPoint;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.GraceImpulsable;
import actually.portals.ActuallySize.pickup.mixininterfaces.HoldPointConfigurable;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Either;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemExplorerStatement;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements HoldPointConfigurable, GraceImpulsable {

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

            // Compare the new point to the old point
            for (Map.Entry<ItemExplorerStatement<?,?>, ASIPSRegisterableHoldPoint> newPoint : reg.getRegisteredPoints().entrySet()) {
                ASIPSRegisterableHoldPoint oldPoint = old.getHoldPoint(newPoint.getKey());

                // If it existed and was different
                if (oldPoint != null && !oldPoint.equals(newPoint)) {

                    //todo Flux from old hold point to new hold point
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
}
