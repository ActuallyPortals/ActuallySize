package actually.portals.ActuallySize.mixin.third.figura;

import actually.portals.ActuallySize.pickup.mixininterfaces.RenderNormalizable;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import org.figuramc.figura.model.rendering.EntityRenderMode;
import org.figuramc.figura.utils.ui.UIHelper;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = UIHelper.class, remap = false)
public class FiguraUIHelperMixin {

    @WrapMethod(method = "drawEntity")
    private static void OnDrawEntity(float x, float y, float scale, float pitch, float yaw, LivingEntity entity, GuiGraphics gui, EntityRenderMode renderMode, Operation<Void> original) {

        RenderNormalizable rend = (RenderNormalizable) entity;
        if (rend.actuallysize$isScaleNormalized() || (renderMode == EntityRenderMode.FIGURA_GUI)) { scale /= (float) rend.actuallysize$getPreNormalizedScale(); }

        original.call(x, y, scale, pitch, yaw, entity, gui, renderMode);
    }
}
