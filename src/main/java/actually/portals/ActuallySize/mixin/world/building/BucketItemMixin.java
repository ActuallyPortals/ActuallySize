package actually.portals.ActuallySize.mixin.world.building;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallyServerConfig;
import actually.portals.ActuallySize.world.grid.ASIBeegBlock;
import actually.portals.ActuallySize.world.grid.ASIWorldBlock;
import actually.portals.ActuallySize.world.mixininterfaces.BeegBreaker;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BucketItem.class)
public class BucketItemMixin {

    @Shadow @Final private Fluid content;
    @Unique HitResult actuallysize$latestRaycast;

    @WrapMethod(method = "use")
    public InteractionResultHolder<ItemStack> OnBucketUsed(Level pLevel, Player pPlayer, InteractionHand pHand, Operation<InteractionResultHolder<ItemStack>> original) {

        // When the bucket is empty, this is a DRAIN operation
        boolean draining = (this.content == Fluids.EMPTY);
        actuallysize$latestRaycast = null;

        // Run your usual bucket use code
        ItemStack originalBucket = pPlayer.getItemInHand(pHand);
        Fluid originalFluid = (new FluidBucketWrapper(originalBucket)).getFluid().getFluid();
        InteractionResultHolder<ItemStack> ret = original.call(pLevel, pPlayer, pHand);
        if (!ActuallyServerConfig.beegBuilding) { return ret; }

        // If it failed, we cant use it
        if (ret.getResult() == InteractionResult.FAIL) { return ret; }
        if (ret.getResult() == InteractionResult.PASS) { return ret; }
        if (!(actuallysize$latestRaycast instanceof BlockHitResult)) { return ret; }
        if (actuallysize$latestRaycast.getType() != HitResult.Type.BLOCK) { return ret; }
        BlockHitResult blockHitResult = (BlockHitResult) actuallysize$latestRaycast;
        BlockPos blockpos = blockHitResult.getBlockPos();

        // Must be used by a beeg player in the serverside
        if (!(pPlayer instanceof ServerPlayer)) { return ret; }
        ServerPlayer beeg = (ServerPlayer) pPlayer;
        BeegBreaker breaker = (BeegBreaker) beeg;
        if (breaker.actuallysize$isBeegBreaking()) { return ret; }
        if (!(pLevel instanceof ServerLevel)) { return ret; }
        ServerLevel world = (ServerLevel) pLevel;

        // Adjust scale of breaking
        double scale = ASIUtilities.getEntityScale(beeg);
        if (beeg.isShiftKeyDown()) { scale = OotilityNumbers.floor(scale * 0.5); }
        if (scale <= 1) { return ret; }

        // Check for valid hit result
        ASIBeegBlock beegBlock = ASIBeegBlock.containing(scale, blockpos.getCenter());
        ASIWorldBlock block = new ASIWorldBlock(world.getBlockState(blockpos), blockpos, world);

        // When fluid was picked up
        if (draining) {

            // Identify the fluid that was harvested
            ItemStack filledBucket = ret.getObject();
            Fluid obtained = (new FluidBucketWrapper(filledBucket)).getFluid().getFluid();

            // No change in fluid? No ASI trigger, unless creative mode
            if (obtained.isSame(this.content) && !beeg.getAbilities().instabuild) { return ret; }

            // No fluid? Also requires creative mode
            if (obtained == Fluids.EMPTY && !beeg.getAbilities().instabuild) { return ret; }

            // Simulate mass-draining by this player
            try {
                breaker.actuallysize$setBeegBreaking(true);
                beegBlock.tryBeegDrain(block, beeg, world, obtained, originalBucket, filledBucket);

            } finally { breaker.actuallysize$setBeegBreaking(false); }

        // Not draining, filling!
        } else {

            // Identify the fluid that was harvested
            ItemStack emptyBucket = ret.getObject();
            Fluid obtained = (new FluidBucketWrapper(emptyBucket)).getFluid().getFluid();

            // No change in fluid? No ASI trigger, unless creative mode
            if (obtained.isSame(originalFluid) && !beeg.getAbilities().instabuild) { return ret; }

            // Simulate mass-fill by this player
            try {
                breaker.actuallysize$setBeegBreaking(true);
                beegBlock.tryBeegFill(block, beeg, world, originalFluid, emptyBucket, originalBucket);

            } finally { breaker.actuallysize$setBeegBreaking(false); }
        }

        return ret;
    }

    @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ForgeEventFactory;onBucketUse(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/HitResult;)Lnet/minecraft/world/InteractionResultHolder;", remap = false))
    @Nullable InteractionResultHolder<ItemStack> onHitResult(@NotNull Player player, @NotNull Level level, @NotNull ItemStack stack, @Nullable HitResult target, Operation<InteractionResultHolder<ItemStack>> original) {
        actuallysize$latestRaycast = target;
        return original.call(player, level, stack, target);
    }
}
