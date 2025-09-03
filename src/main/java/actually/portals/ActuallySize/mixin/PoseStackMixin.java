package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.pickup.mixininterfaces.VASIPoseStack;
import actually.portals.ActuallySize.pickup.mixininterfaces.VASIPoseStackPose;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

@Mixin(PoseStack.class)
public abstract class PoseStackMixin implements VASIPoseStack {

    @Shadow @Final private Deque<PoseStack.Pose> poseStack;

    @Unique Deque<PoseStack.Pose> actuallysize$mirrorPoseStack;

    @Unique
    boolean actuallysize$mirroring;

    @Override public @NotNull Deque<PoseStack.Pose> actuallysize$getDeque() { return poseStack; }

    @Override
    public @NotNull Deque<PoseStack.Pose> actuallysize$getMirrorDeque() {
        return actuallysize$mirrorPoseStack;
    }

    @Override
    public void actuallysize$resetMirrorDeque() {
        if (!actuallysize$mirroring) { return; }
        actuallysize$mirrorPoseStack.clear();

        // Need a sample pose
        if (!poseStack.isEmpty()) {
            VASIPoseStackPose asComb = (VASIPoseStackPose) (Object) poseStack.getFirst();
            PoseStack.Pose pose = asComb.actuallysize$identity();
            actuallysize$mirrorPoseStack.add(pose);
        }
    }

    @Override
    public void actuallysize$enableMirrorDeque() {
        actuallysize$mirroring = true;
        actuallysize$mirrorPoseStack = new ArrayDeque<>();
    }

    @Override
    public void actuallysize$stopMirroring() {
        actuallysize$mirroring = false;
    }


    @Override
    public boolean actuallysize$isMirroring() { return actuallysize$mirroring; }

    @WrapOperation(method = "translate(FFF)V", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix4f;translate(FFF)Lorg/joml/Matrix4f;", remap = false), remap = true)
    public Matrix4f onTranslate(Matrix4f instance, float x, float y, float z, Operation<Matrix4f> original) {

        // Also translate mirror
        if (actuallysize$mirroring && !actuallysize$mirrorPoseStack.isEmpty()) {
            PoseStack.Pose posestack$pose = this.actuallysize$mirrorPoseStack.getLast();
            VASIPoseStackPose asComb = (VASIPoseStackPose) (Object) posestack$pose;
            asComb.actuallysize$pose().translate(x, y, z);
        }

        return original.call(instance, x, y, z);
    }

    @WrapOperation(method = "scale", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix3f;scale(FFF)Lorg/joml/Matrix3f;", remap = false), remap = true)
    public Matrix3f onScale(Matrix3f instance, float x, float y, float z, Operation<Matrix3f> original) {

        // Also scale mirror
        if (actuallysize$mirroring && !actuallysize$mirrorPoseStack.isEmpty()) {
            PoseStack.Pose posestack$pose = this.actuallysize$mirrorPoseStack.getLast();
            VASIPoseStackPose asComb = (VASIPoseStackPose) (Object) posestack$pose;
            asComb.actuallysize$normal().scale(x, y, z);
        }

        return original.call(instance, x, y, z);
    }

    @WrapOperation(method = "scale", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix3f;scale(F)Lorg/joml/Matrix3f;", remap = false), remap = true)
    public Matrix3f onScale(Matrix3f instance, float xyz, Operation<Matrix3f> original) {

        // Also scale mirror
        if (actuallysize$mirroring && !actuallysize$mirrorPoseStack.isEmpty()) {
            PoseStack.Pose posestack$pose = this.actuallysize$mirrorPoseStack.getLast();
            VASIPoseStackPose asComb = (VASIPoseStackPose) (Object) posestack$pose;
            asComb.actuallysize$normal().scale(xyz);
        }

        return original.call(instance, xyz);
    }

    @WrapOperation(method = "scale", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix4f;scale(FFF)Lorg/joml/Matrix4f;", remap = false), remap = true)
    public Matrix4f onScale(Matrix4f instance, float x, float y, float z, Operation<Matrix4f> original) {

        // Also scale mirror
        if (actuallysize$mirroring && !actuallysize$mirrorPoseStack.isEmpty()) {
            PoseStack.Pose posestack$pose = this.actuallysize$mirrorPoseStack.getLast();
            VASIPoseStackPose asComb = (VASIPoseStackPose) (Object) posestack$pose;
            asComb.actuallysize$pose().scale(x, y, z);
        }
        return original.call(instance, x, y, z);
    }

    @WrapOperation(method = "mulPose", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix3f;rotate(Lorg/joml/Quaternionfc;)Lorg/joml/Matrix3f;", remap = false), remap = true)
    public Matrix3f onMulPose(Matrix3f instance, Quaternionfc quat, Operation<Matrix3f> original) {

        // Also multiply mirror
        if (actuallysize$mirroring && !actuallysize$mirrorPoseStack.isEmpty()) {
            PoseStack.Pose posestack$pose = this.actuallysize$mirrorPoseStack.getLast();
            VASIPoseStackPose asComb = (VASIPoseStackPose) (Object) posestack$pose;
            asComb.actuallysize$normal().rotate(quat);
        }
        return original.call(instance, quat);
    }

    @WrapOperation(method = "mulPose", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix4f;rotate(Lorg/joml/Quaternionfc;)Lorg/joml/Matrix4f;", remap = false), remap = true)
    public Matrix4f onMulPose(Matrix4f instance, Quaternionfc quat, Operation<Matrix4f> original) {

        // Also multiply mirror
        if (actuallysize$mirroring && !actuallysize$mirrorPoseStack.isEmpty()) {
            PoseStack.Pose posestack$pose = this.actuallysize$mirrorPoseStack.getLast();
            VASIPoseStackPose asComb = (VASIPoseStackPose) (Object) posestack$pose;
            asComb.actuallysize$pose().rotate(quat);
        }
        return original.call(instance, quat);
    }

    @WrapOperation(method = "rotateAround", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix3f;rotate(Lorg/joml/Quaternionfc;)Lorg/joml/Matrix3f;", remap = false), remap = true)
    public Matrix3f onRotateAround(Matrix3f instance, Quaternionfc quat, Operation<Matrix3f> original) {

        // Also rotate mirror
        if (actuallysize$mirroring && !actuallysize$mirrorPoseStack.isEmpty()) {
            PoseStack.Pose posestack$pose = this.actuallysize$mirrorPoseStack.getLast();
            VASIPoseStackPose asComb = (VASIPoseStackPose) (Object) posestack$pose;
            asComb.actuallysize$normal().rotate(quat);
        }
        return original.call(instance, quat);
    }

    @WrapOperation(method = "rotateAround", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix4f;rotateAround(Lorg/joml/Quaternionfc;FFF)Lorg/joml/Matrix4f;", remap = false), remap = true)
    public Matrix4f onRotateAround(Matrix4f instance, Quaternionfc quat, float ox, float oy, float oz, Operation<Matrix4f> original) {

        // Also rotate mirror
        if (actuallysize$mirroring && !actuallysize$mirrorPoseStack.isEmpty()) {
            PoseStack.Pose posestack$pose = this.actuallysize$mirrorPoseStack.getLast();
            VASIPoseStackPose asComb = (VASIPoseStackPose) (Object) posestack$pose;
            asComb.actuallysize$pose().rotateAround(quat, ox, oy, oz);
        }
        return original.call(instance, quat, ox, oy, oz);
    }

    @Inject(method = "pushPose", at = @At("RETURN"))
    public void onPush(CallbackInfo ci) {

        // Also push mirror
        if (actuallysize$mirroring) {
            PoseStack.Pose posestack$pose = this.actuallysize$mirrorPoseStack.getLast();
            VASIPoseStackPose asComb = (VASIPoseStackPose) (Object) posestack$pose;
            actuallysize$mirrorPoseStack.addLast(asComb.actuallysize$dupe());
        }
    }

    @Inject(method = "popPose", at = @At("RETURN"))
    public void onPop(CallbackInfo ci) {

        // Also pop mirror
        if (actuallysize$mirroring && !actuallysize$mirrorPoseStack.isEmpty()) {
            if (actuallysize$mirrorPoseStack.size() > 1) {
                this.actuallysize$mirrorPoseStack.removeLast();
            } else {
                PoseStack.Pose posestack$pose = this.actuallysize$mirrorPoseStack.getLast();
                VASIPoseStackPose asComb = (VASIPoseStackPose) (Object) posestack$pose;
                this.actuallysize$mirrorPoseStack.removeLast();
                actuallysize$mirrorPoseStack.addLast(asComb.actuallysize$identity());
            }
        }
    }

    /*
    @Inject(method = "setIdentity", at = @At("RETURN"))
    public void onIdentity(CallbackInfo ci) {

        // Also rotate mirror
        if (actuallysize$mirroring) {
            actuallysize$resetMirrorDeque();
        }
    }   //*/

    @Override
    public PoseStack.Pose actuallysize$mirrorLast() {
        if (actuallysize$mirroring) {
            return this.actuallysize$mirrorPoseStack.getLast();
        }
        return null;
    }

    @WrapOperation(method = "mulPoseMatrix", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix4f;mul(Lorg/joml/Matrix4fc;)Lorg/joml/Matrix4f;", remap = false), remap = true)
    public Matrix4f onMulMatrix(Matrix4f instance, Matrix4fc right, Operation<Matrix4f> original) {

        // Also multiply mirror
        if (actuallysize$mirroring && !actuallysize$mirrorPoseStack.isEmpty()) {
            PoseStack.Pose posestack$pose = this.actuallysize$mirrorPoseStack.getLast();
            VASIPoseStackPose asComb = (VASIPoseStackPose) (Object) posestack$pose;
            asComb.actuallysize$pose().mul(right);
        }
        return original.call(instance, right);
    }

    @Unique
    @Nullable ItemEntityDualityHolder actuallysize$parent;

    @Nullable @Unique ArrayList<EntityDualityCounterpart> actuallysize$children;

    @Nullable @Unique EntityRenderer<? extends Entity> actuallysize$renderer;

    @Override
    public @Nullable ItemEntityDualityHolder actuallysize$getPoseParent() {
        return actuallysize$parent;
    }

    @Override
    public void actuallysize$setPoseParent(@Nullable ItemEntityDualityHolder parent) {
        actuallysize$parent = parent;
    }

    @Override
    public @Nullable ArrayList<EntityDualityCounterpart> actuallysize$getPoseChildren() {
        return actuallysize$children;
    }

    @Override
    public void actuallysize$setPoseChildren(@Nullable ArrayList<EntityDualityCounterpart> children) {
        actuallysize$children = children;
    }

    @Override
    public @Nullable EntityRenderer<? extends Entity> actuallysize$getRenderer() {
        return actuallysize$renderer;
    }

    @Override
    public void actuallysize$setRenderer(@Nullable EntityRenderer<? extends Entity> rend) {
        actuallysize$renderer = rend;
    }
}
