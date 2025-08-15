package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.pickup.actions.ASIPSDualityEscapeAction;
import actually.portals.ActuallySize.pickup.actions.ASIPSPickupAction;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity implements Targeting {

    protected MobMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) { super(pEntityType, pLevel); }

    @WrapMethod(method = "convertTo")
    public <T extends Mob> T onConvertDiscarded(EntityType<T> pEntityType, boolean pTransferInventory, Operation<T> original) {
        T ret = original.call(pEntityType, pTransferInventory);
        if (ret == null) { return null; }

        // When held, we must escape first
        EntityDualityCounterpart dualityEntityOld = (EntityDualityCounterpart) this;
        if (dualityEntityOld.actuallysize$isActive()) {

            // Escape old
            ItemStackLocation<? extends Entity> stackLocationOld = dualityEntityOld.actuallysize$getItemStackLocation();
            ASIPSDualityEscapeAction discardAction = new ASIPSDualityEscapeAction(dualityEntityOld);
            discardAction.tryResolve();

            // Activate New
            if (stackLocationOld != null) {
                ASIPSPickupAction convertAction = new ASIPSPickupAction(stackLocationOld, ret);
                convertAction.tryResolve(); }
        }

        // Otherwise, ASI has no business with this operation
        return ret;
    }

    @WrapMethod(method = "requiresCustomPersistence")
    public boolean onRidePreventDespawn(Operation<Boolean> original) {

        // When held, we may not despawn
        if (((EntityDualityCounterpart) this).actuallysize$isHeld()) { return true; }

        // Otherwise, ASI has no business with this operation
        return original.call();
    }
}
