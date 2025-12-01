package actually.portals.ActuallySize.mixin.holding.activation;

import actually.portals.ActuallySize.netcode.ASIClientsidePacketHandler;
import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Shadow private ClientLevel level;

    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "handleSetEquipment", at = @At("RETURN"))
    protected void onHandleSetEquipmentReturn(ClientboundSetEquipmentPacket pPacket, CallbackInfo ci) {
        Entity ent = this.level.getEntity(pPacket.getEntity());
        if (ent == null) { return; }

        // This will also attempt to resolve enqueued duality activations :based:
        ASIClientsidePacketHandler.resolveEnqueuedDualityActivationsFor(ent.getUUID());
    }

    /*      // Superseded by the clock version in ASIEventExecutionListener.OnEverySecond()
    @Inject(method = "handleContainerSetSlot", at = @At("RETURN"))
    protected void onHandleContainerSetSlotReturn(ClientboundContainerSetSlotPacket pPacket, CallbackInfo ci) {
        if (this.minecraft.player == null) { return; }

        ActuallySizeInteractions.Log("Resolving Enqueued Duality Activations - BUT IT WAS COMMENTED! TEST IF THIS IS NEEDED");

        // ASI Start
        ASIClientsidePacketHandler.resolveEnqueuedDualityActivationsFor(this.minecraft.player.getUUID());
    }
     //*/

    @WrapOperation(method = "handleMoveEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundMoveEntityPacket;hasPosition()Z"))
    public boolean onLocalPlayerAuthority(ClientboundMoveEntityPacket instance, Operation<Boolean> original) {

        Entity entity = instance.getEntity(this.level);
        EntityDualityCounterpart entityCounterpart = (EntityDualityCounterpart) entity;
        ASIPSHoldPoint hold = entityCounterpart.actuallysize$getHoldPoint();

        // When held in a hold point, it becomes our business
        if (hold != null && hold.isClientsidePositionable()) {
            return false; }

        // Otherwise proceed as normal
        return original.call(instance);
    }
}
