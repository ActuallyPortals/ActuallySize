package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallyServerConfig;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityEscapeAction;
import actually.portals.ActuallySize.pickup.actions.ASIPSPickupAction;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

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

    @Nullable
    @Unique
    Entity actuallysize$lastAttackKb;

    @WrapMethod(method = "getMaxFallDistance")
    public int onFallDistancePathfinding(Operation<Integer> original) {

        int ori = original.call();


        double size = ASIUtilities.getEntityScale(this);
        if (size != 1) {

            /*
             *  Pehkui decreases fall distance by your scale,
             *  which means that falling 8 blocks when you are
             *  8x bigger results in effectively falling 1 block
             *
             *  Conversely, if you are 8x smaller, falling 1 block
             *  is the same as falling 8 blocks according to it
             *
             *  Thus, the same calculation is applied to the pathfinder
             */
            ori = OotilityNumbers.round(((double) ori) * size);
        }

        return ori;
    }

    @WrapMethod(method = "doHurtTarget")
    public boolean onAttack(Entity pTarget, Operation<Boolean> original) {

        // Remember the last attacked entity momentarily
        actuallysize$lastAttackKb = pTarget;
        boolean ret = original.call(pTarget);
        actuallysize$lastAttackKb = null;
        return ret;
    }

    @WrapOperation(method = "doHurtTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"))
    public void onAttackKnockback(LivingEntity instance, double pStrength, double dx, double dy, Operation<Void> original) {
        //ATT//ActuallySizeInteractions.Log("ASI &9 MBX-ATT &7 Knockback cause " + this.getScoreboardName() + " initial &e @" + pStrength);

        // Adjust kb strength based on scale
        double cookedStrength = pStrength;
        if (actuallysize$lastAttackKb != null && ActuallyServerConfig.strongBeegs) {
            double myScale = ASIUtilities.getEntityScale(this);
            double rsc = ASIUtilities.getEntityScale(actuallysize$lastAttackKb) / myScale; // Scale Relative to Victim

            // When beeg, the knockback method naturally decreases kb, we must undo this factor to take over it
            double myScaleAmp = 1;
            if (ActuallyServerConfig.tankyBeegs) {
                if (myScale > 1) { myScaleAmp = ASIUtilities.beegBalanceResist(myScale, 2, 0); }
            }

            /*
             * More KB when smol, less KB when beeg.
             *
             * Affects scale and not effective size because when designing large entities you
             * already account for their size in their ability to deal knockback, such that
             * using effective size is overkill.
             */
            double combatScaleAmp = ASIUtilities.beegBalanceResist(rsc * rsc, 2, 0);;

            // Amplify by combat but also undo the kb operation beeg tankiness
            cookedStrength = cookedStrength * combatScaleAmp / myScaleAmp;
            //ATT//ActuallySizeInteractions.Log("ASI &9 MBX-ATT &7 Pre-adjusted {x" + rsc + "} to &6 @" + cookedStrength + " &r &e x" + combatScaleAmp + " /" + myScaleAmp);
        }

        // Continue
        original.call(instance, cookedStrength, dx, dy);
    }
}
