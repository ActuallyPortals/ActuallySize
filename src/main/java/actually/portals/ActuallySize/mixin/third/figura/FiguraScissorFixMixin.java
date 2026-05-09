package actually.portals.ActuallySize.mixin.third.figura;

import actually.portals.ActuallySize.pickup.mixininterfaces.PlayerBound;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GuiGraphics.class)
public abstract class FiguraScissorFixMixin implements net.minecraftforge.client.extensions.IForgeGuiGraphics, PlayerBound {

    @Unique @Nullable Player actuallysize$boundPlayer;

    @WrapMethod(method = "enableScissor")
    public void WhenFiguraScissoring(int pMinX, int pMinY, int pMaxX, int pMaxY, Operation<Void> original) {

        // Only makes sense when a player is bound
        if (actuallysize$boundPlayer != null) {
            pMinY -= 26;
            pMinX -= 10;
            pMaxX += 8;
        }

        original.call(pMinX, pMinY, pMaxX, pMaxY);
    }

    @Override
    public @Nullable Player actuallysize$getBoundPlayer() {
        return actuallysize$boundPlayer;
    }

    @Override
    public void actuallysize$setBoundPlayer(@Nullable Player player) {
        actuallysize$boundPlayer = player;
    }
}
