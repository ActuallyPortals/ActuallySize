package actually.portals.ActuallySize.mixin.world.building;

import actually.portals.ActuallySize.world.mixininterfaces.Directed;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ForgeEventFactory.class, remap = false)
public class ForgeEventFactoryMixin {

    @Unique
    @Nullable
    private static Direction actuallysize$dir;

    @Inject(method = "onBlockPlace", at = @At(value = "HEAD"))
    private static void OnPlaceBlockCall(Entity entity, BlockSnapshot blockSnapshot, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        actuallysize$dir = direction;
    }

    @WrapOperation(method = "onBlockPlace", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/eventbus/api/IEventBus;post(Lnet/minecraftforge/eventbus/api/Event;)Z"))
    private static boolean OnPlaceBlock(IEventBus instance, Event event, Operation<Boolean> original) {
        if (event instanceof Directed) { ((Directed) event).actuallysize$setDirection(actuallysize$dir);}
        return original.call(instance, event);
    }

    @Inject(method = "onBlockPlace", at = @At(value = "RETURN"))
    private static void OnPlaceBlockEnd(Entity entity, BlockSnapshot blockSnapshot, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        actuallysize$dir = null;
    }
}
