package actually.portals.ActuallySize.mixin.holding.pose;

import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import actually.portals.ActuallySize.pickup.holding.pose.ASIPSTinyPosedHold;
import actually.portals.ActuallySize.pickup.holding.pose.smol.ASIPSTinyPoseProfile;
import actually.portals.ActuallySize.pickup.holding.pose.smol.ASIPSVanillaPoseProfile;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.VASIPoseStack;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {

    protected LivingEntityRendererMixin(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @WrapMethod(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V")
    public void onRender(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, Operation<Void> original) {

        // Identify this pose stack
        VASIPoseStack asDeque = (VASIPoseStack) pPoseStack;
        boolean fresh = !asDeque.actuallysize$isMirroring();

        // Only the root holder begins mirroring
        if (fresh) {
            asDeque.actuallysize$enableMirrorDeque();
            asDeque.actuallysize$resetMirrorDeque(); }

        // Decide if this is a player with a pose of sitting
        actuallysize$shouldSitLatest = false;
        if (pEntity instanceof Player) {
            EntityDualityCounterpart entityDuality = (EntityDualityCounterpart) pEntity;
            ASIPSHoldPoint holdPoint = entityDuality.actuallysize$getHoldPoint();
            if (entityDuality.actuallysize$isHeld() && holdPoint instanceof ASIPSTinyPosedHold) {

                // Does it have a pose profile?
                ASIPSTinyPoseProfile profile = ((ASIPSTinyPosedHold) holdPoint).getTinyPose((Player) pEntity);
                if (profile instanceof ASIPSVanillaPoseProfile) {

                    // Is the effective pose SITTING?
                    Pose pose = ((ASIPSVanillaPoseProfile) profile).getEffectivePose((Player) pEntity);
                    if (pose == Pose.SITTING) { actuallysize$shouldSitLatest = true; }
                }
            }
        }

        // Call original
        original.call(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
        actuallysize$shouldSitLatest = false;

        // Only the root holder stops mirroring
        if (fresh) { asDeque.actuallysize$stopMirroring(); }
    }

    @Unique
    boolean actuallysize$shouldSitLatest = false;

    @Definition(id = "shouldSit", local = @Local(type = boolean.class))
    @Expression("shouldSit")
    @ModifyExpressionValue(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("MIXINEXTRAS:EXPRESSION"))
    public boolean onShouldSit(boolean original) {

        // Overridden by should sit latest
        if (actuallysize$shouldSitLatest) { return true; }
        return original;
    }
}
