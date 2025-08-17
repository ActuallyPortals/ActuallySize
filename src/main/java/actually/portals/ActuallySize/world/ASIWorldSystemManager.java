package actually.portals.ActuallySize.world;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallyServerConfig;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * The class that handles systems related to interacting
 * with the world as a beeg or a tiny.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIWorldSystemManager {

    /**
     * @param world The world where damage takes place
     * @param type The type of damage
     *
     * @return If this damage type is adjusted by ASI
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static boolean IsAdjustableDamage(@NotNull Level world, @NotNull DamageSource type) {

        // Fall is not affected by ASI
        if (type == world.damageSources().fall()) { return false; }
        if (type == world.damageSources().cramming()) { return false; }
        if (type == world.damageSources().drown()) { return false; }
        if (type == world.damageSources().starve()) { return false; }
        if (type == world.damageSources().fellOutOfWorld()) { return false; }
        if (type == world.damageSources().wither()) { return false; }
        if (type == world.damageSources().outOfBorder()) { return false; }
        if (type == world.damageSources().genericKill()) { return false; }

        // Everything else is
        return true;
    }

    /**
     * Based on the options in the config, this method will adjust the damage
     * dealt between one or two entities of different sizes. In general, beegs
     * take less damage and tinies take more damage.
     *
     * @param originalDamage The amount of damage
     * @param victim The receiver of damage
     * @param attack The attack information
     *
     * @return Damage, adjusted to account for matters of size.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static double ASICombatAdjust(double originalDamage, @NotNull LivingEntity victim, @NotNull DamageSource attack) {
        if (!IsAdjustableDamage(victim.level(), attack)) { return originalDamage; }
        if (!ActuallyServerConfig.strongBeegs && !ActuallyServerConfig.tankyBeegs && !ActuallyServerConfig.delicateTinies) { return originalDamage; }

        /*
         * Damage calculations use scale, not effective size. This
         * is because using effective size would make larger entities
         * that already deal more damage deal even more damage lol.
         */
        double mySize = ASIUtilities.getEntityScale(victim);
        double buffingLimit = ActuallyServerConfig.strongestBeeg;
        //ATT//ActuallySizeInteractions.Log("ASI &2 WSM &7 [" + victim.getScoreboardName() + "] Adjusting hurt &3 " + originalDamage + " &r up to &b " + buffingLimit + " &f at &e x" + mySize);

        /*
         * Is there an aggressive entity? Then we must bother
         * with their damage being amplified if they are bigger.
         */
        double aggressorScale = 0;
        double aggressorAmplificationFactor = 1;
        boolean aggressorIsCrouching;
        if (attack.getDirectEntity() != null) {
            aggressorScale = ASIUtilities.getEntityScale(attack.getDirectEntity());
            aggressorIsCrouching = (attack.getDirectEntity() instanceof Player) && attack.getDirectEntity().isCrouching();
            //ATT//ActuallySizeInteractions.Log("ASI &2 WSM &7 [" + attack.getDirectEntity().getScoreboardName() + "] Aggressor (" + aggressorIsCrouching + ") at &e x" + aggressorScale);

            if (aggressorScale > 0) {

                // Adjust damage based on the relative scale from aggressor to me
                aggressorAmplificationFactor = ASIUtilities.beegBalanceResist(mySize / aggressorScale, buffingLimit, 0);

                // If the beeg is crouching, and bigger than us, there is no amplification. Tinies remain reduced tho.
                if (aggressorIsCrouching && aggressorScale > mySize) { aggressorAmplificationFactor = 1; }
            }
            //ATT//ActuallySizeInteractions.Log("ASI &2 WSM &7 Aggressor Factor: &6 " + aggressorAmplificationFactor + " &r for a total &e " + (originalDamage * aggressorAmplificationFactor));

            // Combat takes precedence to the other stuff below
            return originalDamage * aggressorAmplificationFactor;
        }

        /*
         * Size matters
         */
        double sizeAmplificationFactor = 1;

        // When beeg
        if (mySize > 1 && !ActuallyServerConfig.tankyBeegs) {
            //ATT//ActuallySizeInteractions.Log("ASI &2 WSM &7 Tanky beeg");

            // Reduce damage from all sources
            sizeAmplificationFactor = ASIUtilities.beegBalanceResist(mySize, buffingLimit, 0);

        // When smol
        } else if (mySize < 1 && ActuallyServerConfig.delicateTinies) {
            //ATT//ActuallySizeInteractions.Log("ASI &2 WSM &7 Delicate smol");

            // Increase damage from all sources
            sizeAmplificationFactor = ASIUtilities.beegBalanceResist(mySize, buffingLimit, 0);
        }
        //ATT//ActuallySizeInteractions.Log("ASI &2 WSM &7 Size Factor: &6 " + sizeAmplificationFactor + " &r for a total &e " + (originalDamage * sizeAmplificationFactor));

        // Adjust effect
        return originalDamage * sizeAmplificationFactor;
    }
}
