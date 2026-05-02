package actually.portals.ActuallySize.mixin.compat1201plus.forge0470414minus;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.pickup.mixininterfaces.*;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.commands.CommandSource;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidType;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityFluidPushMixin extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity> implements Nameable, EntityAccess, CommandSource, net.minecraftforge.common.extensions.IForgeEntity, ItemEntityDualityHolder, EntityDualityCounterpart, SetLevelExt, RenderNormalizable, HoldTickable, ModelPartHoldable {

    @Shadow public abstract float getBbHeight();

    @Shadow private boolean onGround;

    @Shadow public abstract Vec3 getDeltaMovement();

    @Unique @Nullable MutableTriple<Double, Vec3, Integer> actuallysize$interimFlow;

    protected EntityFluidPushMixin(Class<net.minecraft.world.entity.Entity> baseClass) { super(baseClass); }

    @Inject(method = "lambda$updateFluidHeightAndDoFluidPushing$29", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    public void whenPushedByFluid(FluidType fluidType, MutableTriple<Double, Vec3, Integer> interim, CallbackInfo ci) {
        actuallysize$interimFlow = interim;

        // If smol, we pretend  you are fully submerged in the liquid
        double mySize = ASIUtilities.getEntityScale((net.minecraft.world.entity.Entity) (Object) this);
        if (mySize < 1) {
            //double inverseAmplification = 1 / mySize;
            double submerged = interim.getLeft();
            if (submerged > getBbHeight()) { submerged = getBbHeight(); }
            //FLW//ActuallySizeInteractions.Log("ASI &6 EMX-FLW &7 Flow submerged from " + interim.getLeft() + " to &6 " + submerged + " &r , Height = &e " + getBbHeight());

            Vec3 multiflow = interim.getMiddle().normalize().scale(submerged);
            double buff = ASIUtilities.beegBalanceResist(mySize, 3, 0);
            if (((Object) this) instanceof LivingEntity) {
                double depth = EnchantmentHelper.getDepthStrider((LivingEntity) (Object) this);
                if (depth > 0) {
                    if (!onGround) { depth = depth * 0.5; }
                    if (depth > 3) { depth = 3; }
                    double nerf = (depth * 0.3 + 0.075) * buff;
                    buff -= nerf;
                }
            }
            //FLW//ActuallySizeInteractions.Log("ASI &6 EMX-FLW &7 Flow base from &3 " + interim.getMiddle().length() + " &r to &b " + multiflow.length() + " &f, buff &9 x" + buff);

            /*
             * Nerf acceleration by interpolating between current and max speed
             */
            double moment = getDeltaMovement().length();
            double buffed = multiflow.scale(buff).length();
            Vec3 multinormal = multiflow.normalize();

            interim.setMiddle(multinormal.scale((0.6 * moment) + (0.2 * buffed)));
        }
    }


    @WrapOperation(method = "lambda$updateFluidHeightAndDoFluidPushing$29", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"))
    public Vec3 whenPushedByFluid(Vec3 instance, Vec3 pVec, Operation<Vec3> original) {
        Vec3 cookedFlow = pVec;
        if (actuallysize$interimFlow != null) { cookedFlow = actuallysize$interimFlow.getMiddle(); }
        return original.call(instance, cookedFlow);
    }
}
