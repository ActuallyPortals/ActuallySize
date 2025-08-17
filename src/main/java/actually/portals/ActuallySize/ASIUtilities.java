package actually.portals.ActuallySize;

import actually.portals.ActuallySize.pickup.mixininterfaces.RenderNormalizable;
import actually.portals.ActuallySize.pehkui.ASIPehkuiCompatibility;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityVectors;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A collection of utility methods that are relevant
 * and often used when calculating size interactions
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIUtilities {

    /**
     * @param entity Entity of which you wish to know its effective practical size
     *
     * @return The practical size of this entity multiplied by its scale
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static double getEffectiveSize(@NotNull Entity entity) {
        return getEffectiveSize(entity, true);
    }

    /**
     * @param entity Entity of which you wish to know its effective practical size
     * @param functional See {@link #getEntityScale(Entity, boolean)} description
     *
     * @return The practical size of this entity multiplied by its scale
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static double getEffectiveSize(@NotNull Entity entity, boolean functional) {
        return getEntityScale(entity, functional) * practicalSize(entity);
    }

    /**
     * @param caster The POV entity, who is considered "normal size"
     * @param target The target entity who you are comparing against this
     *
     * @return If you are looking at a player 20x bigger than you, then '20'
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static double getRelativeScale(@NotNull Entity caster, @NotNull Entity target) {
        return getRelativeScale(caster, true, target, true);
    }

    /**
     * @param caster The POV entity, who is considered "normal size"
     * @param target The target entity who you are comparing against this
     *
     * @param functionalCaster See {@link #getEntityScale(Entity, boolean)} description
     * @param functionalTarget See {@link #getEntityScale(Entity, boolean)} description
     *
     * @return If you are looking at a player 20x bigger than you, then '20'
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static double getRelativeScale(@NotNull Entity caster, boolean functionalCaster, @NotNull Entity target, boolean functionalTarget) {
        return getEffectiveSize(target, functionalTarget) / getEffectiveSize(caster, functionalCaster);
    }

    /**
     * @param caster The POV entity, who is considered scaled
     * @param target The target entity who is considered "normal size"
     *
     * @return If you are 20x bigger than the target, then '20'
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static double inverseRelativeScale(@NotNull Entity caster, @NotNull Entity target) {
        return inverseRelativeScale(caster, true, target, true);
    }

    /**
     * @param caster The POV entity, who is considered scaled
     * @param target The target entity who is considered "normal size"
     *
     * @param functionalCaster See {@link #getEntityScale(Entity, boolean)} description
     * @param functionalTarget See {@link #getEntityScale(Entity, boolean)} description
     *
     * @return If you are 20x bigger than the target, then '20'
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static double inverseRelativeScale(@NotNull Entity caster, boolean functionalCaster, @NotNull Entity target, boolean functionalTarget) {
        return getEffectiveSize(caster, functionalCaster) / getEffectiveSize(target, functionalTarget);
    }

    /**
     * @param caster The POV entity, who is considered "normal size"
     * @param target The target entity who you are comparing against this
     * @param requiredScale The scale required of the caster to succeed in this check
     *
     * @return Suppose you are 10x bigger than the target, and the requirement is 5x, you succeed!
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static boolean meetsScaleRequirement(@NotNull Entity caster, @NotNull Entity target, double requiredScale) {
        return meetsScaleRequirement(caster, true, target, true, requiredScale);
    }

    /**
     * @param caster The POV entity, who is considered "normal size"
     * @param target The target entity who you are comparing against this
     * @param requiredScale The scale required of the caster to succeed in this check
     *
     * @param functionalCaster See {@link #getEntityScale(Entity, boolean)} description
     * @param functionalTarget See {@link #getEntityScale(Entity, boolean)} description
     *
     * @return Suppose you are 10x bigger than the target, and the requirement is 5x, you succeed!
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static boolean meetsScaleRequirement(@NotNull Entity caster, boolean functionalCaster, @NotNull Entity target, boolean functionalTarget, double requiredScale) {

        //ActuallySizeInteractions.Log("ASI GRS Target Scale: " + ASIPehkuiCompatibility.GetEntityScale(target));
        //ActuallySizeInteractions.Log("ASI GRS Target Practical: " + practicalSize(target));
        //ActuallySizeInteractions.Log("ASI GRS Caster Scale: " + ASIPehkuiCompatibility.GetEntityScale(caster));
        //ActuallySizeInteractions.Log("ASI GRS Caster Practical: " + practicalSize(caster));

        // Succeed if you meet the required scale
        return inverseRelativeScale(caster, functionalCaster, target, functionalTarget) >= requiredScale;
    }

    /**
     * This is the squared size of the diagonal of the hitbox of a player.
     * <p><p>
     * It is used to measure the practical sizes of other mobs,
     * for example chickens are like 0.4x0.4x0.3 block which makes
     * them 30% the size of players = As a player, you are already
     * a good 3x bigger than a chicken just by existing.
     *
     * @since 1.0.0
     */
    public static double PRACTICAL_SIZE = 4.52D;

    /**
     * @param mob Mob to measure their practical size
     *
     * @return Their size, per se, compared to a Player's size.
     *         In essence, this method will tell you chickens
     *         are small while the ender dragon is large.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static double practicalSize(@NotNull Entity mob) {

        // If the server config option is enabled, the practical size is one
        if (!ActuallyServerConfig.usePracticalSize) { return 1D; }
        if (mob instanceof Player) { return 1D; }

        // As a basic calculation, it is the diagonal of their hitbox divided by the square root of three (real)
        EntityType<?> type = mob.getType();
        double entityPractical = (type.getDimensions().width * type.getDimensions().width * 2) + (type.getDimensions().height * type.getDimensions().height);

        // It is measured relative to the player
        return Math.sqrt(entityPractical / PRACTICAL_SIZE);
    }

    /**
     * @param entity The entity which scaling factor you seek
     *
     * @return The scaling factor, presumably Pehkui mod, of this entity
     *
     * @since 1.0.0
     * @author Actually Portals
     *
     * @see #getEntityScale(Entity, boolean)
     */
    public static double getEntityScale(@NotNull Entity entity) {
        return getEntityScale(entity, true);
    }

    /**
     * Pehkui, for example, changes the effective scale of the player to 1.0
     * every time it is rendered in the inventory, and only during this instant.
     * With this, the player is not rendered huge or small in the inventory screen.
     * <p>
     * However, ASI does calculations while the inventory is open, and it is important
     * to know the true scale of the player, not its "functional" scale for this instant.
     * Sure, displaying the player at scale 1 is functional for the render system, but it
     * throws off other ASI calculations that need to know the scale the player is
     * supposed to be in the world.
     *
     * @param entity The entity which scaling factor you seek.
     *
     * @param functional True by default, if we leave up to the scale provider
     *                   to decide which scale to return at the moment, false
     *                   will still use the scale provider but a cache made by
     *                   ASI of the true scale of the entity regardless of
     *                   immediate circumstances.
     *
     * @return The scaling factor, presumably Pehkui mod, of this entity.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static double getEntityScale(@NotNull Entity entity, boolean functional) {

        if (!functional) {
            /*
             * The definition of being "Render-Normalized" is when the
             * scale provider (originally Pehkui) is returning a functional
             * value that masks the real one.
             *
             * Pehkui does so when displaying the player in the inventory
             * screen, there where you equip your armor, at scale 1 no matter
             * how big or tiny you are.
             *
             * As far as ASI is concerned though, sometimes we want to know the true
             * entity scale value not the simulated one to display in inventory.
             */
            RenderNormalizable rend = (RenderNormalizable) entity;
            if (rend.actuallysize$isScaleNormalized()) {
                return rend.actuallysize$getPreNormalizedScale(); }
        }

        return ASIPehkuiCompatibility.GetEntityScale(entity);
    }

    /**
     * Changes the scale of the target entity using the scale
     * provider (presumably Pehkui mod) that is currently in use.
     *
     * @param entity The entity to resize
     * @param scale The scale to give it
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void setEntityScale(@NotNull Entity entity, double scale) {
        ASIPehkuiCompatibility.SetEntityScaleInstant(entity, scale);
    }

    /**
     * @param entity The entity which interaction range you seek
     *
     * @return The reach of the entity by which they may perform various actions
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static double getInteractionRange(@NotNull Entity entity) {
        if (entity instanceof Player) { return getInteractionRange((Player) entity); }
        return 4 * getEntityScale(entity, false);
    }

    /**
     * @param player The player which interaction range you seek
     *
     * @return The reach of the player by which they may perform various actions
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static double getInteractionRange(@NotNull Player player) {
        return 4 * getEntityScale(player, false);
    }

    /**
     * @param caster The POV entity who wants to interact with another
     * @param target The target entity being interacted with
     *
     * @return If it is reasonable for these two to interact with each other
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static boolean inInteractionRange(@NotNull Entity caster, @NotNull Entity target) {

        // Different world? Out of range!
        if (!caster.level().equals(target.level())) { return false; }

        // Calculate the range, that of the caster
        double polishedRange = ASIUtilities.getInteractionRange(caster);

        // Add half the diagonal of the bounding box of the target
        AABB box = target.getBoundingBox();
        polishedRange += 0.5*Math.sqrt(
                ((box.maxX-box.minX)*(box.maxX-box.minX)) +
                ((box.maxY-box.minY)*(box.maxY-box.minY)) +
                ((box.maxZ-box.minZ)*(box.maxZ-box.minZ))
                                      );

        // Use that range
        return OotilityVectors.inRange(caster.position(), target.position(), polishedRange);
    }

    /**
     * A simple and basic method to debug show a 3D point in the world
     *
     * @param level World to test particles at
     * @param pos Position to test particles at
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void particles(@Nullable Level level, @NotNull Vec3 pos) {
        if (!(level instanceof ServerLevel)) { return; }
        ServerLevel world = (ServerLevel) level;
        world.sendParticles(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, 10, 0, 0, 0, 0.2);
    }

    /**
     * Suppose being punched, the smaller you are the more damage.
     * <br> (1) It tends to 1 when the relative scale tends to 1
     * <br> (2) It increases the smaller you are = the closer the relative scale gets to zero
     * <br> (3) It decreases the bigger you are = the closer the relative scale gets to infinity
     *
     * @param relativeScale My scale relative to the other, essentially, if I am double their size then 2
     * @param asymptoteBuff The maximum multiplier obtained when passing a relative scale of zero = being infinitely smaller, and maximally nerfed
     * @param asymptoteNerf The minimum multiplier obtained when passing a relative scale of infinity = being maximally buffed
     *
     * @return An amplification factor buffing beegs and nerfing tinies, within reason.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static double beegBalanceResist(double relativeScale, double asymptoteBuff, double asymptoteNerf) {

        // When beeg
        if (relativeScale > 1) {

            double gaussian = OotilityNumbers.gaussian(1, 3, relativeScale);
            return gaussian * (1 - asymptoteNerf) + asymptoteNerf;

        // When smol
        } else if (relativeScale < 1) {

            // Prepare polynomials
            double m = asymptoteBuff - 1;
            double p = relativeScale - 1;
            double p4 = p * p * p * p;
            double p12 = p4 * p4 * p4;

            // Calculate polynomials
            double E4 = m * p4 + 1;
            double E12 = m * p12 + 1;

            /*
             * Linear combination, the fourth degree polynomial
             * contributes a bit in small scale differences, while
             * the twelve degree polynomial is a sharp increase in
             * high size difference.
             */
            return (0.5 * E4) + (0.5 * E12);

        // Same size? no change
        } else { return 1; }
    }

    /**
     * Suppose being punched, the bigger you are the less damage.
     * <br> (1) It tends to 1 when the relative scale tends to 1
     * <br> (2) It decreases the smaller you are = the closer the relative scale gets to zero
     * <br> (3) It increases the bigger you are = the closer the relative scale gets to infinity
     *
     * @param relativeScale My scale relative to the other, essentially, if I am double their size then 2
     * @param asymptoteBuff The maximum multiplier obtained when passing a relative scale of infinity = being maximally buffed
     * @param asymptoteNerf The minimum multiplier obtained when passing a relative scale of zero = being infinitely smaller, and maximally nerfed
     *
     * @return An amplification factor buffing beegs and nerfing tinies, within reason.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static double beegBalanceEnhance(double relativeScale, double asymptoteBuff, double asymptoteNerf) {

        // When beeg
        if (relativeScale > 1) {

            double gaussian = OotilityNumbers.gaussianRev(1, 3, relativeScale);
            return gaussian * (asymptoteBuff - 1) + 1;

        // When smol
        } else if (relativeScale < 1) {

            // Prepare polynomials
            double m = asymptoteNerf - 1;
            double p = relativeScale - 1;
            double p4 = p * p * p * p;
            double p12 = p4 * p4 * p4;

            // Calculate polynomials
            double E4 = m * p4 + 1;
            double E12 = m * p12 + 1;

            /*
             * Linear combination, the fourth degree polynomial
             * contributes a bit in small scale differences, while
             * the twelve degree polynomial is a sharp increase in
             * high size difference.
             */
            return (0.5 * E4) + (0.5 * E12);

        // Same size? no change
        } else { return 1; }
    }
}
