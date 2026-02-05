package actually.portals.ActuallySize.mixin.third.create;

import actually.portals.ActuallySize.compatibilities.create.BeegCreateKinetics;
import actually.portals.ActuallySize.compatibilities.create.BeegCreateMechanics;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = KineticBlockEntity.class, remap = false)
public abstract class KineticBlockEntityMixin implements BeegCreateKinetics {

    @Nullable @Unique BeegCreateMechanics actuallysize$kineticBeeg;

    @Override
    public void actuallysize$setKineticBeeg(@Nullable BeegCreateMechanics beeg) { actuallysize$kineticBeeg = beeg; }

    @Override
    public @Nullable BeegCreateMechanics actuallysize$getKineticBeeg() { return actuallysize$kineticBeeg; }

    @WrapOperation(method = "calculateAddedStressCapacity", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/api/stress/BlockStressValues;getCapacity(Lnet/minecraft/world/level/block/Block;)D"))
    private double OnRecalculatePower(Block block, Operation<Double> original) {
        Double originalPower = original.call(block);

        // The power may be increased by size influence
        BeegCreateKinetics asKinetic = this;
        BeegCreateMechanics asMechanics = asKinetic.actuallysize$getKineticBeeg();
        if (asMechanics != null) { originalPower = asMechanics.adjustStressCapacity(originalPower); }

        return originalPower;
    }

    @WrapMethod(method = "getGeneratedSpeed")
    private float OnRecalculateSpeed(Operation<Float> original) {
        float originalSpeed = original.call();

        // The speed may be increased by size influence
        BeegCreateKinetics asKinetic = this;
        BeegCreateMechanics asMechanics = asKinetic.actuallysize$getKineticBeeg();
        if (asMechanics != null) { originalSpeed = asMechanics.adjustSpeed(originalSpeed); }

        return originalSpeed;
    }
}
