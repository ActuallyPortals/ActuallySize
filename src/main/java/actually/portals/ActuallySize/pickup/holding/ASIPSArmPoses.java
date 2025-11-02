package actually.portals.ActuallySize.pickup.holding;

import actually.portals.ActuallySize.pickup.holding.pose.transformers.ASIPSPIdleTransformer;
import actually.portals.ActuallySize.pickup.holding.pose.transformers.ASIPSPKissTransformer;
import actually.portals.ActuallySize.pickup.holding.pose.transformers.ASIPSPTestTransformer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * <a href="https://discord.com/channels/1133813159531126934/1133813159984119938/1429503483270270996">"this dude LOVES his handheld tinies"</a>
 * <br><br>
 * The collection of special arm poses that ASI
 * provides to hold tinies in your hand with the
 * many hand-held hold-points
 *
 * @author Actually Portals
 * @since 1.0.0
 */
@OnlyIn(Dist.CLIENT)
public class ASIPSArmPoses {

    /**
     * An arm pose that holds a tiny right in front of your face, but below your nose.
     * Not quite seeing eye-to-eye, but rather personal I guess or something.
     *
     * @since 1.0.0
     */
    public static final HumanoidModel.ArmPose ASI_KISS_HOLD = HumanoidModel.ArmPose.create("ASI_KISS_HOLD", false, new ASIPSPKissTransformer());

    /**
     * An arm pose to test rotations of arm poses.
     *
     * @since 1.0.0
     */
    public static final HumanoidModel.ArmPose ASI_TEST_HOLD = HumanoidModel.ArmPose.create("ASI_TEST_HOLD", false, new ASIPSPTestTransformer());

    /**
     * An arm pose of holding a tiny dignifiedly but in idle, somewhat a
     * relaxed arm for the beeg while not treating the tiny like any other item.
     *
     * @since 1.0.0
     */
    public static final HumanoidModel.ArmPose ASI_IDLE_HOLD = HumanoidModel.ArmPose.create("ASI_IDLE_HOLD", false, new ASIPSPIdleTransformer());
}
