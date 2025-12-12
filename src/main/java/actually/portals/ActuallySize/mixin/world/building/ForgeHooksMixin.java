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

    @Unique private static int actuallysize$placingScale;
    @Unique private static int actuallysize$consumptionScale;
    @Unique private static boolean actuallysize$consuming;
    @Unique @Nullable private static List<BlockSnapshot> actuallysize$snaps;
    @Unique private static int actuallysize$originalCount;
    @Unique private static int actuallysize$totalConsume;
    @Unique @Nullable private static Player actuallysize$placer;

    @Inject(method = "onPlaceItemIntoWorld", at = @At("HEAD"))
    private static void OnPlaceBlockCall(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {

        // Identify
        actuallysize$placingScale = -1;
        actuallysize$consumptionScale = -1;
        actuallysize$consuming = false;
        actuallysize$snaps = null;
        actuallysize$originalCount = context.getItemInHand().getCount();
        actuallysize$totalConsume = -1;
        actuallysize$placer = context.getPlayer();
        if (!(actuallysize$placer instanceof ServerPlayer)) { return; }

        // Approved preconditions
        int scale = OotilityNumbers.ceil(ASIUtilities.getEntityScale(actuallysize$placer));
        if (scale <= 1) { return; }
        if (!ASIWorldSystemManager.CanBeBeegBlock(context.getItemInHand())) { return; }

        // Good one
        actuallysize$placingScale = scale;
        actuallysize$consumptionScale = OotilityNumbers.ceil(scale);

        // Lower grid when crouching
        if (actuallysize$placer.isShiftKeyDown()) { actuallysize$placingScale = OotilityNumbers.floor(scale * 0.5); }
        if (actuallysize$placingScale <= 1) { actuallysize$placingScale = -1; }
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
        if (actuallysize$consuming) {
            actuallysize$consuming = false;

            // Multiply use consumption by scale
            if (actuallysize$consumptionScale > 1)  {
                double scaledConsumption = actuallysize$consumptionScale;

                // Adjust to a fraction of placing scale
                if (actuallysize$placingScale != actuallysize$consumptionScale) {

                    // Usually you are placing at a smaller scale than normal
                    double ratio = scaledConsumption / actuallysize$placingScale;

                    // The most common ratio, resulting in 1/8
                    if (ratio == 2) {
                        scaledConsumption = actuallysize$consumptionScale * 0.125;

                    // I guess we are calculating on-the-go
                    } else {
                        ratio = ratio * ratio * ratio;
                        scaledConsumption = actuallysize$consumptionScale / ratio;
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
                if (actuallysize$placingScale != actuallysize$consumptionScale) {
                    actuallysize$totalConsume = actuallysize$placingScale * (actualConsume / maxConsume);
                }
            }
        }

        return ret;
    }

    @WrapOperation(method = "onPlaceItemIntoWorld", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ForgeEventFactory;onBlockPlace(Lnet/minecraft/world/entity/Entity;Lnet/minecraftforge/common/util/BlockSnapshot;Lnet/minecraft/core/Direction;)Z"))
    private static boolean OnPlaceBlockEvent(Entity entity, BlockSnapshot blockSnapshot, Direction direction, Operation<Boolean> original) {

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
            return !beegBlock.tryBeegBuild(actuallysize$snaps, blockSnapshot, blockSnapshot.getCurrentBlock(), direction, actuallysize$placer, level, cons);
        }
    }

    @Inject(method = "onPlaceItemIntoWorld", at = @At("RETURN"))
    private static void OnPlaceBlockEnd(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {

        // Reset
        actuallysize$placingScale = -1;
        actuallysize$consumptionScale = -1;
        actuallysize$consuming = false;
        actuallysize$snaps = null;
        actuallysize$originalCount = -1;
        actuallysize$totalConsume = -1;
        actuallysize$placer = null;
    }
}
