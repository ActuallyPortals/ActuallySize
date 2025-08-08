package actually.portals.ActuallySize.pickup.actions;

import actually.portals.ActuallySize.netcode.packets.ASINetworkDelayableAction;
import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.APIFriendlyProcess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * An API friendly process involving an Item-Entity duality.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public abstract class ASIPSDualityAction implements APIFriendlyProcess, ASINetworkDelayableAction {

    /**
     * @see #getAttempts()
     *
     * @since 1.0.0
     */
    int attempts;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override public int getAttempts() { return attempts; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override public void logAttempt() { attempts++; }

    /**
     * @return The location of the ItemStack in the Holder's inventory
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable public abstract ItemStackLocation<? extends Entity> getStackLocation();

    /**
     * @param isl The location of the ItemStack in the Holder's inventory
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public abstract void setStackLocation(@Nullable ItemStackLocation<? extends Entity> isl);

    /**
     * @return The Entity, if it exists, of the Item-Entity duality
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable public abstract Entity getEntityCounterpart();

    /**
     * @param tiny The Entity, if it exists, of the Item-Entity duality
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public abstract void setEntityCounterpart(@Nullable Entity tiny);

    /**
     * @return The Item, if it exists, of the Item-Entity duality
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable public abstract ItemStack getItemCounterpart();

    /**
     * @param item The Item, if it exists, of the Item-Entity duality
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public abstract void  setItemCounterpart(@Nullable ItemStack item);

    /**
     * @return The hold point that this entity will be held in, read from
     *         the holder entity based on which hold points it has in its
     *         inventory for the given item stack location.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable public abstract ASIPSHoldPoint getHoldPoint();

    /**
     * @param hold  The hold point that this entity will be held in, read from
     *              the holder entity based on which hold points it has in its
     *              inventory for the given item stack location.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public abstract void setHoldPoint(@Nullable ASIPSHoldPoint hold);
}
