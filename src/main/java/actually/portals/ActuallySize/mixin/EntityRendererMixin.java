package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.controlling.execution.ASIClientsideRequests;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

    /*
    @Inject(method = "renderNameTag", at = @At("HEAD"), cancellable = true)
    public <T extends Entity> void onRenderEntity(T pEntity, Component pDisplayName, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, CallbackInfo ci) {

        // Don't render nametags of entities that are held except during world rendering
        if (ASIClientsideRequests.OFF_LEVEL_RENDERING) { ci.cancel(); }
    }   //*/
}
