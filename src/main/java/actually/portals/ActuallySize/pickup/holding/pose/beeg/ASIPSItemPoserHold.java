package actually.portals.ActuallySize.pickup.holding.pose.beeg;

import actually.portals.ActuallySize.pickup.holding.model.ASIMPLRendererLinker;
import actually.portals.ActuallySize.pickup.holding.pose.ASIPSModelPoseHoldPoint;
import actually.portals.ActuallySize.pickup.holding.pose.smol.ASIPSTinyPoseProfile;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.SVFLBit;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A pose hold that simply holds the tiny like
 * they were any normal item, really.
 *
 * @author Actually Portals
 * @since 1.0.0
 */
public class ASIPSItemPoserHold extends ASIPSModelPoseHoldPoint {

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIPSItemPoserHold(@NotNull ResourceLocation nk, @Nullable ASIPSTinyPoseProfile tinyPose, @NotNull SVFLBit svf, @NotNull ASIMPLRendererLinker... linkers) {
        super(nk, tinyPose, svf, linkers);
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIPSItemPoserHold(@NotNull ResourceLocation nk, @Nullable ASIPSTinyPoseProfile tinyPose, @NotNull SVFLBit svf, @NotNull SVFLBit defaultOrigin, @NotNull ASIMPLRendererLinker... linkers) {
        super(nk, tinyPose, svf, defaultOrigin, linkers);
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public @NotNull Object getIdlePose() {
        return ASIPSArmPoses.ASI_IDLE_HOLD;
        //return net.minecraft.client.model.HumanoidModel.ArmPose.ITEM;
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public @NotNull Object getUsePose() {
        return ASIPSArmPoses.ASI_KISS_HOLD;
    }
}
