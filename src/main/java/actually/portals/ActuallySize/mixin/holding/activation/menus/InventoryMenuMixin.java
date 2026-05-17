package actually.portals.ActuallySize.mixin.holding.activation.menus;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.pickup.item.ASIPSHeldEntityItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin extends RecipeBookMenu<CraftingContainer> {

    public InventoryMenuMixin(MenuType<?> pMenuType, int pContainerId) {
        super(pMenuType, pContainerId);
    }

    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    public void OnQuickMoveStack(Player pPlayer, int pIndex, CallbackInfoReturnable<ItemStack> cir) {

        // Must find which item is being quick-moved
        Slot slot = this.slots.get(pIndex);
        if (slot.hasItem()) {

            // Identify if this item is an ASI Held Entity
            ItemStack quickStack = slot.getItem();
            if (quickStack.getItem() instanceof ASIPSHeldEntityItem) {
                ASIPSHeldEntityItem asHeld = (ASIPSHeldEntityItem) quickStack.getItem();

                // Players cannot be quick-stacked
                if (asHeld.isPlayer()) {
                    //ActuallySizeInteractions.Log("QMS [SERVERSIDE={0}] Moving held player '{1}'...", !pPlayer.level().isClientSide(), quickStack.getDisplayName().getString());
                    ItemStack copyStack = quickStack.copy();

                    // From armor slots to the hotbar
                    if (pIndex >= 5 && pIndex < 9) {
                        if (!this.moveItemStackTo(quickStack, 36, 46, false)) { copyStack = ItemStack.EMPTY; }

                    // From hotbar to offhand and armor
                    } else if (pIndex >= 36 && pIndex < 45) {

                        // Armor if offhand unavailable
                        if (this.slots.get(45).hasItem()) {
                            if (!this.moveItemStackTo(quickStack, 5, 9, false)) {
                                copyStack = ItemStack.EMPTY;
                            }

                        // Offhand available
                        } else {
                            if (!this.moveItemStackTo(quickStack, 45, 46, false)) {
                                copyStack = ItemStack.EMPTY;
                            }
                        }

                    // From offhand to hotbar and armor
                    } else if (pIndex == 45) {

                        boolean hotbarAvailable = false;
                        for (int i = 36; i < 45; i++) {
                            if (!this.slots.get(i).hasItem()) { hotbarAvailable = true; i = 45; } }

                        // Hotbar if available
                        if (hotbarAvailable) {
                            if (!this.moveItemStackTo(quickStack, 36, 45, false)) {
                                copyStack = ItemStack.EMPTY;
                            }

                        // Armor, then
                        } else {
                            if (!this.moveItemStackTo(quickStack, 5, 9, false)) {
                                copyStack = ItemStack.EMPTY;
                            }
                        }

                    } else { copyStack = ItemStack.EMPTY; }

                    if (quickStack.isEmpty()) {
                        slot.setByPlayer(ItemStack.EMPTY);
                    } else {
                        slot.setChanged();
                    }

                    if (quickStack.getCount() == copyStack.getCount()) {
                        copyStack = ItemStack.EMPTY;
                    }

                    slot.onTake(pPlayer, quickStack);
                    if (pIndex == 0) {
                        pPlayer.drop(quickStack, false);
                    }

                    cir.setReturnValue(copyStack); cir.cancel();
                }
            }
        }
    }
}
