package actually.portals.ActuallySize.mixin.third.create;

import actually.portals.ActuallySize.compatibilities.create.BeegCreateActuator;
import actually.portals.ActuallySize.compatibilities.create.BeegCreateKinetics;
import actually.portals.ActuallySize.compatibilities.create.BeegCreateMechanics;
import actually.portals.ActuallySize.world.grid.ASIWorldBlock;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.waterwheel.WaterWheelBlockEntity;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WaterWheelBlockEntity.class, remap = false)
public abstract class WaterWheelBlockEntityMixin extends GeneratingKineticBlockEntity implements BeegCreateActuator {

    @Shadow
    public abstract void lazyTick();

    public WaterWheelBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {super(type, pos, state);}

    @Unique
    boolean actuallysize$backwards;
    @Unique
    int actuallysize$crankingTicks;

    @Override
    public @NotNull InteractionResult actuallysize$whenBeegUsed(@NotNull ASIWorldBlock where, @NotNull Player who, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        boolean update = false;
        boolean back = who.isShiftKeyDown();

        // Decide if recalculating rotation or just continuing the cranking
        if (actuallysize$crankingTicks == 0 || back != actuallysize$backwards) {update = true;}

        // Update rotation with boost from size
        this.actuallysize$backwards = back;
        if (update && !where.getWorld().isClientSide) {updateGeneratedRotation();}

        // Cranking for 10 ticks
        actuallysize$crankingTicks = 10;
        return InteractionResult.SUCCESS;
    }

    @Inject(method = "lazyTick", at = @At("HEAD"))
    public void OnLazyTickExtension(CallbackInfo ci) {
        if (actuallysize$crankingTicks > 0) { actuallysize$crankingTicks = 10; }
    }

    @Override
    public void tick() {
        super.tick();

        // Eventually, run out of cranking momentum
        if (actuallysize$crankingTicks > 0) {
            actuallysize$crankingTicks--;

            if (actuallysize$crankingTicks == 0) {
                ((BeegCreateKinetics) this).actuallysize$setKineticBeeg(null);
                if (level != null && !level.isClientSide) { updateGeneratedRotation(); }
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void tickAudio() {
        super.tickAudio();
        if (actuallysize$crankingTicks > 0 && AnimationTickHolder.getTicks() % 10 == 0) {
            if (!AllBlocks.HAND_CRANK.has(getBlockState()))
                return;
            AllSoundEvents.CRANKING.playAt(level, worldPosition, (actuallysize$crankingTicks) / 2.5f, .65f + (10 - actuallysize$crankingTicks) / 10f, true);
        }
    }

    @WrapMethod(method = "getGeneratedSpeed")
    private float OnRecalculateSpeed(Operation<Float> original) {
        float originalSpeed = original.call();

        // The speed may be increased by size influence
        BeegCreateKinetics asKinetic = (BeegCreateKinetics) this;
        BeegCreateMechanics asMechanics = asKinetic.actuallysize$getKineticBeeg();
        if (asMechanics != null) { originalSpeed = asMechanics.adjustSpeed(originalSpeed); }

        return originalSpeed;
    }
}
