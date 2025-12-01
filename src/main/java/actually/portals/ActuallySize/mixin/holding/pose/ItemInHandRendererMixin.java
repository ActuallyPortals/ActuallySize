package actually.portals.ActuallySize.mixin.holding.pose;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

    @Unique
    int actuallysize$lastUseDuration;

    @Inject(method = "applyEatTransform", at = @At("HEAD"))
    public void eatAnimationAdjust0(PoseStack pPoseStack, float pPartialTicks, HumanoidArm pHand, ItemStack pStack, CallbackInfo ci) {
        actuallysize$lastUseDuration = pStack.getUseDuration();
    }

    @Definition(id = "f", local = @Local(type = float.class, ordinal = 1))
    @Definition(id = "getUseDuration", method = "Lnet/minecraft/world/item/ItemStack;getUseDuration()I")
    @Definition(id = "pStack", local = @Local(type = ItemStack.class, argsOnly = true))
    @Expression("f / (float) pStack.getUseDuration()")
    @ModifyExpressionValue(method = "applyEatTransform", at = @At("MIXINEXTRAS:EXPRESSION"))
    public float eatAnimationAdjust1(float original) {

        /*
         * Usually making the first 20% of the animation be this
         * transient period is good, but that expects about 32
         * ticks total for a transient period of about 8 ticks.
         *
         * ASI Makes tinies take way longer to eat some foods, then
         * I want to cap this transient period at 8 ticks. Then,
         * I will make f1 elapse 20% or 8 ticks, whichever is longer,
         * and the rest 80% of f1 will span the rest of the animation
         */

        // Reconstruct the full animation
        float full = actuallysize$lastUseDuration;
        float twenty = full * 0.2F;
        if (twenty > 8) {
            float remainingTime = original * full;
            float elapsedTime = full - remainingTime;

            if (elapsedTime > 8) {
                float span = full - 8;
                elapsedTime -= 8;

                // Every subsequent tick is a fraction of the elapsed time
                return 0.8F * (1 - (elapsedTime / span));

            } else {

                // Every elapsed tick counts for 1/8th of the 20%, getting it down to 80% at the eight tick
                return 1F - 0.199F * (elapsedTime / 8F);
            }
        }

        return original;
    }
}
