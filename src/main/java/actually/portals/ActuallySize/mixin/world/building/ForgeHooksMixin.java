package actually.portals.ActuallySize.mixin.world.building;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.world.blocks.furniture.ASIBeegFurnishing;
import actually.portals.ActuallySize.world.grid.ASIBeegBlock;
import actually.portals.ActuallySize.world.grid.ASIWorldBlock;
import actually.portals.ActuallySize.world.grid.events.ASIBeegFurniturePlaceEvent;
import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.BlockSnapshot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = ForgeHooks.class, remap = false)
public class ForgeHooksMixin {

    @Unique private static @Nullable ASIBeegBlock actuallysize$placingGrid;
    @Unique private static boolean actuallysize$consuming;
    @Unique @Nullable private static List<BlockSnapshot> actuallysize$snaps;
    @Unique private static int actuallysize$originalCount;
    @Unique private static int actuallysize$totalConsume;
    @Unique @Nullable private static Player actuallysize$placer;
    @Unique @Nullable private static Level actuallysize$level;

    @Inject(method = "onPlaceItemIntoWorld", at = @At("HEAD"))
    private static void OnPlaceBlockCall(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {

        // Identify
        actuallysize$placingGrid = null;
        actuallysize$furnishingPlacement = null;
        actuallysize$consuming = false;
        actuallysize$snaps = null;
        actuallysize$level = context.getLevel();
        actuallysize$originalCount = context.getItemInHand().getCount();

        actuallysize$totalConsume = -1;
        actuallysize$placer = context.getPlayer();
        if (!(actuallysize$placer instanceof ServerPlayer)) { return; }

        // Approved preconditions
        int scale = OotilityNumbers.ceil(ASIUtilities.getEntityScale(actuallysize$placer));
        if (scale <= 1) { return; }
        if (actuallysize$OnFurniturePlaceCall(context, scale)) { return; }
        if (!ActuallySizeInteractions.WORLD_SYSTEM.canBeBeegBlock(context.getItemInHand())) { return; }

        // Good one
        actuallysize$placingGrid = ASIBeegBlock.containing(scale, context.getClickedPos().getCenter()).withHalved(actuallysize$placer.isShiftKeyDown());
        if (actuallysize$placingGrid.getEffectiveScale() <= 1) { actuallysize$placingGrid = null; }
    }

    @WrapOperation(method = "onPlaceItemIntoWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/InteractionResult;consumesAction()Z", remap = true))
    private static boolean OnPlaceBlockConsuming(InteractionResult instance, Operation<Boolean> original) {
        actuallysize$consuming =  true;
        return original.call(instance);
    }

    @WrapOperation(method = "onPlaceItemIntoWorld", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;clone()Ljava/lang/Object;"))
    private static Object OnPlaceBlockSnapshots(ArrayList<BlockSnapshot> instance, Operation<Object> original) {
        actuallysize$snaps = (List<BlockSnapshot>) original.call(instance);
        return actuallysize$snaps;
    }

    @WrapOperation(method = "onPlaceItemIntoWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCount()I", remap = true))
    private static int OnPlaceBlockCount(ItemStack instance, Operation<Integer> original) {
        int ret = original.call(instance);
        if (!actuallysize$consuming) { return ret; }
        if (actuallysize$placingGrid == null && actuallysize$furnishingPlacement == null) { return ret; }
        actuallysize$consuming = false;

        // When placing down furniture
        if (actuallysize$furnishingPlacement != null) {

            // Multiply consumption cost by multiplier
            int actualConsume = actuallysize$originalCount - ret;
            actualConsume *= actuallysize$furnishingPlacement.getItemCostMultiplier();
            if (actualConsume > actuallysize$originalCount) {

                // Not enough count to consume this, cancel the event
                actuallysize$furnishingPlacement.setCanceled(true);
                actualConsume = actuallysize$originalCount;
            }

            // Calculate consumption and count
            ret = actuallysize$originalCount - actualConsume;
            actuallysize$totalConsume = actualConsume;

        // When placing down blocks
        } else {
            int placingScale = actuallysize$placingGrid.getEffectiveScale();
            int economyScale = actuallysize$placingGrid.getScale();
            double scaledConsumption = economyScale;

            // Adjust to a fraction of placing scale
            if (placingScale != economyScale) {

                // Usually you are placing at a smaller scale than normal
                double ratio = scaledConsumption / placingScale;

                // The most common ratio, resulting in 1/8
                if (ratio == 2) {
                    scaledConsumption = economyScale * 0.125;

                    // I guess we are calculating on-the-go
                } else {
                    ratio = ratio * ratio * ratio;
                    scaledConsumption = economyScale / ratio;
                }
            }

            // Calculate the blocks needed for this VS the blocks we actually have
            int maxConsume = OotilityNumbers.ceil(scaledConsumption);
            int actualConsume = actuallysize$originalCount - ret;
            actualConsume *= maxConsume;
            if (actualConsume > actuallysize$originalCount) { actualConsume = actuallysize$originalCount; }

            // Calculate consumption and count
            ret = actuallysize$originalCount - actualConsume;
            actuallysize$totalConsume = actualConsume;

            /*
             * When placing a smaller block, it should still use
             * your larger grid's consumption to generate the
             * volume, we revert the calculation from the ratio above
             */
            if (placingScale != economyScale) {
                actuallysize$totalConsume = placingScale * (actualConsume / maxConsume);
            }
        }

        return ret;
    }

    @WrapOperation(method = "onPlaceItemIntoWorld", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ForgeEventFactory;onBlockPlace(Lnet/minecraft/world/entity/Entity;Lnet/minecraftforge/common/util/BlockSnapshot;Lnet/minecraft/core/Direction;)Z"))
    private static boolean OnPlaceBlockEvent(Entity entity, BlockSnapshot blockSnapshot, Direction direction, Operation<Boolean> original) {

        // Use furniture
        if (actuallysize$furnishingPlacement != null) {

            // When cancelled, we cancel the placing of block
            if (actuallysize$furnishingPlacement.isCanceled()) { return true; }

            // Restore previous snapshots
            Level level = actuallysize$level;
            List<BlockSnapshot> snapshots = actuallysize$snapshots;
            if (snapshots != null) {
                if (level != null) {
                    for (BlockSnapshot blocksnapshot : Lists.reverse(snapshots)) {
                        level.restoringBlockSnapshots = true;
                        blocksnapshot.restore(true, false);
                        level.restoringBlockSnapshots = false; } }
                snapshots.clear();
            }

            // Run event and success
            actuallysize$furnishingPlacement.getFurnishing().place(actuallysize$furnishingPlacement);
            return false;

        // On normal operation
        } else if (actuallysize$placingGrid == null) {
            return original.call(entity, blockSnapshot, direction);

        // Turn to Beeg Block Event
        } else {
            ServerLevel level = (ServerLevel) actuallysize$placer.level();

            // Undo the provided snapshot
            actuallysize$snaps.clear();

            /*
             * Identify the number of items consumed when placing blocks
             * to a limit reasonable enough for creative mode and a full
             * stack which is the maximum you can normally place down ya
             */

            int cons = actuallysize$totalConsume;
            if (cons >= 64) { cons = -1; }
            if (actuallysize$placer.getAbilities().instabuild) { cons = 32767; }

            // Find beeg block where it is placed
            ASIBeegBlock beegBlock = ASIBeegBlock.containing(actuallysize$placingGrid.getScale(), blockSnapshot.getPos().getCenter()).withHalved(actuallysize$placingGrid.isHalved());
            return !beegBlock.tryBeegBuild(actuallysize$snaps, blockSnapshot, blockSnapshot.getCurrentBlock(), direction, actuallysize$placer, level, cons);
        }
    }
    @Unique private static @Nullable List<BlockSnapshot> actuallysize$snapshots;

    @WrapOperation(method = "onPlaceItemIntoWorld", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;clone()Ljava/lang/Object;"))
    private static Object OnSnapshotsClone(ArrayList instance, Operation<Object> original) {

        actuallysize$snapshots = (List<BlockSnapshot>) original.call(instance);
        return actuallysize$snapshots;
    }

    @WrapOperation(method = "onPlaceItemIntoWorld", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ForgeEventFactory;onMultiBlockPlace(Lnet/minecraft/world/entity/Entity;Ljava/util/List;Lnet/minecraft/core/Direction;)Z"))
    private static boolean OnPlaceBlockEvent(@Nullable Entity entity, List<BlockSnapshot> blockSnapshots, Direction direction, Operation<Boolean> original) {

        // No furnishing? Not ASI business
        if (actuallysize$furnishingPlacement == null) { return original.call(entity, blockSnapshots, direction); }

        // When cancelled, we cancel the placing of block
        if (actuallysize$furnishingPlacement.isCanceled()) { return true; }

        // Restore previous snapshots
        Level level = actuallysize$level;
        if (level != null) {
            for (BlockSnapshot blocksnapshot : Lists.reverse(blockSnapshots)) {
                level.restoringBlockSnapshots = true;
                blocksnapshot.restore(true, false);
                level.restoringBlockSnapshots = false; } }
        blockSnapshots.clear();

        // Run event and succeed
        actuallysize$furnishingPlacement.getFurnishing().place(actuallysize$furnishingPlacement);
        return false;
    }

    @Inject(method = "onPlaceItemIntoWorld", at = @At("RETURN"))
    private static void OnPlaceBlockEnd(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {

        // Reset
        actuallysize$furnishingPlacement = null;
        actuallysize$placingGrid = null;
        actuallysize$consuming = false;
        actuallysize$snaps = null;
        actuallysize$originalCount = -1;
        actuallysize$totalConsume = -1;
        actuallysize$placer = null;
        actuallysize$level = null;
        actuallysize$snapshots = null;
    }

    @Unique private static @Nullable ASIBeegFurniturePlaceEvent actuallysize$furnishingPlacement;

    @Unique
    private static boolean actuallysize$OnFurniturePlaceCall(@NotNull UseOnContext context, int scale) {
        if (!(actuallysize$placer instanceof ServerPlayer)) { return false; }

        ItemStack placing = context.getItemInHand();
        ASIBeegFurnishing furnishing = ActuallySizeInteractions.WORLD_SYSTEM.isBeegFurniture(placing.getItem());
        if (furnishing == null) { return false; }

        // Prepare this furniture placement.
        actuallysize$furnishingPlacement = furnishing.preparePlace((ServerPlayer) actuallysize$placer,
                new ASIWorldBlock(BlockPos.containing(context.getClickedPos().getCenter().relative(context.getClickedFace(), 1)), context.getLevel()), context.getClickedFace());
        if (actuallysize$furnishingPlacement == null) { return false; }

        /*
         * If the event is cancelled, the placement of this item is cancelled
         * at the end of this mixin shenanigans.
         *
         * However, specifically for THIS method, we have the option to enable
         * the code below and cancel the Beeg Furniture system instead, still
         * allowing the block to be placed without engaging Beeg Furniture.
         */
        // if (actuallysize$furnishingPlacement.isCanceled()) { actuallysize$furnishingPlacement = null; return false; }
        return true;
    }
}
