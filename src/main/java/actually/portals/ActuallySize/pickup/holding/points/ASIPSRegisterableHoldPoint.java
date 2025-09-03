package actually.portals.ActuallySize.pickup.holding.points;

import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemExplorerStatement;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import gunging.ootilities.GungingOotilitiesMod.exploring.entities.ISEEquipmentSlotted;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * The "registerable" hold points are glorified {@link ASIPSHoldPoint} enum constants
 * that are registered onto the {@link actually.portals.ActuallySize.pickup.ASIPickupSystemManager}
 * during mod loading. This way they are somewhat of an expandable enum by third parties.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public abstract class ASIPSRegisterableHoldPoint extends ASIPSHoldPoint {

    /**
     * The goal of this system is to use hold points as enums, and be able to index maps by
     * them, and having instant operations when browsing those maps. For this purpose the hold
     * point is assigned an integer during mod load, which is guaranteed to remain constant
     * while the program executes but may not necessarily be the same across multiple runs.
     * <p></p>
     * Please do not save this number to files, use the {@link #getNamespacedKey()} to save and load
     * if this in persistence if you ever need such a thing.
     *
     * @since 1.0.0
     */
    int ordinal = 0;

    /**
     * Only occurs during mod load, assigns an index to this dynamic-like enum. These are not
     * guaranteed to be identical over multiple program runs, but will work during one session.
     * Please save anything persistent by the {@link #getNamespacedKey()} and not this number.
     *
     * @param i The enum constant to assign to this hold point
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void setOrdinal(int i) { ordinal = i; }

    /**
     * @return The ordinal of this hold point, if treated as an enum
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public int getOrdinal() { return ordinal; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ASIPSRegisterableHoldPoint)) { return false; }
        ASIPSRegisterableHoldPoint that = (ASIPSRegisterableHoldPoint) o;
        return ordinal == that.ordinal;
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(ordinal);
    }

    /**
     * @param namespacedKey The name of this hold point
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSRegisterableHoldPoint(@NotNull ResourceLocation namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    /**
     * The name of this enum constant.
     *
     * @since 1.0.0
     */
    @NotNull final ResourceLocation namespacedKey;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public ResourceLocation getNamespacedKey() { return namespacedKey; }

}
