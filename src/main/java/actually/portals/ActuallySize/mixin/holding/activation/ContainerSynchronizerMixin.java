package actually.portals.ActuallySize.mixin.holding.activation;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.netcode.ASINetworkManager;
import actually.portals.ActuallySize.netcode.packets.clientbound.ASINCItemEntityActivationPacket;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityActivationAction;
import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import actually.portals.ActuallySize.pickup.item.ASIPSHeldEntityItem;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import gunging.ootilities.GungingOotilitiesMod.exploring.players.ISPExplorerStatements;
import gunging.ootilities.GungingOotilitiesMod.exploring.players.ISPPlayerLocation;
import gunging.ootilities.GungingOotilitiesMod.exploring.players.ISPPlayerStatement;
import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(targets = "net/minecraft/server/level/ServerPlayer$1")
public abstract class ContainerSynchronizerMixin {

    /*
     * Special thanks to Mixin God mumfrey for answering how to
     * do this (a few years ago) in the SpongePowered discord.
     */
    @Shadow @Final
    ServerPlayer this$0;

    @Inject(method = "sendInitialData", at = @At("RETURN"))
    protected void onOpenedContainer(AbstractContainerMenu p_143448_, NonNullList<ItemStack> p_143449_, ItemStack p_143450_, int[] p_143451_, CallbackInfo ci) {

        // Make sure to send hotbar activation packets
        int containerSize = p_143449_.size();
        int firstHotbarSlot = containerSize - 9;
        for (int i = firstHotbarSlot; i < containerSize; i++) {

            // Check the hotbar items
            ItemStack obs = p_143449_.get(i);
            if (!(obs.getItem() instanceof ASIPSHeldEntityItem)) { continue; }
            if (!((ASIPSHeldEntityItem) obs.getItem()).isPlayer()) { continue; }

            // Build activation packet
            int asHotbarSlot = i - firstHotbarSlot;
            ISPPlayerStatement statement = ISPExplorerStatements.STANDARD.of(asHotbarSlot);
            if (asHotbarSlot == this$0.getInventory().selected) { statement = ISPExplorerStatements.MAINHAND; }
            ItemStackLocation<? extends Entity> stackLocation = new ISPPlayerLocation(this$0, statement);
            ASIPSDualityActivationAction action = new ASIPSDualityActivationAction(stackLocation);
            if (!action.isVerified()) { continue; }

            // Send activation packet
            /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "EDS", "&f SendInitialData activation packet for {0}", stackLocation);
            ASINCItemEntityActivationPacket packet = new ASINCItemEntityActivationPacket(stackLocation, action.getEntityCounterpart(), action.getHoldPoint());
            ASINetworkManager.broadcastEntityUpdate(action.getEntityCounterpart(), packet);
        }
    }

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
