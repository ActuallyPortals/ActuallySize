package actually.portals.ActuallySize.mixin.holding.activation;

import actually.portals.ActuallySize.pickup.ASIPickupSystemManager;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityActivationAction;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityDeactivationAction;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityFluxAction;
import actually.portals.ActuallySize.pickup.item.ASIPSHeldEntityItem;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import actually.portals.ActuallySize.world.mixininterfaces.ForceSlotSynchronization;
import com.google.common.base.Suppliers;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import gunging.ootilities.GungingOotilitiesMod.exploring.players.ISPExplorerStatements;
import gunging.ootilities.GungingOotilitiesMod.exploring.players.ISPPlayerLocation;
import gunging.ootilities.GungingOotilitiesMod.exploring.players.ISPPlayerStatement;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;
import java.util.function.Supplier;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin implements ForceSlotSynchronization {

    @Shadow
    @Final
    private NonNullList<ItemStack> remoteSlots;

    @Shadow
    @Final
    public NonNullList<Slot> slots;

    @Shadow
    protected abstract void triggerSlotListeners(int pSlotIndex, ItemStack pStack, Supplier<ItemStack> pSupplier);

    @Shadow
    protected abstract void synchronizeSlotToRemote(int pSlotIndex, ItemStack pStack, Supplier<ItemStack> pSupplier);

    @Shadow private ItemStack remoteCarried;
    @Unique
    @Nullable ItemStack actuallysize$itemCounterpart;
    @Unique
    @Nullable Player actuallysize$entityCounterpart;
    @Unique
    @Nullable Player actuallysize$holder;
    @Unique
    @Nullable ItemStackLocation<? extends Entity> actuallysize$stackLocation;

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
                    if (asHolder instanceof Player) {actuallysize$holder = (Player) asHolder;}
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
                    case 36:
                        locationStatement = ISPExplorerStatements.FEET;
                        break;
                    case 37:
                        locationStatement = ISPExplorerStatements.LEGS;
                        break;
                    case 38:
                        locationStatement = ISPExplorerStatements.CHEST;
                        break;
                    case 39:
                        locationStatement = ISPExplorerStatements.HEAD;
                        break;
                    default:
                        locationStatement = null;
                        break;
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
            ASIPickupSystemManager.processHotbarSlots((ServerPlayer) pPlayer, false);
        }
    }

    /*
    @WrapMethod(method = "addSlot")
    Slot WhenSlotAdded(Slot pSlot, Operation<Slot> original) {

        // Somewhy, opening a chests is unbinding player held entities which is crazy. This allows to debug that

        int originalIndex = pSlot.index;
        Slot ret = original.call(pSlot);
        ItemStack itemStack = ret.getItem();
        ActuallySizeInteractions.Log("WSA Added [X={2}, Y={3}] #{0} (originally {1}): {4} ({5})", ret.index, originalIndex, ret.x, ret.y, itemStack.getDisplayName().getString(), (((ItemDualityCounterpart) (Object) itemStack).actuallysize$getEntityCounterpart() == null ? "null" : ((ItemDualityCounterpart) (Object) itemStack).actuallysize$getEntityCounterpart().getScoreboardName() + " CLIENTSIDE=" + ((ItemDualityCounterpart) (Object) itemStack).actuallysize$getEntityCounterpart().level().isClientSide));
        return ret;
    }
    //*/

    @Override
    public void actuallysize$synchronizeSlotToRemote(int i) {

        // Identify the new item to send
        ItemStack itemstack = this.slots.get(i).getItem();

        // Force sync by changing what's in there
        if (itemstack.getItem() == Items.AIR) {
            this.remoteSlots.set(i, Items.TORCH.getDefaultInstance());

            // Set to AIR to guarantee it's not there
        } else {
            this.remoteSlots.set(i, Items.AIR.getDefaultInstance());
        }

        // Send sync
        Supplier<ItemStack> supplier = Suppliers.memoize(itemstack::copy);
        this.triggerSlotListeners(i, itemstack, supplier);
        this.synchronizeSlotToRemote(i, itemstack, supplier);
    }

    @Inject(method = "synchronizeSlotToRemote", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/NonNullList;set(ILjava/lang/Object;)Ljava/lang/Object;"), cancellable = true)
    protected void onSynchronizeSlotToRemoteMid(int pSlotIndex, ItemStack pStack, Supplier<ItemStack> pSupplier, CallbackInfo ci) {

        // Only the business of ASI when syncing an Item-Entity duality that is active
        ItemDualityCounterpart dualityItem = (ItemDualityCounterpart) (Object) pStack;
        if (dualityItem == null) {return;}
        if (!dualityItem.actuallysize$isDualityActive()) {return;}
        Entity entityCounterpart = dualityItem.actuallysize$getEntityCounterpart();
        if (entityCounterpart instanceof Player) {return;}
        Entity enclosedEntity = dualityItem.actuallysize$getEnclosedEntity(entityCounterpart.level());
        if (enclosedEntity == null) {return;}

        /*
         * What is currently in the remote?
         */
        ItemStack asRemote = this.remoteSlots.get(pSlotIndex);
        ItemDualityCounterpart remoteItem = (ItemDualityCounterpart) (Object) asRemote;
        if (remoteItem == null) {return;}

        // Check the remote enclosed entity
        Entity remoteEnclosed = remoteItem.actuallysize$getEnclosedEntity(entityCounterpart.level());
        if (remoteEnclosed == null) {return;}
        UUID remoteUUID = remoteItem.actuallysize$getEnclosedEntityUUID();
        UUID dualityUUID = dualityItem.actuallysize$getEnclosedEntityUUID();
        if (dualityUUID == null || remoteUUID == null) {return;}

        // Identical UUIDs? Cancel this method call
        if (remoteUUID.equals(dualityUUID)) {
            ci.cancel();

            /*
             * We did cancel this from sending it over the
             * network, but updating remote slots must happen
             */
            this.remoteSlots.set(pSlotIndex, pSupplier.get());
        }
    }

    @WrapMethod(method = "setRemoteCarried")
    void whenRemoteCarriedChanged(ItemStack newRemoteCarried, Operation<Void> original) {

        // When the remote changes
        if (!ItemStack.isSameItemSameTags(newRemoteCarried, remoteCarried)) {

            /*
             * We want to register deactivation and activation
             * duality flux actions focused on the cursor. This
             * requires special attention since it is inside a
             * container and not necessarily THE inventor cursor.
             */

            if (newRemoteCarried.getItem() instanceof ASIPSHeldEntityItem) {

                // The new remote sends an activation packet
                //*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "RCC", "Remote carried update, reactivating {0}", stackLocation);
                //ASINCItemEntityActivationPacket packet = new ASINCItemEntityActivationPacket(stackLocation, action.getEntityCounterpart(), action.getHoldPoint());
                //ASINetworkManager.broadcastEntityUpdate(action.getEntityCounterpart(), packet);
            }

            if (remoteCarried.getItem() instanceof ASIPSHeldEntityItem) {

            }
        }

        original.call(newRemoteCarried);
    }
}
