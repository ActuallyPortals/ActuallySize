package actually.portals.ActuallySize.mixin.world.fear;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.world.ASIWorldSystemManager;
import actually.portals.ActuallySize.world.aigoals.FearBeegsGoal;
import actually.portals.ActuallySize.world.mixininterfaces.Jumpstartable;
import actually.portals.ActuallySize.world.mixininterfaces.Rerollable;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MeleeAttackGoal.class)
public abstract class MeleeAttackGoalMixin extends Goal {

    @Shadow @Final protected PathfinderMob mob;
    @Shadow private long lastCanUseCheck;

    @Unique @Nullable PanicGoal actuallysize$asPanicGoal;
    @Unique @Nullable FearBeegsGoal actuallysize$asFearGoal;
    @Unique @Nullable Goal actuallysize$currentFear;

    @Unique @NotNull
    public PanicGoal actuallysize$getAsPanicGoal() {

        // Create if it doesn't exist
        if (actuallysize$asPanicGoal == null) {
            actuallysize$asPanicGoal = new PanicGoal(mob, 1.5); }

        // Done
        return actuallysize$asPanicGoal;
    }

    @Unique @NotNull
    public FearBeegsGoal actuallysize$getAsFearGoal() {

        // Create if it doesn't exist
        if (actuallysize$asFearGoal == null) {
            actuallysize$asFearGoal = new FearBeegsGoal(mob, 1.1, 20); }

        // Done
        return actuallysize$asFearGoal;
    }

    @Unique @Nullable
    public Goal actuallysize$toFearGoal(@Nullable LivingEntity target) {
        if (target == null) { return null; }

        // Find out how much bigger they are
        double relative = ASIUtilities.getRelativeScale(mob, target);
        if (ASIWorldSystemManager.CanPanic(target, mob, relative)) { return actuallysize$getAsPanicGoal(); }
        if (ASIWorldSystemManager.CanFear(target, mob, relative)) { return actuallysize$getAsFearGoal(); }

        // Nothing
        return null;
    }

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    public void onCanUse(CallbackInfoReturnable<Boolean> cir) {

        // Being held means not attacking
        if (((EntityDualityCounterpart) mob).actuallysize$isHeld()) { cir.cancel(); cir.setReturnValue(false); return; }

        // Only checks when this timer is completed
        long i = this.mob.level().getGameTime();
        if (i - this.lastCanUseCheck < 20L) { return; }

        // Check the target for beeg-ness
        LivingEntity livingentity = this.mob.getTarget();
        actuallysize$currentFear = actuallysize$toFearGoal(livingentity);
        if (actuallysize$currentFear != null) {
            cir.cancel();
            cir.setReturnValue(true);   // FEAR ALWAYS SETS IN
            //cir.setReturnValue(actuallysize$currentFear.canUse());

            // Start this up
            if (actuallysize$currentFear instanceof Jumpstartable) {
                ((Jumpstartable) actuallysize$currentFear).actuallysize$jumpstart();
            }
        }
    }

    @Inject(method = "canContinueToUse", at = @At("HEAD"), cancellable = true)
    public void onCanContinueToUse(CallbackInfoReturnable<Boolean> cir) {

        // Being held means not attacking
        if (((EntityDualityCounterpart) mob).actuallysize$isHeld()) { cir.cancel(); cir.setReturnValue(false); return; }

        // Check the target for beeg-ness
        LivingEntity livingentity = this.mob.getTarget();
        actuallysize$currentFear = actuallysize$toFearGoal(livingentity);
        if (actuallysize$currentFear != null) {
            cir.cancel();
            cir.setReturnValue(true);   // FEAR ALWAYS SETS IN
            //cir.setReturnValue(actuallysize$currentFear.canContinueToUse());

            // Reroll
            if (actuallysize$currentFear instanceof Rerollable) {

                // When finalized
                boolean nativeCanContinue = actuallysize$currentFear.canContinueToUse();
                if (!nativeCanContinue) {
                    ((Rerollable) actuallysize$currentFear).actuallysize$reroll();

                // Reroll when needed
                } else {

                    long i = this.mob.level().getGameTime();
                    if (i - this.lastCanUseCheck > 10L) {
                        this.lastCanUseCheck = i;
                        if (OotilityNumbers.rollSuccess(0.25)) { ((Rerollable) actuallysize$currentFear).actuallysize$reroll(); }
                    }
                }
            }
        }
    }

    @Inject(method = "start", at = @At("HEAD"), cancellable = true)
    public void onStart(CallbackInfo ci) {

        // Check the target for beeg-ness
        LivingEntity livingentity = this.mob.getTarget();
        actuallysize$currentFear = actuallysize$toFearGoal(livingentity);
        if (actuallysize$currentFear != null) { ci.cancel(); actuallysize$currentFear.start(); }
    }

    @Inject(method = "stop", at = @At("HEAD"))
    public void onStop(CallbackInfo ci) {

        // Stop all goals
        actuallysize$getAsPanicGoal().stop();
        actuallysize$getAsFearGoal().stop();
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void onTick(CallbackInfo ci) {

        // Check the target for beeg-ness
        if (actuallysize$currentFear != null) { ci.cancel(); actuallysize$currentFear.tick(); }
    }
}
