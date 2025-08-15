package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GroundPathNavigation.class)
public abstract class GroundPathNavigationMixin extends PathNavigation {

    public GroundPathNavigationMixin(Mob pMob, Level pLevel) { super(pMob, pLevel); }

    @WrapMethod(method = "canUpdatePath")
    public boolean onTryPathing(Operation<Boolean> original) {

        // If the mob is held, it cannot path
        EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) this.mob;
        if (dualityEntity.actuallysize$isHeld()) { return false; }

        // Not held not our business
        return original.call();
    }
}
