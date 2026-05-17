package actually.portals.ActuallySize.mixin.holding.activation.menus;

import actually.portals.ActuallySize.pickup.item.ASIPSHeldEntityItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StonecutterMenu.class)
public abstract class StonecutterMenuMixin extends AbstractContainerMenu {

    protected StonecutterMenuMixin(@Nullable MenuType<?> pMenuType, int pContainerId) {
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
                if (asHeld.isPlayer()) { cir.setReturnValue(ItemStack.EMPTY); cir.cancel();}
            }
        }
    }
}
