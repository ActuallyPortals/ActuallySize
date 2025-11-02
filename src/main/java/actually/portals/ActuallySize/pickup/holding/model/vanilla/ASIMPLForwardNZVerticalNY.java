package actually.portals.ActuallySize.pickup.holding.model.vanilla;

import actually.portals.ActuallySize.pickup.holding.model.ASIMPLRendererLinker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

/**
 * A renderer linker which forward axis is the negative of the third vector of the 4x4 matrix,
 * and its vertical axis is the negative of the second vector of the 4x4 matrix of the pose stack.
 *
 * @author Actually Portals
 * @since 1.0.0
 */
public class ASIMPLForwardNZVerticalNY extends ASIMPLRendererLinker {

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIMPLForwardNZVerticalNY(@NotNull String part) {
        super(part);
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public @NotNull Vec3 getBasis(@NotNull ModelPart part, @NotNull PoseStack stack, @NotNull Matrix4f pose) {
        return new Vec3(-pose.m20(), -pose.m21(), -pose.m22());
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public @NotNull Vec3 getSense(@NotNull ModelPart part, @NotNull PoseStack stack, @NotNull Matrix4f pose) {
        return new Vec3(-pose.m10(), -pose.m11(), -pose.m12());
    }
}
