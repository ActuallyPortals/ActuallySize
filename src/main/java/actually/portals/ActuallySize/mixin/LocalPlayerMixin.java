package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.netcode.ASINetworkManager;
import actually.portals.ActuallySize.netcode.packets.serverbound.ASINSStrugglePacket;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import gunging.ootilities.GungingOotilitiesMod.scheduling.SchedulingManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {

    @Shadow public Input input;

    public LocalPlayerMixin(ClientLevel pClientLevel, GameProfile pGameProfile) {
        super(pClientLevel, pGameProfile);
    }

    @WrapOperation(method = "canAutoJump", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isPassenger()Z"))
    public boolean onRidePreventAutoJump(LocalPlayer instance, Operation<Boolean> original) {

        // When held, we cannot elytra glide
        if (((EntityDualityCounterpart) instance).actuallysize$isHeld()) { return true; }

        // Otherwise, ASI has no business with this operation
        return original.call(instance);
    }

    @Inject(method = "canStartSprinting", at = @At(value = "HEAD"), cancellable = true)
    public void onRidePreventSprint(CallbackInfoReturnable<Boolean> cir) {

        // When held, we cannot sprint
        if (((EntityDualityCounterpart) this).actuallysize$isHeld()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Unique
    boolean actuallysize$wasStruggling;

    @Inject(method = "tick", at = @At(value = "RETURN"))
    public void onStruggle(CallbackInfo ci) {

        /*
         * Struggle packet sent
         */
        EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) this;
        if (wantsToStopRiding() && dualityEntity.actuallysize$isHeld()) {
            //this.input.shiftKeyDown = false;  // No need to prevent crouching, really

            // Only struggle on fresh presses
            if (!actuallysize$wasStruggling) {
                actuallysize$wasStruggling = true;
                ASINSStrugglePacket packet = new ASINSStrugglePacket(SchedulingManager.getClientTicks());
                ASINetworkManager.playerToServer(packet);

            }
        } else { actuallysize$wasStruggling = false; }
    }

    /*      Despite it being disabled when passenger, there is no problem when held
    @ModifyExpressionValue(method = "sendPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isControlledCamera()Z"))
    public boolean onSendPositionAsPassenger(boolean original) {

        // Already not controlling the camera? No point in debating it
        if (!original) { return false; }

        /*
         *  When held in some beeg's hand, you do not control
         *  your position packets that's kind of how it works.
         *
        EntityDualityCounterpart entityCounterpart = (EntityDualityCounterpart) this;
        return true; //LCL//!entityCounterpart.actuallysize$isHeld();
    }
    //*/

    /*      Despite it being disabled when passenger, there is no problem when held
    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isPassenger()Z"))
    public boolean onTickIsPassenger(boolean original) {

        /*
         * I don't think it is possible to be riding
         * something while being held lol but anyway
         *
        EntityDualityCounterpart entityCounterpart = (EntityDualityCounterpart) this;
        if (entityCounterpart.actuallysize$isHeld()) { return false; }

        // If we are not held, return the original value
        return original;
    }
    //*/

    /*      Despite it being disabled when passenger, there is no problem when held
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;sendPosition()V"))
    public void onSendPositionControl(LocalPlayer instance, Operation<Void> original) {

        /*
         *  When held in some beeg's hand, you do not control
         *  your position packets that's kind of how it works.
         *  Then we stop this method from sending ride control
         *  position packets
         *
        EntityDualityCounterpart entityCounterpart = (EntityDualityCounterpart) this;
        if (entityCounterpart.actuallysize$isHeld()) { return; }

        // Proceed as normal
        original.call(instance);
    }
    //*/
}
