package actually.portals.ActuallySize.mixin.holding.activation.menus;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.pickup.ASIPickupSystemManager;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityActivationAction;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityDeactivationAction;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityFluxAction;
import actually.portals.ActuallySize.pickup.item.ASIPSHeldEntityItem;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import gunging.ootilities.GungingOotilitiesMod.exploring.players.ISPExplorerStatements;
import gunging.ootilities.GungingOotilitiesMod.exploring.players.ISPPlayerLocation;
import gunging.ootilities.GungingOotilitiesMod.exploring.players.ISPPlayerStatement;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {

    @Shadow public abstract int incrementStateId();

    @Unique @Nullable ItemStack actuallysize$itemCounterpart;
    @Unique @Nullable Player actuallysize$entityCounterpart;
    @Unique @Nullable Player actuallysize$holder;
    @Unique @Nullable ItemStackLocation<? extends Entity> actuallysize$stackLocation;

    @WrapMethod(method = "moveItemStackTo")
    boolean WhenHeldPlayerQuickMoved(ItemStack pStack, int pStartIndex, int pEndIndex, boolean pReverseDirection, Operation<Boolean> original) {

        // Identify when players pass each other around
        actuallysize$itemCounterpart = null;
        if (pStack.getItem() instanceof ASIPSHeldEntityItem && ((ASIPSHeldEntityItem) pStack.getItem()).isPlayer()) {
            actuallysize$itemCounterpart = pStack;
            ItemDualityCounterpart dualityItem = (ItemDualityCounterpart) (Object) actuallysize$itemCounterpart;
            if (dualityItem.actuallysize$getEntityCounterpart() instanceof Player) {
                actuallysize$entityCounterpart = (Player) dualityItem.actuallysize$getEntityCounterpart();
                actuallysize$stackLocation = dualityItem.actuallysize$getItemStackLocation();
                if (actuallysize$entityCounterpart != null && actuallysize$stackLocation != null) {
                    ItemEntityDualityHolder asHolder = ((EntityDualityCounterpart) actuallysize$entityCounterpart).actuallysize$getItemEntityHolder();
                    if (asHolder instanceof Player) { actuallysize$holder = (Player) asHolder; }
                }
            }

            // Reset if not players
            if (actuallysize$holder == null) {
                actuallysize$itemCounterpart = null;
                actuallysize$entityCounterpart = null;
                actuallysize$stackLocation = null;
            }
        }

        boolean ret = original.call(pStack, pStartIndex, pEndIndex, pReverseDirection);

        // Reset at end
        if (actuallysize$itemCounterpart != null) {
            actuallysize$itemCounterpart = null;
            actuallysize$entityCounterpart = null;
            actuallysize$holder = null;
            actuallysize$stackLocation = null;
        }

        return ret;
    }

    @WrapOperation(method = "moveItemStackTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;setByPlayer(Lnet/minecraft/world/item/ItemStack;)V"))
    void WhenHeldPlayerQuickMoved(Slot instance, ItemStack pStack, Operation<Void> original) {

        original.call(instance, pStack);

        // When players wee passing each other around
        if (actuallysize$itemCounterpart != null) {
            //ActuallySizeInteractions.Log("MIST Moving held player {0} item {1} to #{2} item {3}", actuallysize$entityCounterpart.getScoreboardName(), actuallysize$itemCounterpart.getDisplayName().getString(), instance.getSlotIndex(), pStack.getDisplayName().getString());

            /*
             * Measured:
             *
             * Standard: [0, 35]
             * Armor: [36 (boots), 39 (helmet)]
             * Offhand: 40
             */
            ISPPlayerStatement locationStatement;
            if (instance.getSlotIndex() == 40) {
                locationStatement = ISPExplorerStatements.OFFHAND;
            } else if (instance.getSlotIndex() >= 0 && instance.getSlotIndex() < 36) {
                locationStatement = ISPExplorerStatements.STANDARD.of(instance.getSlotIndex());
            } else {
                switch (instance.getSlotIndex()) {
                    case 36: locationStatement = ISPExplorerStatements.FEET; break;
                    case 37: locationStatement = ISPExplorerStatements.LEGS; break;
                    case 38: locationStatement = ISPExplorerStatements.CHEST; break;
                    case 39: locationStatement = ISPExplorerStatements.HEAD; break;
                    default: locationStatement = null; break;
                }
            }

            // Fire an activation action
            if (locationStatement != null) {
                ASIPSDualityActivationAction action = new ASIPSDualityActivationAction(
                        new ISPPlayerLocation(actuallysize$holder, locationStatement), pStack, actuallysize$entityCounterpart);
                ASIPSDualityDeactivationAction inaction = new ASIPSDualityDeactivationAction(actuallysize$stackLocation);
                ASIPSDualityFluxAction flux = new ASIPSDualityFluxAction(inaction, action);
                flux.tryResolve();
            }
        }
    }

    @Inject(method = "doClick", at = @At("RETURN"))
    void OnHotbarHotkeySwap(int pSlotId, int pButton, ClickType pClickType, Player pPlayer, CallbackInfo ci) {

        // Hotbar swaps trigger Hotbar Fluxes
        if (pClickType == ClickType.SWAP && (pPlayer instanceof ServerPlayer)) {
            ASIPickupSystemManager.processHotbarSlots((ServerPlayer) pPlayer, false); }
    }
}
