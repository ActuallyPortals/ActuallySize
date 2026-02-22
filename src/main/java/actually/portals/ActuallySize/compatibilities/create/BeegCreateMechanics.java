package actually.portals.ActuallySize.compatibilities.create;

import actually.portals.ActuallySize.ASIUtilities;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A set of options that affect Create Kinetic
 * blocks when they are affected by giants.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class BeegCreateMechanics {

    /**
     * @param beeg The giant interacting with this kinetic component
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public BeegCreateMechanics(@NotNull Player beeg) {
        this.beeg = beeg;
        size = ASIUtilities.getEffectiveSize(beeg);
        stressCapacityMultiplier = 1;
        speedMultiplier = 1;
    }

    /**
     * The giant interacting with this kinetic component
     *
     * @since 1.0.0
     */
    @NotNull Player beeg;

    /**
     * The size of the giant interacting with this kinetic component
     *
     * @since 1.0.0
     */
    double size;

    /**
     * The speed multiplier resulting from size influence
     *
     * @since 1.0.0
     */
    double speedMultiplier;

    /**
     * The power multiplier resulting from size influence
     *
     * @since 1.0.0
     */
    double stressCapacityMultiplier;

    /**
     * The power adding resulting from size influence,
     * after having scaled the original power by
     * the stress capacity multiplier.
     *
     * @since 1.0.0
     */
    double addedStressCapacity;

    /**
     * The speed increase resulting from size influence,
     * after having scaled the original speed by
     * the speed multiplier.
     *
     * @since 1.0.0
     */
    double addedSpeed;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public double getStressCapacityMultiplier() { return stressCapacityMultiplier; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public double getSpeedMultiplier() { return speedMultiplier; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public double getSize() { return size; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @NotNull Player getBeeg() { return beeg; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public void setSize(double size) { this.size = size; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public void setSpeedMultiplier(double speedMultiplier) { this.speedMultiplier = speedMultiplier; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public void setStressCapacityMultiplier(double stressCapacityMultiplier) { this.stressCapacityMultiplier = stressCapacityMultiplier; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public double getAddedStressCapacity() { return addedStressCapacity; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public void setAddedStressCapacity(double addedStressCapacity) { this.addedStressCapacity = addedStressCapacity; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public double getAddedSpeed() { return addedSpeed; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public void setAddedSpeed(double addedSpeed) { this.addedSpeed = addedSpeed; }


    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public String toString() { return "S[x" + getSpeedMultiplier() + ", +" + getAddedSpeed() + "]-C[x" + getStressCapacityMultiplier() + ". +" + getAddedStressCapacity() + "]"; }

    /**
     * @param originalSpeed The original speed value
     * @return The result of adding these speeds together
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public float adjustSpeed(float originalSpeed) {
        double multiplicity = originalSpeed * getSpeedMultiplier();
        double added = getAddedSpeed();
        if (added == 0) { return (float) multiplicity; }

        // Add normally to choose rotation direction
        int sign = 1;
        if (multiplicity + added < 0) { sign = -1; }

        // When they have the same sign, they add through the sum of their roots
        if (multiplicity * added > 0) {
            return (float) (sign * Math.sqrt(multiplicity*multiplicity + added*added));

        // When their signs oppose, decrease the greatest in similar fashion
        } else {
            return (float) (sign * Math.sqrt(Math.abs(added*added - multiplicity*multiplicity)));
        }
    }

    /**
     * @param originalStress The original stress capacity value
     * @return The result of adding these stresses together
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public double adjustStressCapacity(double originalStress) {

        // Simple combination
        double multiplicity = originalStress * getStressCapacityMultiplier();
        return multiplicity + getAddedStressCapacity();
    }
}
