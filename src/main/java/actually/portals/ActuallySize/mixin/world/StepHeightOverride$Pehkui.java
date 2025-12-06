package actually.portals.ActuallySize.mixin.world;


import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.extensions.IForgeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import virtuoel.pehkui.mixin.PehkuiMixinConfigPlugin;
import virtuoel.pehkui.util.ScaleUtils;

/**
 * Just enough priority to be applied after {@link virtuoel.pehkui.mixin.step_height.IForgeEntityMixin}
 */
@Mixin(value = IForgeEntity.class, priority = 1020, remap = false)
public interface StepHeightOverride$Pehkui {

    /**
     * @author Actually Portals
     *
     * @reason Step height addition attribute must scale with
     *         player size, which Pehkui already does but is
     *         applying this twice for a Size-Squared super
     *         annoying result. I Overwrite it to apply this
     *         only once
     */
    @Overwrite
    default float getStepHeight() {
        Entity living = (Entity) this;

        float vanillaStep = living.maxUpStep();
        float additions = 0;
        if (living instanceof LivingEntity) {

            // Pehkui step scale
            double scale = ScaleUtils.getStepHeightScale(living);

            // Attribute contribution
            AttributeInstance attribute = ((LivingEntity) living).getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get());
            if (attribute != null) { additions = (float) (attribute.getValue() * scale); }

            // Whatever this is for, I copied it over from Pehkui
            if (PehkuiMixinConfigPlugin.APOTHIC_ATTRIBUTES_LOADED && living instanceof Player) {
                return additions;
            }
        }

        // Add base with addition
        return Math.max(0, vanillaStep + additions);
    }
}
