package actually.portals.ActuallySize.mixin.holding.pose;

import actually.portals.ActuallySize.pickup.mixininterfaces.RenderNormalizable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * To display the player in inventory, Pehkui scales them down to scale 1
 * in their internal code. This messes up my {@link actually.portals.ActuallySize.pickup.item.ASIPSHeldEntityRenderer}
 * as it will no longer scale the held entity correctly so that it is the
 * correct size relative to the player's scale.
 * <p>
 * Furthermore, it is useful to indicate when someone is being rendered
 * in inventory, and treat that case differently for THIRD PERSON MAINHAND
 * / OFFHAND rendering contexts.
 *
 * @since 1.0.0
 */
@Mixin(value = InventoryScreen.class, priority = 950)
public abstract class InventoryScreenMixin$Pehkui {

    @Inject(method = "renderEntityInInventory", at = @At("HEAD"))
    private static void onRenderEntityInInventoryCall(GuiGraphics pGuiGraphics, int pX, int pY, int pScale, Quaternionf pPose, Quaternionf pCameraOrientation, LivingEntity pEntity, CallbackInfo ci) {
        RenderNormalizable rend = (RenderNormalizable) pEntity;
        rend.actuallysize$setPreNormalizedScale(false);
    }
    @Inject(method = "renderEntityInInventory", at = @At("RETURN"))
    private static void onRenderEntityInInventoryReturn(GuiGraphics pGuiGraphics, int pX, int pY, int pScale, Quaternionf pPose, Quaternionf pCameraOrientation, LivingEntity pEntity, CallbackInfo ci) {
        RenderNormalizable rend = (RenderNormalizable) pEntity;
        rend.actuallysize$setPreNormalizedScale(true);
    }
}
