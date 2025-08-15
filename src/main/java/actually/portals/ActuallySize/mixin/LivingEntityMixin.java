package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.netcode.ASINetworkManager;
import actually.portals.ActuallySize.netcode.packets.clientbound.ASINCItemEntityActivationPacket;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemDualityCounterpart;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import gunging.ootilities.GungingOotilitiesMod.exploring.entities.ISEEquipmentSlotted;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.UUID;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable, net.minecraftforge.common.extensions.IForgeLivingEntity {

    public LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) { super(pEntityType, pLevel); }

    @WrapMethod(method = "pushEntities")
    public void onPushEntities(Operation<Void> original) {

        // If the animal is held, it cannot mate
        EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) this;
        if (dualityEntity.actuallysize$isHeld()) { return; }

        // Not held not our business
        original.call();
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
    protected void onHandleEquipmentChangesReturn(Map<EquipmentSlot, ItemStack> pEquipments, CallbackInfo ci) {

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
            //HDA//ActuallySizeInteractions.Log("ASI &1 EDS &r Duality entity &f " + entityCounterpart.getScoreboardName() + " &7 in &e " + expected);

            // Send another packet to link entity
            //HDA//ActuallySizeInteractions.Log("ASI &1 EDS &r LivingEntityMixin.onHandleEquipmentChangesReturn(Map, CallbackInfo) ");
            ASINCItemEntityActivationPacket packet = new ASINCItemEntityActivationPacket(stackLocation, entityCounterpart);
            ASINetworkManager.broadcastEntityUpdate(this, packet);
        }
    }

    @Inject(method = "equipmentHasChanged", at = @At(value = "HEAD"), cancellable = true)
    protected void onEquipmentHasChangedCall(ItemStack pOldItem, ItemStack pNewItem, CallbackInfoReturnable<Boolean> cir) {

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
}
