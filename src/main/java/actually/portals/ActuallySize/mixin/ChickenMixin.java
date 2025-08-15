package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Chicken.class)
public abstract class ChickenMixin extends Animal {

    protected ChickenMixin(EntityType<? extends Animal> pEntityType, Level pLevel) { super(pEntityType, pLevel); }

    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Chicken;onGround()Z"))
    public boolean onRidingPreventFlapping(Chicken instance, Operation<Boolean> original) {

        // When held, if securely, it counts as being on ground
        EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) this;
        ASIPSHoldPoint hold = dualityEntity.actuallysize$getHoldPoint();
        ItemEntityDualityHolder holder = dualityEntity.actuallysize$getItemEntityHolder();
        if (hold != null && holder != null) { return !hold.isDangling(holder, dualityEntity); }

        // Otherwise, ASI has no business with this operation
        return original.call(instance);
    }
}
