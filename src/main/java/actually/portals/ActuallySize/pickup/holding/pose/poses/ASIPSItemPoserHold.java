package actually.portals.ActuallySize.pickup.holding.pose.poses;

import actually.portals.ActuallySize.pickup.holding.model.ASIMPLRendererLinker;
import actually.portals.ActuallySize.pickup.holding.pose.ASIPSModelPoseHoldPoint;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.SVFLBit;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

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
    public ASIPSItemPoserHold(@NotNull ResourceLocation nk, @NotNull SVFLBit svf, @NotNull ASIMPLRendererLinker... linkers) {
        super(nk, svf, linkers);
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIPSItemPoserHold(@NotNull ResourceLocation nk, @NotNull SVFLBit svf, @NotNull SVFLBit defaultOrigin, @NotNull ASIMPLRendererLinker... linkers) {
        super(nk, svf, defaultOrigin, linkers);
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public @NotNull Object getIdlePose() {
        return actually.portals.ActuallySize.pickup.holding.ASIPSArmPoses.ASI_IDLE_HOLD;
        //return net.minecraft.client.model.HumanoidModel.ArmPose.ITEM;
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public @NotNull Object getUsePose() {
        return actually.portals.ActuallySize.pickup.holding.ASIPSArmPoses.ASI_KISS_HOLD;
    }
}
