package actually.portals.ActuallySize.mixin.holding.pose;

import net.minecraft.client.renderer.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

    /*
    @Inject(method = "renderNameTag", at = @At("HEAD"), cancellable = true)
    public <T extends Entity> void onRenderEntity(T pEntity, Component pDisplayName, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, CallbackInfo ci) {

        // Don't render nametags of entities that are held except during world rendering
        if (ASIClientsideRequests.OFF_LEVEL_RENDERING) { ci.cancel(); }
    }   //*/
}
