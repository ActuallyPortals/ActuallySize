package actually.portals.ActuallySize.pickup.holding.pose;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A Hold Point that also modifies the arm pose of the entity
 * when the item-counterpart is held in a main hand.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface ASIPSArmPoserHold {

    /**
     * @return The Arm Pose by which the smol is held
     *
     * @param holder The beeg doing the holding
     * @param hand The hand the item is in
     * @param itemCounterpart The item stack
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull Object getHoldPose(@NotNull LivingEntity holder, @NotNull InteractionHand hand, @NotNull ItemStack itemCounterpart);
}
