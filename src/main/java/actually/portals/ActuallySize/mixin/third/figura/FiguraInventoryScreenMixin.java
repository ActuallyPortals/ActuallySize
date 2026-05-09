package actually.portals.ActuallySize.mixin.third.figura;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.pickup.mixininterfaces.PlayerBound;
import actually.portals.ActuallySize.pickup.mixininterfaces.RenderNormalizable;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = InventoryScreen.class, priority = 1001)
public abstract class FiguraInventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> implements RecipeUpdateListener {
    public FiguraInventoryScreenMixin(InventoryMenu pMenu, Inventory pPlayerInventory, Component pTitle) { super(pMenu, pPlayerInventory, pTitle); }

    @WrapMethod(method = "renderEntityInInventoryFollowsMouse")
    private static void onRenderEntityInInventoryCall(GuiGraphics pGuiGraphics, int pX, int pY, int pScale, float pMouseX, float pMouseY, LivingEntity pEntity, Operation<Void> original) {
        RenderNormalizable rend = (RenderNormalizable) pEntity;
        rend.actuallysize$setPreNormalizedScale(false);

        // Bind this GUI Graphics to the player
        if (pEntity instanceof Player) {
            ((PlayerBound) pGuiGraphics).actuallysize$setBoundPlayer((Player) pEntity);

            // Magic
            pScale = OotilityNumbers.floor(1.07 * ((double) pScale / rend.actuallysize$getPreNormalizedScale()));
        }

        // Render
        original.call(pGuiGraphics, pX, pY, pScale, pMouseX, pMouseY, pEntity);

        // Reset
        ((PlayerBound) pGuiGraphics).actuallysize$setBoundPlayer(null);
        rend.actuallysize$setPreNormalizedScale(true);
    }
}
