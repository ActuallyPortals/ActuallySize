package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Animal.class)
public abstract class AnimalMixin extends AgeableMob {

    protected AnimalMixin(EntityType<? extends AgeableMob> pEntityType, Level pLevel) { super(pEntityType, pLevel); }

    @WrapMethod(method = "canMate")
    public boolean onTryMate(Animal pOtherAnimal, Operation<Boolean> original) {

        // If the animal is held, it cannot mate
        EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) this;
        if (dualityEntity.actuallysize$isHeld()) { return false; }

        // Not held not our business
        return original.call(pOtherAnimal);
    }
}
