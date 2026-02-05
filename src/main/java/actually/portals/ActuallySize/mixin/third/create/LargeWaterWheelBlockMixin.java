package actually.portals.ActuallySize.mixin.third.create;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallyServerConfig;
import actually.portals.ActuallySize.compatibilities.create.BeegCreateActuator;
import actually.portals.ActuallySize.compatibilities.create.BeegCreateKinetics;
import actually.portals.ActuallySize.compatibilities.create.BeegCreateMechanics;
import actually.portals.ActuallySize.world.grid.ASIWorldBlock;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.waterwheel.LargeWaterWheelBlock;
import com.simibubi.create.content.kinetics.waterwheel.LargeWaterWheelBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LargeWaterWheelBlock.class, remap = false)
public abstract class LargeWaterWheelBlockMixin extends RotatedPillarKineticBlock implements IBE<LargeWaterWheelBlockEntity> {

    public LargeWaterWheelBlockMixin(Properties properties) { super(properties); }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true, remap = true)
    public void OnUsedByPlayer(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand pHand, BlockHitResult pHit, CallbackInfoReturnable<InteractionResult> cir) {

        // Consider cranking, if you are not big enough just cancel
        BeegCreateMechanics cranking = new BeegCreateMechanics(player);
        if (cranking.getSize() < (ActuallyServerConfig.tiniestCranker * ActuallyServerConfig.waterwheelCranker)) { return; }
        double relativeSize = cranking.getSize() / ActuallyServerConfig.waterwheelCranker;

        // Prepare size-based cranking modifiers, identical math to the normal crankshaft
        cranking.setAddedSpeed(32 * ASIUtilities.beegBalanceEnhance(relativeSize * relativeSize, 1, 0) * (player.isShiftKeyDown() ? -1 : 1));
        cranking.setAddedStressCapacity(16 * cranking.getSize());

        // Attempt to turn, consume action on success
        withBlockEntityDo(worldIn, pos, be -> {
            BeegCreateKinetics asKinetics = (BeegCreateKinetics) be;
            asKinetics.actuallysize$setKineticBeeg(cranking);
            BeegCreateActuator asActuator = (BeegCreateActuator) be;
            InteractionResult result = asActuator.actuallysize$whenBeegUsed(new ASIWorldBlock(state, pos, worldIn), player, pHand, pHit);
            if (result.consumesAction()) { cir.setReturnValue(result); cir.cancel(); }
        });
    }
}
