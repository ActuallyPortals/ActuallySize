package actually.portals.ActuallySize.mixin.holding.food;

import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import gunging.ootilities.GungingOotilitiesMod.scheduling.SchedulingManager;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SwellGoal.class)
public abstract class SwellGoalMixin extends Goal {

    @Shadow @Final private Creeper creeper;

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    public void onCanUse(CallbackInfoReturnable<Boolean> cir) {

        // When held, fuse is overridden
        if (((EntityDualityCounterpart) creeper).actuallysize$isHeld()) {
            cir.setReturnValue(false);
            cir.cancel();

            // The target is now the holder, must be a player to use
            ItemEntityDualityHolder holder = ((EntityDualityCounterpart) creeper).actuallysize$getItemEntityHolder();
            if (holder instanceof Player) { cir.setReturnValue(true); }
        }
    }

    @Unique long actuallysize$lastSwell;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void onTick(CallbackInfo ci) {

        // When held, fuse is overridden
        if (((EntityDualityCounterpart) creeper).actuallysize$isHeld()) {
            ci.cancel();

            // The target is now the holder, must be a player to use
            ItemEntityDualityHolder dualityHolder = ((EntityDualityCounterpart) creeper).actuallysize$getItemEntityHolder();
            if (dualityHolder instanceof Player) {
                Player holder = (Player) dualityHolder;

                // Should probably confirm the used hand holds the creeper but whatever // holder.getUsedItemHand()
                boolean activelyInUse = holder.getUseItemRemainingTicks() > 0;
                this.creeper.setSwellDir(activelyInUse ? 1 : -1);

                long i = creeper.level().getGameTime();
                if (activelyInUse) {
                    actuallysize$lastSwell = i;
                    this.creeper.setSwellDir(1);
                } else {

                    // Keeps swelling for ten ticks and only relaxes after that
                    if (i > actuallysize$lastSwell + 10) {
                        this.creeper.setSwellDir(-1);

                    // Continue swelling
                    } else {
                        this.creeper.setSwellDir(1);
                    }
                }
            }
        }
    }
}
