package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.pickup.mixininterfaces.VASIPoseStack;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

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

        // Call original
        original.call(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);

        // Only the root holder stops mirroring
        if (fresh) { asDeque.actuallysize$stopMirroring(); }
    }
}
