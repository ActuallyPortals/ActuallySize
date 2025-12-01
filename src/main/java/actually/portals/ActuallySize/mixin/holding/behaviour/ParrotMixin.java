package actually.portals.ActuallySize.mixin.holding.behaviour;

import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Parrot.class)
public abstract class ParrotMixin extends ShoulderRidingEntity implements VariantHolder<Parrot.Variant>, FlyingAnimal {

   protected ParrotMixin(EntityType<? extends ShoulderRidingEntity> pEntityType, Level pLevel) { super(pEntityType, pLevel); }

    @WrapOperation(method = "calculateFlapping", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Parrot;isPassenger()Z"))
    public boolean onRidingPreventFlapping(Parrot instance, Operation<Boolean> original) {

        // When held, we cannot flap when securely held... but we do flap when dangling
        EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) this;
        ASIPSHoldPoint hold = dualityEntity.actuallysize$getHoldPoint();
        ItemEntityDualityHolder holder = dualityEntity.actuallysize$getItemEntityHolder();
        if (hold != null && holder != null) { return !hold.isDangling(holder, dualityEntity); }

        // Otherwise, ASI has no business with this operation
        return original.call(instance);
    }
}
