package actually.portals.ActuallySize.pickup.holding.pose.beeg.transformers;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IArmPoseTransformer;

/**
 * The arm pose transformer to hold tinies in a kiss
 *
 * @author Actually Portals
 * @since 1.0.0
 */
@OnlyIn(Dist.CLIENT)
public class ASIPSPKissTransformer implements IArmPoseTransformer {

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public void applyTransform(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm) {
        ModelPart whoo;
        int sense;
        if (arm == HumanoidArm.LEFT) { whoo = model.leftArm; sense = 1; } else { whoo = model.rightArm; sense = -1; }

        float turn = 0.610865238198F;
        double diff = entity.yHeadRot - entity.yBodyRot;
        float mag = (float) (diff * 0.02), pag = 0F, xO, xE, yO, yE, zO, zE;

        // P =  90°, X = -30°, Y = 8°, Z = 35°
        // P =   0°, X = -85°, Y = 30°, Z = 0°
        // P = -90°, X = -190°, Y = -43°, Z = 0°
        xO = -1.4835298642F; // -85°
        yO = 0.523598775598F + (mag * turn * sense); // 30°
        zO = 0; // 0°

        // No change when crouching
        if (entity.isCrouching()) {
            xE = xO;
            yE = yO;
            zE = zO;

        // Standing up varies it
        } else {

            // Hold profile when looking down
            double pitch = entity.xRotO;
            double pi2 = Math.PI * 2;
            while (pitch >= pi2) { pitch -= pi2; }
            while (pitch < 0) { pitch += pi2; }
            if (pitch > 0) {
                pag = (float) (pitch * 0.0111);
                xE = -0.523598775598F; // -30°
                yE = 0.13962634016F; // 8°
                zE = 0.610865238198F; // 35°

            // Different profile when looking up
            } else {

                // Looking above only effective up to 60°
                if (pitch > -60) {
                    pag = -(float) (pitch * 0.016667);
                    xE = -2.70526034059F; // -155° (two-thirds of the way to -190°)
                    yE = yO; // 30°
                    zE = 0F; // 0°

                // When looking far above, profile changes
                } else {
                    pag = -(float) ((pitch+60) * 0.0333333);
                    xO = -2.70526034059F; // -155° (two-thirds of the way to -190°)
                    xE = -3.31612557879F; // -190°
                    yE = -0.750491578358F; // -43°
                    zE = 0F; // 0°
                }
            }
        }

        // A wobble amount
        float wob = (0.0872664625997F * (float) Math.cos(System.currentTimeMillis() * 0.015D));

        // Finish
        whoo.xRot = xO + ((xE - xO) * pag) + wob; // -85° + 5°
        whoo.yRot = sense * (yO + ((yE - yO) * pag)); // 30° + 35°
        whoo.zRot = sense * (zO + ((zE - zO) * pag));
    }
}
