package actually.portals.ActuallySize.mixin.holding.activation;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.netcode.ASINetworkManager;
import actually.portals.ActuallySize.netcode.packets.clientbound.ASINCItemEntityActivationPacket;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemDualityCounterpart;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/server/level/ServerPlayer$1")
public abstract class ContainerSynchronizerMixin {

    /*
     * Special thanks to Mixin God mumfrey for answering how to
     * do this (a few years ago) in the SpongePowered discord.
     */
    @Shadow @Final
    ServerPlayer this$0;

    @Inject(method = "sendSlotChange", at = @At("RETURN"))
    protected void onSendSlotChangeReturn(AbstractContainerMenu inventory, int slot, ItemStack update, CallbackInfo ci) {

        if (update.isEmpty()) { return; }

        // Only the business of ASI when syncing an Item-Entity duality that is active
        ItemDualityCounterpart itemDuality = (ItemDualityCounterpart) (Object) update;
        if (itemDuality == null) { return; }
        if (!itemDuality.actuallysize$isDualityActive()) { return; }

        // I suppose, if it is about to get changed, re-send the activation packet at least
        Entity entityCounterpart = itemDuality.actuallysize$getEntityCounterpart();
        ItemStackLocation<? extends Entity> stackLocation = itemDuality.actuallysize$getItemStackLocation();
        if (stackLocation == null || entityCounterpart == null) { return; }

        // Send another packet to link entity
        /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "EDS", "&f SendSlotChange activation packet for {0}", stackLocation);
        ASINCItemEntityActivationPacket packet = new ASINCItemEntityActivationPacket(stackLocation, entityCounterpart, ((EntityDualityCounterpart) entityCounterpart).actuallysize$getHoldPoint());
        ASINetworkManager.serverToPlayer(this$0, packet);
    }
}
