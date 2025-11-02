package actually.portals.ActuallySize.pickup.holding.pose.beeg;

import actually.portals.ActuallySize.pickup.holding.model.ASIMPLRendererLinker;
import actually.portals.ActuallySize.pickup.holding.pose.ASIPSModelPoseHoldPoint;
import actually.portals.ActuallySize.pickup.holding.pose.smol.ASIPSTinyPoseProfile;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.SVFLBit;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A pose hold that uses the TEST hold trasnformer
 *
 * @author Actually Portals
 * @since 1.0.0
 */
public class ASIPSTestPoserHold extends ASIPSModelPoseHoldPoint {

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIPSTestPoserHold(@NotNull ResourceLocation nk, @Nullable ASIPSTinyPoseProfile tinyPose, @NotNull SVFLBit svf, @NotNull ASIMPLRendererLinker... linkers) {
        super(nk, tinyPose, svf, linkers);
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIPSTestPoserHold(@NotNull ResourceLocation nk, @Nullable ASIPSTinyPoseProfile tinyPose, @NotNull SVFLBit svf, @NotNull SVFLBit defaultOrigin, @NotNull ASIMPLRendererLinker... linkers) {
        super(nk, tinyPose, svf, defaultOrigin, linkers);
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public @NotNull Object getIdlePose() {
        return ASIPSArmPoses.ASI_TEST_HOLD;
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public @NotNull Object getUsePose() {
        return ASIPSArmPoses.ASI_TEST_HOLD;
    }
}
