package actually.portals.ActuallySize.mixin.world.building;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.world.ASIWorldSystemManager;
import actually.portals.ActuallySize.world.grid.ASIBeegBlock;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.BlockSnapshot;
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

    @Unique private static double actuallysize$placingScale;
    @Unique private static boolean actuallysize$consuming;
    @Unique @Nullable private static List<BlockSnapshot> actuallysize$snaps;
    @Unique private static int actuallysize$originalCount;
    @Unique private static int actuallysize$totalConsume;
    @Unique @Nullable private static Player actuallysize$placer;

    @Inject(method = "onPlaceItemIntoWorld", at = @At("HEAD"))
    private static void OnPlaceBlockCall(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {

        // Identify
        actuallysize$placingScale = -1;
        actuallysize$consuming = false;
        actuallysize$snaps = null;
        actuallysize$originalCount = context.getItemInHand().getCount();
        actuallysize$totalConsume = -1;
        actuallysize$placer = context.getPlayer();
        if (!(actuallysize$placer instanceof ServerPlayer)) { return; }

        // Approved preconditions
        double scale = ASIUtilities.getEntityScale(actuallysize$placer);
        if (scale <= 1) { return; }
        if (!ASIWorldSystemManager.CanBeBeegBlock(context.getItemInHand())) { return; }

        // Good one
        actuallysize$placingScale = scale;
    }

    @WrapOperation(method = "onPlaceItemIntoWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/InteractionResult;consumesAction()Z"))
    private static boolean OnPlaceBlockConsuming(InteractionResult instance, Operation<Boolean> original) {
        actuallysize$consuming =  true;
        return original.call(instance);
    }

    @WrapOperation(method = "onPlaceItemIntoWorld", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;clone()Ljava/lang/Object;"))
    private static Object OnPlaceBlockSnapshots(ArrayList<BlockSnapshot> instance, Operation<Object> original) {
        actuallysize$snaps = (List<BlockSnapshot>) original.call(instance);
        return actuallysize$snaps;
    }

    @WrapOperation(method = "onPlaceItemIntoWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCount()I"))
    private static int OnPlaceBlockCount(ItemStack instance, Operation<Integer> original) {
        int ret = original.call(instance);
        if (actuallysize$consuming) {
            actuallysize$consuming = false;

            // Multiply use consumption by scale
            if (actuallysize$placingScale > 1)  {
                int diff = actuallysize$originalCount - ret;
                diff *= OotilityNumbers.ceil(actuallysize$placingScale);
                if (diff > actuallysize$originalCount) { diff = actuallysize$originalCount; }
                ret = actuallysize$originalCount - diff;
                actuallysize$totalConsume = diff;
            }
        }

        return ret;
    }

    @WrapOperation(method = "onPlaceItemIntoWorld", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ForgeEventFactory;onBlockPlace(Lnet/minecraft/world/entity/Entity;Lnet/minecraftforge/common/util/BlockSnapshot;Lnet/minecraft/core/Direction;)Z"))
    private static boolean OnPlaceBlockConsuming(Entity entity, BlockSnapshot blockSnapshot, Direction direction, Operation<Boolean> original) {

        // Normal operation
        if (actuallysize$placingScale < 1) {
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
            ASIBeegBlock beegBlock = ASIBeegBlock.containing(actuallysize$placingScale, blockSnapshot.getPos().getCenter());
            return !beegBlock.tryPlace(actuallysize$snaps, blockSnapshot, blockSnapshot.getCurrentBlock(), direction, actuallysize$placer, level, cons);
        }
    }

    @Inject(method = "onPlaceItemIntoWorld", at = @At("RETURN"))
    private static void OnPlaceBlockEnd(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {

        // Reset
        actuallysize$placingScale = -1;
        actuallysize$consuming = false;
        actuallysize$snaps = null;
        actuallysize$originalCount = -1;
        actuallysize$totalConsume = -1;
        actuallysize$placer = null;
    }
}
