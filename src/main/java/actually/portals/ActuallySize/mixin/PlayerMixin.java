package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSHoldPointRegistry;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSRegisterableHoldPoint;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.HoldPointConfigurable;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Either;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemExplorerStatement;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements HoldPointConfigurable {

    protected PlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) { super(pEntityType, pLevel); }

    @Unique @NotNull ASIPSHoldPointRegistry actuallysize$localRegistry = new ASIPSHoldPointRegistry();

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

    @Inject(method = "startSleepInBed", at = @At(value = "HEAD"), cancellable = true)
    public void onStartRiding(BlockPos pAt, CallbackInfoReturnable<Either<Player.BedSleepingProblem, Unit>> cir) {

        // If held, you cannot sleep
        EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) this;
        if (dualityEntity.actuallysize$isHeld()) {
            cir.setReturnValue(Either.left(Player.BedSleepingProblem.OTHER_PROBLEM));
            cir.cancel();
        }
    }

    @WrapOperation(method = "tryToStartFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;canElytraFly(Lnet/minecraft/world/entity/LivingEntity;)Z"))
    public boolean onTryFallFly(ItemStack instance, LivingEntity livingEntity, Operation<Boolean> original) {
        boolean success = original.call(instance, livingEntity);

        // Okay, so we are about to glide. Are we held?
        EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) this;
        ASIPSHoldPoint hold = dualityEntity.actuallysize$getHoldPoint();
        ItemEntityDualityHolder holder = dualityEntity.actuallysize$getItemEntityHolder();
        if (hold != null && holder != null && success) {

            // Can we glide off?
            if (hold.canBeGlidedOff(holder, dualityEntity)) {

                // Escape
                dualityEntity.actuallysize$escapeDuality();

            // Cannot be glided off, cancel glide
            } else { return false; }
        }

        // Not held? ASI has no business with this operation
        return success;
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isPassenger()Z"))
    public boolean onRidingPreventCrit(Player instance, Operation<Boolean> original) {

        // When held, we cannot crit
        if (((EntityDualityCounterpart) instance).actuallysize$isHeld()) { return true; }

        // Otherwise, ASI has no business with this operation
        return original.call(instance);
    }
}
