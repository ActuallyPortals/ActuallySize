package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import actually.portals.ActuallySize.pickup.mixininterfaces.VASIPoseStack;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {

    @Shadow @Final private PoseStack pose;

    @WrapMethod(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V")
    public void onRenderItem(LivingEntity pEntity, Level pLevel, ItemStack pStack, int pX, int pY, int pSeed, int pGuiOffset, Operation<Void> original) {

        // Set parent
        ItemEntityDualityHolder dualityHolder = (ItemEntityDualityHolder) pEntity;
        VASIPoseStack asDeque = (VASIPoseStack) this.pose;

        // Nested calls will be annoying
        ItemEntityDualityHolder concurrent = asDeque.actuallysize$getPoseParent();
        ArrayList<EntityDualityCounterpart> deckedChildren = asDeque.actuallysize$getPoseChildren();
        EntityRenderer<? extends Entity> rend = asDeque.actuallysize$getRenderer();

        // Replace parent
        asDeque.actuallysize$setPoseParent(dualityHolder);
        asDeque.actuallysize$setPoseChildren(null);
        asDeque.actuallysize$setRenderer(null);

        /* If it has children, record them
        if (!dualityHolder.actuallysize$getHeldEntityDualities().isEmpty()) {
            ArrayList<EntityDualityCounterpart> children = new ArrayList<>();

            // Add held entities (not simple dualities)
            for (Map.Entry<ASIPSHoldPoint, EntityDualityCounterpart> child : dualityHolder.actuallysize$getHeldEntityDualities().entrySet()) {
                if (child.getValue().actuallysize$isHeld()) { children.add(child.getValue()); }
            }

            // Save children
            if (!children.isEmpty()) { asDeque.actuallysize$setPoseChildren(children); }
        }   //*/

        // Call original
        original.call(pEntity, pLevel, pStack, pX, pY, pSeed, pGuiOffset);

        // Remove parent and children
        asDeque.actuallysize$setPoseParent(concurrent);
        asDeque.actuallysize$setPoseChildren(deckedChildren);
        asDeque.actuallysize$setRenderer(rend);
    }
}
