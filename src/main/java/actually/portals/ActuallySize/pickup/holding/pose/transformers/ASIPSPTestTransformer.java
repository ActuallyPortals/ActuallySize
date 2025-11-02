package actually.portals.ActuallySize.pickup.holding.pose.transformers;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IArmPoseTransformer;

/**
 * A transformer that waves the arms wildly to
 * check out how model point pose holds are doing
 *
 * @author Actually Portals
 * @since 1.0.0
 */
@OnlyIn(Dist.CLIENT)
public class ASIPSPTestTransformer implements IArmPoseTransformer {

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public void applyTransform(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm) {
        ModelPart whoo;
        if (arm == HumanoidArm.LEFT) { whoo = model.leftArm; } else { whoo = model.rightArm; }

        whoo.xRot = (float) Math.toRadians(entity.position().x);
        //whoo.yRot = 0F; //(float) Math.cos(System.currentTimeMillis() * 0.005D);
        whoo.yRot = (float) Math.toRadians(entity.position().y-200);
        whoo.zRot = (float) Math.toRadians(entity.position().z);
    }
}
