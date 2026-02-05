package actually.portals.ActuallySize.mixin.third.create;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallyServerConfig;
import actually.portals.ActuallySize.compatibilities.create.BeegCreateKinetics;
import actually.portals.ActuallySize.compatibilities.create.BeegCreateMechanics;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.crank.HandCrankBlock;
import com.simibubi.create.content.kinetics.crank.HandCrankBlockEntity;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = HandCrankBlock.class, remap = false)
public class HandCrankBlockMixin {

    @Nullable @Unique
    private static BeegCreateMechanics actuallysize$lastCranker;

    @Inject(method = "use", at = @At("HEAD"), cancellable = true, remap = true)
    public void OnHandCranking(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {

        // Tinies cannot operate the crankshaft :(
        double size = actuallysize$lastCranker == null ? 1 : actuallysize$lastCranker.getSize();
        if (size < ActuallyServerConfig.tiniestCranker) {
            cir.setReturnValue(InteractionResult.PASS);
            cir.cancel();
            return;
        }

        // Beegs are clumsy with the crankshaft
        double max = ActuallyServerConfig.largestCranker;
        if (size > max) {

            // Works normally until it randomly breaks
            if (player instanceof ServerPlayer && OotilityNumbers.rollSuccess(size / (max * 200))) {
                ((ServerPlayer) player).gameMode.destroyBlock(pos);
                cir.setReturnValue(InteractionResult.SUCCESS);
                cir.cancel();
            }
        }
    }

    @WrapMethod(method = "use", remap = true)
    private InteractionResult OnNewCranker(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit, Operation<InteractionResult> original) {

        actuallysize$lastCranker = new BeegCreateMechanics(player);
        actuallysize$lastCranker.setSpeedMultiplier(ASIUtilities.beegBalanceEnhance(actuallysize$lastCranker.getSize() * actuallysize$lastCranker.getSize(), 1, 0));
        actuallysize$lastCranker.setStressCapacityMultiplier(actuallysize$lastCranker.getSize());
        InteractionResult ret = original.call(state, worldIn, pos, player, handIn, hit);
        actuallysize$lastCranker = null;

        return ret;
    }

    @WrapOperation(method = "lambda$use$0", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/crank/HandCrankBlockEntity;turn(Z)V"))
    private static void OnActuallyCranking(HandCrankBlockEntity instance, boolean back, Operation<Void> original) {

        BeegCreateKinetics asKinetic = (BeegCreateKinetics) instance;
        asKinetic.actuallysize$setKineticBeeg(actuallysize$lastCranker);
        original.call(instance, back);
    }
    
    @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"), remap = true)
    public void OnHandCranking(Player instance, float pExhaustion, Operation<Void> original) {
        
        // Beegs will resist crankshaft hunger
        double size = ASIUtilities.getEffectiveSize(instance);
        original.call(instance, pExhaustion * ((float) ASIUtilities.beegBalanceResist(size * size, 2, 0)));
    }
}
