package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.pickup.mixininterfaces.VASIPoseStackPose;
import com.mojang.blaze3d.vertex.PoseStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Mixin(PoseStack.Pose.class)
public abstract class PoseStackPoseMixin implements VASIPoseStackPose {
    @Shadow @Final private Matrix4f pose;
    @Shadow @Final private Matrix3f normal;
    @Unique
    private static Constructor<PoseStack.Pose> actuallysize$realConstructor;

    /**
     * @return Actually just clones this thing, no combine, send in a null one if you want
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public @NotNull PoseStack.Pose actuallysize$identity() {

        // Fetch the constructor once
        if (actuallysize$realConstructor == null) {
            try {
                Class<PoseStack.Pose> pose = PoseStack.Pose.class;
                actuallysize$realConstructor = pose.getDeclaredConstructor(Matrix4f.class, Matrix3f.class);
                //actuallysize$realConstructor.setAccessible(true);

            } catch (NoSuchMethodException ignored) { }
        }

        // Create identity pose
        try {
            return actuallysize$realConstructor.newInstance(new Matrix4f(), new Matrix3f());

        // Not gonna happen :crpyawn:
        } catch (InstantiationException|IllegalAccessException|InvocationTargetException ignored) { }
        return null;
    }

    @Override
    public @NotNull Matrix4f actuallysize$pose() {
        return pose;
    }

    @Override
    public @NotNull Matrix3f actuallysize$normal() {
        return normal;
    }

    @Override
    public @NotNull PoseStack.Pose actuallysize$dupe() {
        // Create new one with these same specs
        try {
            return actuallysize$realConstructor.newInstance(new Matrix4f(pose), new Matrix3f(normal));

            // Not gonna happen :crpyawn:
        } catch (InstantiationException|IllegalAccessException|InvocationTargetException ignored) { }
        return null;
    }
}
