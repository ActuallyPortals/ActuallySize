package actually.portals.ActuallySize.pickup.holding.pose.transformers;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IArmPoseTransformer;

/**
 * The arm pose transformer to hold tinies in front of you
 *
 * @author Actually Portals
 * @since 1.0.0
 */
@OnlyIn(Dist.CLIENT)
public class ASIPSPIdleTransformer  implements IArmPoseTransformer {

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public void applyTransform(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm) {
        ModelPart whoo;
        int sense;
        if (arm == HumanoidArm.LEFT) { whoo = model.leftArm; sense = 1; } else { whoo = model.rightArm; sense = -1; }

        whoo.xRot = -1.1344640138F; // -65°
        whoo.yRot = sense * 0.261799387799F; // 15°
        whoo.zRot = 0;
    }
}
