package actually.portals.ActuallySize.mixin.compat1201plus.forge0470402plus;


import actually.portals.ActuallySize.ASIUtilities;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public abstract class LivingEntityReachMixin extends Entity implements Attackable, net.minecraftforge.common.extensions.IForgeLivingEntity {

    public LivingEntityReachMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @WrapMethod(method = "getAttributeValue(Lnet/minecraft/world/entity/ai/attributes/Attribute;)D")
    public double onReachAttribute(Attribute pAttribute, Operation<Double> original) {
        double ret = original.call(pAttribute);

        // Reach attributes are increased
        if (pAttribute == ForgeMod.BLOCK_REACH.get() ||
                pAttribute == ForgeMod.ENTITY_REACH.get()) {

            double size = ASIUtilities.getEffectiveSize(this);
            if (size < 0.65) { size = 0.65; }
            ret *= size;
        }

        return ret;
    }
}
