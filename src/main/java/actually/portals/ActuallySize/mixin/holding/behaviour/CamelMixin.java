package actually.portals.ActuallySize.mixin.holding.behaviour;

import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.RiderShieldingMount;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Camel.class)
public abstract class CamelMixin extends AbstractHorse implements PlayerRideableJumping, RiderShieldingMount, Saddleable {

    protected CamelMixin(EntityType<? extends AbstractHorse> pEntityType, Level pLevel) { super(pEntityType, pLevel); }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/camel/Camel;isPassenger()Z"))
    public boolean onRidingPreventDash(Camel instance, Operation<Boolean> original) {

        // When held, we cannot dash
        if (((EntityDualityCounterpart) instance).actuallysize$isHeld()) { return true; }

        // Otherwise, ASI has no business with this operation
        return original.call(instance);
    }
}
