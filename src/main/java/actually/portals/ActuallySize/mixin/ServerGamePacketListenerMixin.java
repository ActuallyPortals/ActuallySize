package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.GraceImpulsable;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;

@Mixin(value = ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerMixin {

    @Shadow public abstract ServerPlayer getPlayer();

    @Shadow @Nullable private Entity lastVehicle;
    @Shadow public ServerPlayer player;

    @ModifyExpressionValue(method = "handleMoveVehicle", at = @At(value = "CONSTANT", args = "doubleValue=100.0"))
    public double onMovingToQuicklyVehicle(double original) {

        // When held, this depends on the beeg
        double scale;
        EntityDualityCounterpart entityCounterpart = ((EntityDualityCounterpart) player);
        if (entityCounterpart.actuallysize$isHeld() && entityCounterpart.actuallysize$getItemEntityHolder() instanceof Entity) {

            // This is only really a problem at large scales
            scale = ASIUtilities.getEntityScale((Entity) entityCounterpart.actuallysize$getItemEntityHolder());

        // Not held? Then use your own scale
        } else {
            if (lastVehicle == null) { return original; }
            scale = ASIUtilities.getEntityScale(lastVehicle);
        }


        // This is only really a problem at large scales
        if (scale < 1) { return original; }

        // Increase rubber-banding distance
        return original * scale;
    }

    @WrapOperation(method = "handleMoveVehicle", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;isSingleplayerOwner()Z"))
    public boolean canBypassCheaterVehicleCheck(ServerGamePacketListenerImpl instance, Operation<Boolean> original) {

        // Bypass through Grace Impulse
        if (this.player != null) {
            GraceImpulsable imp = (GraceImpulsable) this.player;
            if (imp.actuallysize$isInGraceImpulse()) { return true; } }

        return original.call(instance);
    }

    @WrapOperation(method = "handleMovePlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;isSingleplayerOwner()Z"))
    public boolean canBypassCheaterMoveCheck(ServerGamePacketListenerImpl instance, Operation<Boolean> original) {

        // Bypass through Grace Impulse
        if (this.player != null) {
            GraceImpulsable imp = (GraceImpulsable) this.player;
            if (imp.actuallysize$isInGraceImpulse()) { return true; } }

        return original.call(instance);
    }

    @ModifyExpressionValue(method = "handleMovePlayer", at = @At(value = "CONSTANT", args = "floatValue=100.0"))
    public float onMovingToQuicklyWalk(float original) {
        if (this.player == null) { return original; }

        // When held, this depends on the beeg
        double scale;
        EntityDualityCounterpart entityCounterpart = ((EntityDualityCounterpart) player);
        if (entityCounterpart.actuallysize$isHeld() && entityCounterpart.actuallysize$getItemEntityHolder() instanceof Entity) {

            // This is only really a problem at large scales
            scale = ASIUtilities.getEntityScale((Entity) entityCounterpart.actuallysize$getItemEntityHolder());

            // Not held? Then use your own scale
        } else {
            if (this.player == null) { return original; }
            scale = ASIUtilities.getEntityScale(this.player);
        }

        // This is only really a problem at large scales
        if (scale < 1) { return original; }

        // Increase rubber-banding distance
        return (float) (original * scale);
    }

    @ModifyExpressionValue(method = "handleMovePlayer", at = @At(value = "CONSTANT", args = "floatValue=300.0"))
    public float onMovingToQuicklyFlight(float original) {

        // When held, this depends on the beeg
        double scale;
        EntityDualityCounterpart entityCounterpart = ((EntityDualityCounterpart) player);
        if (entityCounterpart.actuallysize$isHeld() && entityCounterpart.actuallysize$getItemEntityHolder() instanceof Entity) {

            // This is only really a problem at large scales
            scale = ASIUtilities.getEntityScale((Entity) entityCounterpart.actuallysize$getItemEntityHolder());

            // Not held? Then use your own scale
        } else {
            if (this.player == null) { return original; }
            scale = ASIUtilities.getEntityScale(this.player);
        }

        // This is only really a problem at large scales
        if (scale < 1) { return original; }

        // Increase rubber-banding distance
        return (float) (original * scale);
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "CONSTANT", args = "intValue=80"))
    public int onFlyingKicking(int original) {

        // When held, this depends on the beeg
        double scale;
        EntityDualityCounterpart entityCounterpart = ((EntityDualityCounterpart) player);
        if (entityCounterpart.actuallysize$isHeld() && entityCounterpart.actuallysize$getItemEntityHolder() instanceof Entity) {

            // This is only really a problem at large scales
            scale = ASIUtilities.getEntityScale((Entity) entityCounterpart.actuallysize$getItemEntityHolder());

            // Not held? Then use your own scale
        } else {
            if (this.player == null) { return original; }
            scale = ASIUtilities.getEntityScale(this.player);
        }

        // This is only really a problem at small scales
        if (scale >= 1) { return original; }

        // Increase allowed float distance
        return (int) Math.ceil((original / scale));
    }

    @Definition(id = "isPassenger", method = "Lnet/minecraft/server/level/ServerPlayer;isPassenger()Z")
    @Expression("?.isPassenger()")
    @ModifyExpressionValue(method = "tick", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    public boolean onIsPassengerTickTest(boolean original) {

        // Already a passenger? We are done
        if (original) { return true; }

        /*
         * Alternatively, it is also a passenger for this
         * context when it is held by another entity. Emphasis
         * on it being for THIS CONTEXT.
         */
        return ((EntityDualityCounterpart) this.player).actuallysize$isHeld();
    }
}
