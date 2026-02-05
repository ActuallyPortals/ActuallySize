package actually.portals.ActuallySize.mixin.third.create;

import actually.portals.ActuallySize.compatibilities.create.BeegCreateKinetics;
import actually.portals.ActuallySize.compatibilities.create.BeegCreateMechanics;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.crank.HandCrankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HandCrankBlockEntity.class, remap = false)
public class HandCrankBlockEntityMixin extends GeneratingKineticBlockEntity {

    public HandCrankBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/crank/HandCrankBlockEntity;updateGeneratedRotation()V"))
    public void OnStopCranking(CallbackInfo ci) {
        BeegCreateKinetics asKinetics = (BeegCreateKinetics) this;
        asKinetics.actuallysize$setKineticBeeg(null);
    }

    @WrapMethod(method = "getGeneratedSpeed")
    private float OnRecalculateSpeed(Operation<Float> original) {
        float originalSpeed = original.call();

        // The speed may be increased by size influence
        BeegCreateKinetics asKinetic = (BeegCreateKinetics) this;
        BeegCreateMechanics asMechanics = asKinetic.actuallysize$getKineticBeeg();
        if (asMechanics != null) { originalSpeed = asMechanics.adjustSpeed(originalSpeed); }

        return originalSpeed;
    }
}
