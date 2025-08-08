package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level implements WorldGenLevel {

    protected ServerLevelMixin(WritableLevelData pLevelData, ResourceKey<Level> pDimension, RegistryAccess pRegistryAccess, Holder<DimensionType> pDimensionTypeRegistration, Supplier<ProfilerFiller> pProfiler, boolean pIsClientSide, boolean pIsDebug, long pBiomeZoomSeed, int pMaxChainedNeighborUpdates) {
        super(pLevelData, pDimension, pRegistryAccess, pDimensionTypeRegistration, pProfiler, pIsClientSide, pIsDebug, pBiomeZoomSeed, pMaxChainedNeighborUpdates);
    }

    @Shadow protected abstract void tickPassenger(Entity pRidingEntity, Entity pPassengerEntity);

    @Inject(method = "lambda$tick$6", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getVehicle()Lnet/minecraft/world/entity/Entity;"), cancellable = true)
    public void onEntityTick(ProfilerFiller profilerfiller, Entity p_184065_, CallbackInfo ci) {

        /*
         *  This is called when the entity is about to be ticked,
         *  before it checks if it is a passenger (passengers are
         *  ticked by their vehicle when the vehicle entity ticks).
         *
         *  Held entities are similar to passengers in that they are
         *  ticked by the holder when the holder ticks, so we check
         *  if this has a holder and if so cancel this entity tick.
         */
        EntityDualityCounterpart entityCounterpart = ((EntityDualityCounterpart) p_184065_);
        if (entityCounterpart.actuallysize$isHeld()) { ci.cancel(); }
    }

    @Inject(method = "tickNonPassenger", at = @At(value = "RETURN"))
    public void onTickNonPassenger(Entity p_8648_, CallbackInfo ci) {

        /*
         *  Currently the holder is being ticked, that means it must
         *  tick its held entities. Do so at the end of this method
         */
        ItemEntityDualityHolder holder = (ItemEntityDualityHolder) p_8648_;
        ArrayList<EntityDualityCounterpart> held = new ArrayList<>(holder.actuallysize$getHeldEntityDualities().values());
        for (EntityDualityCounterpart entityCounterpart : held) {

            // Tick those that are actually held (not just active)
            if (entityCounterpart.actuallysize$isHeld()) {
                tickPassenger(p_8648_, (Entity) entityCounterpart);
            }
        }
    }

    @Inject(method = "tickPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getPassengers()Ljava/util/List;"))
    public void onHeldTickHeld(Entity pRidingEntity, Entity pPassengerEntity, CallbackInfo ci) {

        // Pop the profiler call that was intercepted
        ProfilerFiller profilerfiller = this.getProfiler();
        profilerfiller.pop();

        /*
         *  Currently the holder is being ticked, that means it must
         *  tick its held entities. Do so at the end of this method
         */
        ItemEntityDualityHolder holder = (ItemEntityDualityHolder) pPassengerEntity;
        ArrayList<EntityDualityCounterpart> held = new ArrayList<>(holder.actuallysize$getHeldEntityDualities().values());
        for (EntityDualityCounterpart entityCounterpart : held) {

            // Tick those that are actually held (not just active)
            if (entityCounterpart.actuallysize$isHeld()) {
                tickPassenger(pPassengerEntity, (Entity) entityCounterpart);
            }
        }

        // Push a dummy profiler fill to be immediately popped after injection
        profilerfiller.push("ASI-DUMMY");
    }

    @Unique Entity actuallysize$latestRidingEntity;
    @Unique Entity actuallysize$latestPassengerEntity;

    @Inject(method = "tickPassenger", at = @At("HEAD"))
    public void onTickPassenger(Entity pRidingEntity, Entity pPassengerEntity, CallbackInfo ci) {
        actuallysize$latestRidingEntity = pRidingEntity;
        actuallysize$latestPassengerEntity = pPassengerEntity;
    }

    @Definition(id = "getVehicle", method = "Lnet/minecraft/world/entity/Entity;getVehicle()Lnet/minecraft/world/entity/Entity;")
    @Expression("?.getVehicle() == ?")
    @WrapOperation(method = "tickPassenger", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    private boolean onVerifyVehicle(Object left, Object right, Operation<Boolean> original) {

        // Run original comparison
        boolean normal = original.call(left, right);
        if (normal) { return true; }

        // For this method, I only support entities being the vehicle
        if (!(right instanceof Entity)) { return false; }

        // For it to be ticked through this method, it must be HELD, not only active
        EntityDualityCounterpart entityCounterpart = (EntityDualityCounterpart) actuallysize$latestPassengerEntity;
        if (!entityCounterpart.actuallysize$isHeld()) {
            return false; }

        // Identify
        Entity vehicle = (Entity) right;

        // Try comparing by holder
        if (actuallysize$latestRidingEntity == vehicle) {

            // Check the holder of the passenger
            Entity holder = (Entity) entityCounterpart.actuallysize$getItemEntityHolder();

            // It must be the same as the vehicle
            return holder == vehicle;
        }

        // Bad
        return false;
    }

    @Inject(method = "tickPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;stopRiding()V"))
    public void onPassengerTickEject(Entity pRidingEntity, Entity pPassengerEntity, CallbackInfo ci) {
        EntityDualityCounterpart entityCounterpart = (EntityDualityCounterpart) pPassengerEntity;
        entityCounterpart.actuallysize$escapeDuality();
    }
}
