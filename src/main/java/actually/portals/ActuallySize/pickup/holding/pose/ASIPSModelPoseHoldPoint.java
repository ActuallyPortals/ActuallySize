package actually.portals.ActuallySize.pickup.holding.pose;

import actually.portals.ActuallySize.pickup.holding.model.ASIMPLRendererLinker;
import actually.portals.ActuallySize.pickup.holding.model.ASIPSModelPartHoldPoint;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.SVFLBit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A hold point that puts the held entity at a specific set of
 * coordinates relative to the holder's specified model part.
 *
 * @author Actually Portals
 * @since 1.0.0
 */
public abstract class ASIPSModelPoseHoldPoint extends ASIPSModelPartHoldPoint implements ASIPSArmPoserHold {

    /**
     * A ride point relative to the holder's pitch, yaw, and world coordinates.
     *
     * @param nk The namespaced key to name this slot
     * @param svf The SVF coordinate information
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIPSModelPoseHoldPoint(@NotNull ResourceLocation nk, @NotNull SVFLBit svf, @NotNull ASIMPLRendererLinker... linkers) {
        super(nk, svf, linkers);
    }

    /**
     * A ride point relative to the holder's pitch, yaw, and world coordinates.
     *
     * @param nk The namespaced key to name this slot
     * @param svf The SVF coordinate information
     *
     * @param defaultOrigin Before any model part can be calculated, this transformation
     *                      is applied to the holder coordinates to approximate where the
     *                      model part should be.
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIPSModelPoseHoldPoint(@NotNull ResourceLocation nk, @NotNull SVFLBit svf, @NotNull SVFLBit defaultOrigin, @NotNull ASIMPLRendererLinker... linkers) {
        super(nk, svf, defaultOrigin, linkers);
    }

    /**
     * The context-dependent arm pose when holding the tiny
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public abstract Object getIdlePose();

    /**
     * The context-dependent arm pose when using the tiny
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public abstract Object getUsePose();

    /**
     * @return The Arm Pose by which the smol is held
     *
     * @param holder The beeg doing the holding
     * @param hand The hand the item is in
     * @param itemCounterpart The item stack
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public @NotNull Object getHoldPose(@NotNull LivingEntity holder, @NotNull InteractionHand hand, @NotNull ItemStack itemCounterpart) {

        // Sometimes, items are actively being used (and should be held differently)
        boolean activelyInUse = (holder.getUsedItemHand() == hand && holder.getUseItemRemainingTicks() > 0);
        if (activelyInUse) { return getUsePose(); } else { return getIdlePose(); }
    }
}
