package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import actually.portals.ActuallySize.pickup.mixininterfaces.GraceImpulsable;
import actually.portals.ActuallySize.pickup.mixininterfaces.VASIPoseStack;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.Map;

@Mixin(value = EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin implements ResourceManagerReloadListener, GraceImpulsable {

    @Shadow private boolean shouldRenderShadow;

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    private <T extends Entity> void onRenderCall(EntityRenderer<T> instance, T pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, Operation<Void> original) {

        // Set parent
        ItemEntityDualityHolder dualityHolder = (ItemEntityDualityHolder) pEntity;
        VASIPoseStack asDeque = (VASIPoseStack) pPoseStack;

        // Nested calls will be annoying
        ItemEntityDualityHolder concurrent = asDeque.actuallysize$getPoseParent();
        ArrayList<EntityDualityCounterpart> deckedChildren = asDeque.actuallysize$getPoseChildren();
        EntityRenderer<? extends Entity> rend = asDeque.actuallysize$getRenderer();

        // Replace parent
        asDeque.actuallysize$setPoseParent(dualityHolder);
        asDeque.actuallysize$setPoseChildren(null);
        asDeque.actuallysize$setRenderer(instance);

        // If it has children, record them
        if (!dualityHolder.actuallysize$getHeldEntityDualities().isEmpty()) {
            ArrayList<EntityDualityCounterpart> children = new ArrayList<>();

            // Add held entities (not simple dualities)
            for (Map.Entry<ASIPSHoldPoint, EntityDualityCounterpart> child : dualityHolder.actuallysize$getHeldEntityDualities().entrySet()) {
                if (child.getValue().actuallysize$isHeld()) { children.add(child.getValue()); }
            }

            // Save children
            if (!children.isEmpty()) { asDeque.actuallysize$setPoseChildren(children); }
        }

        // Call original
        original.call(instance, pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);

        // Remove parent and children
        asDeque.actuallysize$setPoseParent(concurrent);
        asDeque.actuallysize$setPoseChildren(deckedChildren);
        asDeque.actuallysize$setRenderer(rend);
    }

    @Override
    public void actuallysize$addGraceImpulse(int ticks) { }

    @Override
    public boolean actuallysize$isInGraceImpulse() { return shouldRenderShadow; }
}
