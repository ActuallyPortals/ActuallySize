package actually.portals.ActuallySize.pickup.holding.pose.poses;

import actually.portals.ActuallySize.pickup.holding.model.ASIMPLRendererLinker;
import actually.portals.ActuallySize.pickup.holding.pose.ASIPSModelPoseHoldPoint;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.SVFLBit;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

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
    public ASIPSTestPoserHold(@NotNull ResourceLocation nk, @NotNull SVFLBit svf, @NotNull ASIMPLRendererLinker... linkers) {
        super(nk, svf, linkers);
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIPSTestPoserHold(@NotNull ResourceLocation nk, @NotNull SVFLBit svf, @NotNull SVFLBit defaultOrigin, @NotNull ASIMPLRendererLinker... linkers) {
        super(nk, svf, defaultOrigin, linkers);
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public @NotNull Object getIdlePose() {
        return actually.portals.ActuallySize.pickup.holding.ASIPSArmPoses.ASI_TEST_HOLD;
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public @NotNull Object getUsePose() {
        return actually.portals.ActuallySize.pickup.holding.ASIPSArmPoses.ASI_TEST_HOLD;
    }
}
