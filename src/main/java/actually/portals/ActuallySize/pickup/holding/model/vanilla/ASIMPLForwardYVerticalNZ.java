package actually.portals.ActuallySize.pickup.holding.model.vanilla;

import actually.portals.ActuallySize.pickup.holding.model.ASIMPLRendererLinker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

/**
 * A renderer linker to link the right arm model part
 * Except it has problems when the vertical axis gets
 * a negative Y component, since the correct metric
 * should have been that it breaks the plane defined by
 * it, not having a negative Y component.
 * <br><br>
 * Because this vertical axis is sideways, it makes the
 * funny when its Y component is negative and its side
 * and forward get inverted 90° off from when they should
 *
 * @author Actually Portals
 * @since 1.0.0
 */
@Deprecated
public class ASIMPLForwardYVerticalNZ extends ASIMPLRendererLinker {

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIMPLForwardYVerticalNZ(@NotNull String part) { super(part); }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public @NotNull Vec3 getBasis(@NotNull ModelPart part, @NotNull PoseStack stack, @NotNull Matrix4f pose) {
        return new Vec3(pose.m10(), pose.m11(), pose.m12());
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public @NotNull Vec3 getSense(@NotNull ModelPart part, @NotNull PoseStack stack, @NotNull Matrix4f pose) {
        return new Vec3(-pose.m20(), -pose.m21(), -pose.m22());
    }
}
