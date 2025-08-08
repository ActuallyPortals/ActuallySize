package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {

    public LocalPlayerMixin(ClientLevel pClientLevel, GameProfile pGameProfile) {
        super(pClientLevel, pGameProfile);
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
