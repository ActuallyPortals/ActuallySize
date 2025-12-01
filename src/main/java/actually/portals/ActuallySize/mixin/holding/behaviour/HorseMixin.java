package actually.portals.ActuallySize.mixin.holding.behaviour;

import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractHorse.class)
public abstract class HorseMixin extends Animal implements ContainerListener, HasCustomInventoryScreen, OwnableEntity, PlayerRideableJumping, Saddleable {

    protected HorseMixin(EntityType<? extends AbstractHorse> pEntityType, Level pLevel) { super(pEntityType, pLevel); }

    @WrapMethod(method = "canParent")
    public boolean onTryParent(Operation<Boolean> original) {

        // If the sniffer is held, it cannot mate
        EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) this;
        if (dualityEntity.actuallysize$isHeld()) { return false; }

        // Not held not our business
        return original.call();
    }
}
