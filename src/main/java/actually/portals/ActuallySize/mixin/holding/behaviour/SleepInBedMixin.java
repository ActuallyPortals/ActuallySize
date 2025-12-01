package actually.portals.ActuallySize.mixin.holding.behaviour;

import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.SleepInBed;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(SleepInBed.class)
public abstract class SleepInBedMixin extends Behavior<LivingEntity> {
    public SleepInBedMixin(Map<MemoryModuleType<?>, MemoryStatus> pEntryCondition) { super(pEntryCondition); }

    @Inject(method = "checkExtraStartConditions", at = @At(value = "HEAD"), cancellable = true)
    public void onCheckConditions(ServerLevel pLevel, LivingEntity pOwner, CallbackInfoReturnable<Boolean> cir) {

        // If the entity is held, cancel
        EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) pOwner;
        if (dualityEntity.actuallysize$isHeld()) { cir.setReturnValue(false); cir.cancel(); }
    }

    @WrapOperation(method = "start", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;startSleeping(Lnet/minecraft/core/BlockPos;)V"))
    public void onStartSleeping(LivingEntity instance, BlockPos pPos, Operation<Void> original) {

        // Cant sleep when held
        EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) instance;
        if (dualityEntity.actuallysize$isHeld()) { return; }

        // Proceed as normal
        original.call(instance, pPos);
    }
}
