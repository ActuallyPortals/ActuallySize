package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Sniffer.class)
public abstract class SnifferMixin extends Animal {

    protected SnifferMixin(EntityType<? extends Animal> pEntityType, Level pLevel) { super(pEntityType, pLevel); }

    @WrapMethod(method = "canSniff")
    public boolean onTrySniff(Operation<Boolean> original) {

        // If the sniffer is held, it cannot sniff :tears:
        EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) this;
        if (dualityEntity.actuallysize$isHeld()) { return false; }

        // Not held not our business
        return original.call();
    }

    @WrapMethod(method = "canDig()Z")
    public boolean onTryDig(Operation<Boolean> original) {

        // If the sniffer is held, it cannot dig :tears:
        EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) this;
        if (dualityEntity.actuallysize$isHeld()) { return false; }

        // Not held not our business
        return original.call();
    }
}
