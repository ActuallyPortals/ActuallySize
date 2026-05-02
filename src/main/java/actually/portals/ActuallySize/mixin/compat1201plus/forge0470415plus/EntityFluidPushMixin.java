package actually.portals.ActuallySize.mixin.compat1201plus.forge0470415plus;

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
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityFluidPushMixin extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity> implements Nameable, EntityAccess, CommandSource, net.minecraftforge.common.extensions.IForgeEntity, ItemEntityDualityHolder, EntityDualityCounterpart, SetLevelExt, RenderNormalizable, HoldTickable, ModelPartHoldable {

    @Shadow
    public abstract float getBbHeight();

    @Shadow private boolean onGround;

    @Shadow public abstract Vec3 getDeltaMovement();

    @Shadow public abstract void setDeltaMovement(Vec3 pDeltaMovement);

    @Unique @Nullable Vec3 actuallysize$interimFlow;
    @Unique @Nullable double actuallysize$mySize;
    protected EntityFluidPushMixin(Class<net.minecraft.world.entity.Entity> baseClass) { super(baseClass); }

    @Inject(method = "lambda$updateFluidHeightAndDoFluidPushing$29", at = @At(value = "HEAD"), remap = false)
    public void whenPushedByFluid1(FluidType fluidType, @Coerce Object interim, CallbackInfo ci) {

        /*
         * In order to make water drag tinies with more intensity,
         * I must scale the drag vector resulting from water flow
         * based on how tiny they are. This is further complicated
         * by the fact that the more submerged in water you are the
         * stronger it pushes you.
         *
         * Unfortunately, the Entity.FluidCalcs class is private so
         * the amount submerged in fluid is unavailable to us. Then,
         * I cancel the original amount of pushing it gives and intercept
         * the height submerged when it is used later, and then I apply
         * the modified drag calculation.
         *
         * Reset this calculation
         */
        actuallysize$interimFlow = null;
        actuallysize$mySize = 1;
    }

    @WrapOperation(method = "lambda$updateFluidHeightAndDoFluidPushing$29", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"))
    public Vec3 whenPushedByFluid2(Vec3 instance, Vec3 pVec, Operation<Vec3> original) {

        // If smol, we pretend  you are fully submerged in the liquid
        actuallysize$mySize = ASIUtilities.getEntityScale((net.minecraft.world.entity.Entity) (Object) this);
        if (actuallysize$mySize < 1) {

            // Modify water drag
            actuallysize$interimFlow = pVec;
            return original.call(instance, Vec3.ZERO);  // For now, no change to the actual movement delta

        // No change
        } else {

            return original.call(instance, pVec.scale(ASIUtilities.beegBalanceResist(actuallysize$mySize, 1, 0)));
        }
    }

    @WrapOperation(method = "lambda$updateFluidHeightAndDoFluidPushing$29", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setFluidTypeHeight(Lnet/minecraftforge/fluids/FluidType;D)V"), remap = false)
    public void whenPushedByFluid3(Entity instance, FluidType type, double height, Operation<Void> original) {

        // Apply movement drag changes if applicable, now that we can intercept HEIGHT
        if (actuallysize$interimFlow != null) {


            //double inverseAmplification = 1 / mySize;
            double submerged = height;
            if (submerged > getBbHeight()) { submerged = getBbHeight(); }
            //FLW//ActuallySizeInteractions.Log("ASI &6 EMX-FLW &7 Flow submerged from " + interim.getLeft() + " to &6 " + submerged + " &r , Height = &e " + getBbHeight());

            Vec3 multiflow = actuallysize$interimFlow.normalize().scale(submerged);
            double buff = ASIUtilities.beegBalanceResist(actuallysize$mySize, 3, 0);
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

            // Actually apply
            Vec3 cookedFlow  = multinormal.scale((0.6 * moment) + (0.2 * buffed));
            setDeltaMovement(getDeltaMovement().add(cookedFlow));
        }

        // Proceed as normal
        original.call(instance, type, height);
    }
}
