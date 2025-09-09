package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {

    public ServerPlayerMixin(Level pLevel, BlockPos pPos, float pYRot, GameProfile pGameProfile) { super(pLevel, pPos, pYRot, pGameProfile); }

    @Inject(method = "startSleepInBed", at = @At(value = "HEAD"), cancellable = true)
    public void onStartSleeping(BlockPos pAt, CallbackInfoReturnable<Either<BedSleepingProblem, Unit>> cir) {

        // If held, you cannot sleep
        EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) this;
        if (dualityEntity.actuallysize$isHeld()) {
            cir.setReturnValue(Either.left(BedSleepingProblem.OTHER_PROBLEM));
            cir.cancel();
        }
    }
}
