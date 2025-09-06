package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallyServerConfig;
import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.netcode.ASINetworkManager;
import actually.portals.ActuallySize.netcode.packets.clientbound.ASINCItemEntityActivationPacket;
import actually.portals.ActuallySize.pickup.mixininterfaces.*;
import actually.portals.ActuallySize.world.ASIWorldSystemManager;
import actually.portals.ActuallySize.world.mixininterfaces.AmountMatters;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import gunging.ootilities.GungingOotilitiesMod.exploring.entities.ISEEquipmentSlotted;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Edacious, Attackable, net.minecraftforge.common.extensions.IForgeLivingEntity {

    @Shadow @Final public WalkAnimationState walkAnimation;

    public LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) { super(pEntityType, pLevel); }

    @WrapMethod(method = "isPickable")
    public boolean onPickEntities(Operation<Boolean> original) {

        // If held, not pushable
        EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) this;
        if (dualityEntity.actuallysize$isHeld()) {return false;}

        // Not held not our business
        return original.call();
    }

    @WrapMethod(method = "isPushable")
    public boolean onPushEntities(Operation<Boolean> original) {

        // If held, not pushable
        EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) this;
        if (dualityEntity.actuallysize$isHeld()) {return false;}

        // Not held not our business
        return original.call();
    }

    @Inject(method = "startSleeping", at = @At("HEAD"))
    public void onSleeping(BlockPos pPos, CallbackInfo ci) {

        //todo Somehow allow you to sleep in certain slots

        // If currently held, force-sleeping allows you to escape
        EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) this;
        if (dualityEntity.actuallysize$isHeld()) { dualityEntity.actuallysize$escapeDuality(); }
    }

    @WrapOperation(method = "updateFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isPassenger()Z"))
    public boolean onRidePreventGlide(LivingEntity instance, Operation<Boolean> original) {

        // When held, we cannot elytra glide
        if (((EntityDualityCounterpart) instance).actuallysize$isHeld()) { return true; }

        // Otherwise, ASI has no business with this operation
        return original.call(instance);
    }

    @Inject(method = "handleEquipmentChanges", at = @At("RETURN"))
    public void onHandleEquipmentChangesReturn(Map<EquipmentSlot, ItemStack> pEquipments, CallbackInfo ci) {

        // Send an update for every change here
        for (Map.Entry<EquipmentSlot, ItemStack> slot : pEquipments.entrySet()) {

            /*
             * If we are sending an update somewhere... it must be accompanied by
             * the Item-Duality activation packet since it will get reset
             */

            ItemStack update = slot.getValue();
            if (update.isEmpty()) { continue; }
            //HDA//ActuallySizeInteractions.Log("ASI &1 EDS &r Updating &f " + update.getDisplayName().getString() + " &7 in &e " + slot.getKey());

            // Only the business of ASI when syncing an Item-Entity duality that is active
            ItemDualityCounterpart dualityItem = (ItemDualityCounterpart) (Object) update;
            if (dualityItem == null) { continue; }
            if (!dualityItem.actuallysize$isDualityActive()) { continue; }

            // I suppose, if it is about to get changed, re-send the activation packet at least
            Entity entityCounterpart = dualityItem.actuallysize$getEntityCounterpart();
            ItemStackLocation<? extends Entity> stackLocation = dualityItem.actuallysize$getItemStackLocation();
            if (stackLocation == null || entityCounterpart == null) { continue; }

            /*
             * Strictly speaking, if we are sending an update here, we really expect
             * the Stack Location to be an Equipment-based Stack Location.
             *
             * If the slot of the entity duality does not match the slot of the equipment...
             * duality activation did not occur correctly and that is very bad... OR this is
             * in the middle of a Duality Flux event that resolves at the start of next tick
             * which is better :based:
             */
            if (!(stackLocation.getStatement() instanceof ISEEquipmentSlotted)) { continue; }
            EquipmentSlot expected = ((ISEEquipmentSlotted) stackLocation.getStatement()).getEquipmentSlot();
            if (!expected.equals(slot.getKey())) { continue; }

            // Send another packet to link entity
            /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "EDS", "&9 HandleEquipmentChanges activation packet for {0} (disabled)", stackLocation);
            //ASINCItemEntityActivationPacket packet = new ASINCItemEntityActivationPacket(stackLocation, entityCounterpart);
            //ASINetworkManager.broadcastEntityUpdate(this, packet);
        }
    }

    @Inject(method = "equipmentHasChanged", at = @At(value = "HEAD"), cancellable = true)
    public void onEquipmentHasChangedCall(ItemStack pOldItem, ItemStack pNewItem, CallbackInfoReturnable<Boolean> cir) {

        // Only the business of ASI when syncing an Item-Entity duality that is active
        ItemDualityCounterpart dualityItem = (ItemDualityCounterpart) (Object) pNewItem;
        if (dualityItem == null) { return; }
        if (!dualityItem.actuallysize$isDualityActive()) { return; }
        Entity entityCounterpart = dualityItem.actuallysize$getEntityCounterpart();
        if (entityCounterpart instanceof Player) { return; }
        Entity enclosedEntity = dualityItem.actuallysize$getEnclosedEntity(entityCounterpart.level());
        if (enclosedEntity == null) { return; }

        /*
         * What is currently in the remote?
         */
        ItemDualityCounterpart remoteItem = (ItemDualityCounterpart) (Object) pOldItem;
        if (remoteItem == null) { return; }

        // Check the remote enclosed entity
        Entity remoteEnclosed = remoteItem.actuallysize$getEnclosedEntity(entityCounterpart.level());
        if (remoteEnclosed == null) { return; }
        UUID remoteUUID = remoteItem.actuallysize$getEnclosedEntityUUID();
        UUID dualityUUID = dualityItem.actuallysize$getEnclosedEntityUUID();
        if (dualityUUID == null || remoteUUID == null) { return; }

        // Identical UUIDs? Then the item has not actually changed
        if (remoteUUID.equals(dualityUUID)) { cir.setReturnValue(false); }
    }

    @Unique
    @Nullable Entity actuallysize$lastHurterKb;

    @WrapMethod(method = "hurt")
    public boolean onHurtCall(DamageSource pDamageSource, float pDamageAmount, Operation<Boolean> original) {

        // Only adjust if it was not adjusted before
        AmountMatters am = (AmountMatters) pDamageSource;
        if (am.actuallysize$getAmount() == null) {

            // Adjust damage amount based on options
            double asi = ASIWorldSystemManager.ASICombatAdjust(pDamageAmount,  (LivingEntity) (Object) this, pDamageSource);

            // Put it in the Damage Source
            ((AmountMatters) pDamageSource).actuallysize$setAmount(asi);
        }
        //ATT//ActuallySizeInteractions.Log("ASI &6 LEX-ATT &7 Hurt " + this.getScoreboardName() + " from &6 " + pDamageAmount + " &r to &e " + am.actuallysize$getAmount());

        // Continue
        actuallysize$lastHurterKb = pDamageSource.getDirectEntity();
        boolean ret = original.call(pDamageSource, (float) (double) am.actuallysize$getAmount());

        // Done using it
        am.actuallysize$setAmount(null);
        actuallysize$lastHurterKb = null;
        return ret;
    }

    @WrapMethod(method = "actuallyHurt")
    public void onActuallyHurtCall(DamageSource pDamageSource, float pDamageAmount, Operation<Void> original) {

        // Only adjust if it was not adjusted before
        AmountMatters am = (AmountMatters) pDamageSource;
        if (am.actuallysize$getAmount() == null) {

            // Adjust damage amount based on options
            double asi = ASIWorldSystemManager.ASICombatAdjust(pDamageAmount,  (LivingEntity) (Object) this, pDamageSource);

            // Put it in the Damage Source
            ((AmountMatters) pDamageSource).actuallysize$setAmount(asi);
        }
        //ATT//ActuallySizeInteractions.Log("ASI &6 LEX-ATT &7 Actually hurt " + this.getScoreboardName() + " for &e " + am.actuallysize$getAmount());

        // Continue
        original.call(pDamageSource, (float) (double) am.actuallysize$getAmount());

        // Done using it
        am.actuallysize$setAmount(null);
    }

    @WrapOperation(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"))
    public void onCombatKnockback(LivingEntity instance, double pStrength, double dx, double dy, Operation<Void> original) {
        //ATT//ActuallySizeInteractions.Log("ASI &6 LEX-ATT &7 Knockback cause " + this.getScoreboardName() + " initial &e @" + pStrength + " &d <" + dx + ", " + dy + ">");

        // Adjust kb strength based on scale
        double cookedStrength = pStrength;
        if (actuallysize$lastHurterKb != null && ActuallyServerConfig.strongBeegs) {
            double myScale = ASIUtilities.getEntityScale(this);
            double rsc = myScale / ASIUtilities.getEntityScale(actuallysize$lastHurterKb);  // Scale Relative to Victim

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
            //ATT//ActuallySizeInteractions.Log("ASI &6 LEX-ATT &7 Pre-adjusted {x" + rsc + "} to &6 @" + cookedStrength + " &r &e x" + combatScaleAmp + " /" + myScaleAmp);
        }

        // Continue
        original.call(instance, cookedStrength, dx, dy);
    }

    @WrapMethod(method = "knockback")
    public void onKnockback(double pStrength, double pX, double pZ, Operation<Void> original) {
        //ATT//ActuallySizeInteractions.Log("ASI &6 LEX-ATT &7 Knockback " + this.getScoreboardName() + " for &e @" + pStrength);

        // When specified in config
        double cookedStrength = pStrength;
        if (ActuallyServerConfig.tankyBeegs) {

            // When beeg, decrease knockback
            double myScale = ASIUtilities.getEntityScale(this);
            if (myScale > 1) { cookedStrength *= ASIUtilities.beegBalanceResist(myScale, 2, 0); }
            //ATT//ActuallySizeInteractions.Log("ASI &6 LEX-ATT &7 Modified kb by &e x" + ASIUtilities.beegBalanceInverse(myScale, 0, 0) + " &r to &6 @" + cookedStrength);
        }

        // Continue
        original.call(cookedStrength, pX, pZ);
    }

    @Inject(method = "handleDamageEvent", at = @At(value = "RETURN"))
    public void onHandleDamageWalkAnimationHurt(DamageSource pDamageSource, CallbackInfo ci) {

        // When there is a direct entity, use that instead of environmental
        double otherScale = 1;
        if (pDamageSource.getDirectEntity() != null) { otherScale = ASIUtilities.getEntityScale(pDamageSource.getDirectEntity()); }
        double myScale = ASIUtilities.getEntityScale(this);

        // Muffle the animation using our relative sizes
        if (myScale > otherScale) {
            this.walkAnimation.setSpeed((float) (this.walkAnimation.speed() * otherScale / myScale));
        }
    }

    @Unique
    boolean actuallysize$wasConsumed;

    @Unique
    @Nullable FoodProperties actuallysize$edaciousProperties;

    @Override
    public @Nullable FoodProperties actuallysize$getEdaciousProperties() { return actuallysize$edaciousProperties; }

    @Override
    public void actuallysize$setEdaciousProperties(@Nullable FoodProperties props) { }

    @Override
    public void actuallysize$setWasConsumed(boolean eda) {

        // Changes in consumption reset these properties
        actuallysize$edaciousProperties = null;
        actuallysize$wasConsumed = eda;
    }

    @Override
    public boolean actuallysize$wasConsumed() { return actuallysize$wasConsumed; }

    @Definition(id = "drops", local = @Local(type = Collection.class))
    @Expression("drops")
    @ModifyExpressionValue(method = "dropAllDeathLoot", at = @At("MIXINEXTRAS:EXPRESSION"))
    @NotNull Collection<ItemEntity> edaciousDrops(@NotNull Collection<ItemEntity> original) {

        // Upon death, this stuff is reset
        if (!actuallysize$wasConsumed()) { return original; }
        LivingEntity thisEntity = (LivingEntity) (Object) this;

        // If death from consumption, calculate the result of eating everything edible
        ArrayList<ItemEntity> postFiltered = new ArrayList<>();
        for (ItemEntity ori : original) {

            // Immediately add non-edibles
            ItemStack within = ori.getItem();
            if (!within.isEdible()) {
                postFiltered.add(ori);
                continue;
            }

            // Okay now we can talk about obtaining food from the food items
            for (int i = 0; i < within.getCount(); i++) {

                // Simulate eating count amount of times
                FoodProperties oriProperties = ori.getItem().getFoodProperties(thisEntity);
                if (oriProperties == null) {continue;}
                if (actuallysize$edaciousProperties == null) {
                    actuallysize$edaciousProperties = oriProperties;

                    // Combine with the previous
                } else {
                    actuallysize$edaciousProperties = ((Combinable<FoodProperties>) actuallysize$edaciousProperties)
                            .actuallysize$combineWith(oriProperties);
                }
            }

            //FOO//ActuallySizeInteractions.Log("ASI &1 LMX-FOO &r Edacious conserve &6 " + within.getDisplayName().getString() + "x" + within.getCount() + " &r to &e F" + actuallysize$edaciousProperties.getNutrition() + " &b S" + actuallysize$edaciousProperties.getSaturationModifier());

            // Delete this item
            within.setCount(0);
            ori.remove(RemovalReason.DISCARDED);
        }

        //FOO//ActuallySizeInteractions.Log("ASI &1 LMX-FOO &r Edacious result &6 " + this.getClass().getSimpleName() + " &b " + (actuallysize$edaciousProperties == null ? "null" : "F" + actuallysize$edaciousProperties.getNutrition() + " S" + actuallysize$edaciousProperties.getSaturationModifier()));

        // Return the resultant items
        return postFiltered;
    }

    @WrapOperation(method = "startUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseDuration()I"))
    public int resetItemUseCache(ItemStack instance, Operation<Integer> original) {

        // Reset cache ticks
        ((UseTimed) (Object) instance).actuallysize$setUseTimeTicks(0);

        // Proceed
        return original.call(instance);
    }
}
