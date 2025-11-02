package actually.portals.ActuallySize.mixin;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin {

    @Shadow public HumanoidModel.ArmPose rightArmPose;

    @Shadow public HumanoidModel.ArmPose leftArmPose;

    @Inject(method = "poseRightArm", at = @At("HEAD"), cancellable = true)
    public <T extends LivingEntity> void onPoseRight(T pLivingEntity, CallbackInfo ci) {
        if (this.rightArmPose.ordinal() > 9) {
            ci.cancel();
            HumanoidModel<T> humanoid = (HumanoidModel<T>) (Object) this;
            this.rightArmPose.applyTransform(humanoid, pLivingEntity, HumanoidArm.RIGHT);
        }
    }

    @Inject(method = "poseLeftArm", at = @At("HEAD"), cancellable = true)
    public <T extends LivingEntity> void onPoseLeft(T pLivingEntity, CallbackInfo ci) {
        if (this.leftArmPose.ordinal() > 9) {
            ci.cancel();
            HumanoidModel<T> humanoid = (HumanoidModel<T>) (Object) this;
            this.leftArmPose.applyTransform(humanoid, pLivingEntity, HumanoidArm.LEFT);
        }
    }
}
