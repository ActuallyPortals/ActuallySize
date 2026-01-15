package actually.portals.ActuallySize.mixin.holding.activation;

import actually.portals.ActuallySize.pickup.mixininterfaces.ItemDualityCounterpart;
import actually.portals.ActuallySize.world.mixininterfaces.ForceSlotSynchronization;
import com.google.common.base.Suppliers;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;
import java.util.function.Supplier;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin implements ForceSlotSynchronization {

    @Shadow @Final private NonNullList<ItemStack> remoteSlots;

    @Shadow @Final public NonNullList<Slot> slots;

    @Shadow protected abstract void triggerSlotListeners(int pSlotIndex, ItemStack pStack, Supplier<ItemStack> pSupplier);

    @Shadow protected abstract void synchronizeSlotToRemote(int pSlotIndex, ItemStack pStack, Supplier<ItemStack> pSupplier);

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
        if (dualityItem == null) { return; }
        if (!dualityItem.actuallysize$isDualityActive()) { return; }
        Entity entityCounterpart = dualityItem.actuallysize$getEntityCounterpart();
        if (entityCounterpart instanceof Player) { return; }
        Entity enclosedEntity = dualityItem.actuallysize$getEnclosedEntity(entityCounterpart.level());
        if (enclosedEntity == null) { return; }

        /*
         * What is currently in the remote?
         */
        ItemStack asRemote = this.remoteSlots.get(pSlotIndex);
        ItemDualityCounterpart remoteItem = (ItemDualityCounterpart) (Object) asRemote;
        if (remoteItem == null) { return; }

        // Check the remote enclosed entity
        Entity remoteEnclosed = remoteItem.actuallysize$getEnclosedEntity(entityCounterpart.level());
        if (remoteEnclosed == null) { return; }
        UUID remoteUUID = remoteItem.actuallysize$getEnclosedEntityUUID();
        UUID dualityUUID = dualityItem.actuallysize$getEnclosedEntityUUID();
        if (dualityUUID == null || remoteUUID == null) { return; }

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

}
