package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import actually.portals.ActuallySize.pickup.holding.model.ASIMPLRendererLinker;
import actually.portals.ActuallySize.pickup.mixininterfaces.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Map;

@Mixin(ModelPart.class)
public abstract class ModelPartMixin implements VASIModelPart {

    @Shadow @Final private Map<String, ModelPart> children;
    @Unique @Nullable String actuallysize$partName = null;

    @Inject(method = "getChild", at = @At(value = "HEAD"))
    public void OnChildrenFetch(String pName, CallbackInfoReturnable<ModelPart> cir) {
        ModelPart ret =  this.children.get(pName);

        // Make it answer to its name
        if (ret != null) {
            VASIModelPart asDeq = ((VASIModelPart) (Object) ret);
            //if (asDeq.actuallysize$getModelName() != null) { ActuallySizeInteractions.Log("ASI &3 DEQ &7 Renamed model part &b " + asDeq.actuallysize$getModelName() + " &r to &e " + pName); }
            asDeq.actuallysize$setModelName(pName); }
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"))
    public void PostModelPartRender(PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha, CallbackInfo ci) {

        // If there are entities to pose
        ArrayList<EntityDualityCounterpart> children = ((VASIPoseStack) pPoseStack).actuallysize$getPoseChildren();
        if (children != null) {

            // Identify
            ItemEntityDualityHolder dualityHolder = ((VASIPoseStack) pPoseStack).actuallysize$getPoseParent();
            ModelPart part = (ModelPart) (Object) this;

            // Must try update in all of them
            for (EntityDualityCounterpart entityCounterpart : children) {

                // Find the linker for this in the Hold Point configuration and link
                ASIPSHoldPoint holdPoint = entityCounterpart.actuallysize$getHoldPoint();
                ASIMPLRendererLinker linker = holdPoint.getModelPartLinker(dualityHolder, entityCounterpart, part, ASIMPLRendererLinker.class);
                if (linker != null) { linker.linkModel(dualityHolder, entityCounterpart, part, pPoseStack); }
            }
        }
    }

    @Override
    public @Nullable String actuallysize$getModelName() {
        return actuallysize$partName;
    }

    @Override
    public void actuallysize$setModelName(@Nullable String name) {
        actuallysize$partName = name;
    }
}
