package actually.portals.ActuallySize.pickup.holding.points;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.pickup.holding.pose.ASIPSTinyPosedHold;
import actually.portals.ActuallySize.pickup.holding.pose.smol.ASIPSTinyPoseProfile;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;

/**
 * The standard ASI implementation of hold points, with all the
 * capabilities ASI is expected to provide of them.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public abstract class ASIPSImplementedHoldPoint extends ASIPSRegisterableHoldPoint implements ASIPSTinyPosedHold {

    /**
     * @param namespacedKey The name of this hold point
     * @author Actually Portals
     *
     * @param tinyPose The pose of players held in this hold point
     *
     * @since 1.0.0
     */
    public ASIPSImplementedHoldPoint(@NotNull ResourceLocation namespacedKey, @Nullable ASIPSTinyPoseProfile tinyPose) {
        super(namespacedKey);
        this.tinyPose = tinyPose;
    }

    /**
     * Sets a required number of struggles over a 60
     * tick span to escape.
     * <br><br>
     * The theoretical maximum for a TAS is 30, one
     * every other tick, but for a HACKER it is still
     * 60 one every tick. Then, a value of 61 makes
     * this unescape-able, while a value of 0 makes this
     * instantly escape-able.
     *
     *
     * @since 1.0.0
     */
    int withStruggleReq = 0;

    /**
     * @param req The number of struggles required to escape.
     *            Set to a number greater than 60 to prevent
     *            any escape, and to 1 for instant dismount.
     *            <br><br>
     *            Creative mode players' struggles are 5x more
     *            effective by default, and they also scale slightly
     *            with the size of the tiny so bigger tinies can
     *            escape more easily. Basically they may count for
     *            a bit more than one struggle every tick.
     *
     * @return this
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public ASIPSImplementedHoldPoint withStruggleRequirement(int req) { withStruggleReq = req; return this; }

    /**
     * The maximum number of ticks that may elapse from the
     * first to the last tick of a struggle for the tiny
     * to break free. A smaller number makes it harder, and
     * at ZERO breaking free is disabled entirely.
     *
     *
     * @since 1.0.0
     */
    int withStruggleTres = 0;

    /**
     * @param tres The maximum number of ticks that may elapse from the
     *             first to the last tick of a struggle for the tiny
     *             any escape, to a maximum of 60. Using ZERO disables
     *             any escape since no struggle is ever counted.
     *
     * @return this
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public ASIPSImplementedHoldPoint withStruggleThreshold(int tres) { withStruggleTres = tres; return this; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public boolean canBeEscapedByStruggling(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityDuality, ArrayList<Long> struggles) {
        //STG//ActuallySizeInteractions.Log("[" + getNamespacedKey() + "] Comparing STRUGGLE to &6 Threshold " + withStruggleTres + " &7 and &a Requirement " + withStruggleReq);
        if (withStruggleTres < 0.1) { return false; }
        if (withStruggleReq < 0.1) { return true; }

        // Calculate weight
        double w = 1 * ASIUtilities.beegBalanceEnhance(ASIUtilities.getRelativeScale((Entity) holder, (Entity) entityDuality) / canSustainScale, 3, 0.333);
        if (entityDuality instanceof Player) {
            if (((Player) entityDuality).isCreative()) {
                if (holder instanceof Player) {

                    // Creative mode players held by survival players gain 3x struggle benefit
                    if (!((Player) holder).isCreative()) { w *= 3; }

                // Creative mode players held by monsters gain 10x struggle benefit
                } else { w *= 10; }
            }
        }

        // Survival player struggles are half as effective when held by creative mode players
        if (holder instanceof Player) {
            if (((Player) holder).isCreative()) {
                if (entityDuality instanceof Player) {
                    if (!((Player) entityDuality).isCreative()) {
                        w *= 0.5;
                    }
                }
            }
        }
        //STG//ActuallySizeInteractions.Log("Struggle Power: &e X" + w);

        HashMap<Long, Double> buffer = new HashMap<>();
        for (Long l : struggles) {
            //STG//ActuallySizeInteractions.Log("&8 ----- Struggle Pass -----");

            // Remove old entries of the buffer
            ArrayList<Long> keys = new ArrayList<>(buffer.keySet());
            for (Long b : keys) {
                if (l - b > withStruggleTres) { buffer.remove(b); } else {
                    //STG//ActuallySizeInteractions.Log("&8 + &f " + b);
                }
            }

            // Add the newest to the buffer
            buffer.put(l, 1D);
            //STG//ActuallySizeInteractions.Log("&8 + &f " + l);

            // Check the buffer size count to meet the threshold
            double strugglePower = buffer.size() * w;
            //STG//ActuallySizeInteractions.Log("&8 RESULT &b " + strugglePower);
            if (strugglePower > withStruggleReq) { return true; }
        }

        // The tiny could NOT escape
        return false;
    }

    /**
     * Allows the held player to ride something nearby and escape
     * that way, just like riding a boat can teleport you away.
     *
     * @since 1.0.0
     */
    boolean canRidingDismount;

    /**
     * @param dismount Allows the held player to ride something nearby and escape
     *                 that way, just like riding a boat can teleport you away.
     *
     * @return this
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public ASIPSImplementedHoldPoint withRidingDismount(boolean dismount) { canRidingDismount = dismount; return this; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public boolean canBeEscapedByRiding(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityDuality) { return canRidingDismount; }

    /**
     * Allows other beegs to grab this tiny from this slot
     *
     * @since 1.0.0
     */
    boolean withStealEscape;

    /**
     * @param escape Allows other beegs to grab this tiny from this slot
     *
     * @return this
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public ASIPSImplementedHoldPoint withStealLock(boolean escape) { withStealEscape = escape; return this; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public boolean canBeEscapedByStealing(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityDuality, @NotNull ItemEntityDualityHolder pickpocket) {

        // Either escape is enabled, or creative mode funny
        return withStealEscape || super.canBeEscapedByStealing(holder, entityDuality, pickpocket);
    }

    /**
     * Locks the tiny in the slot, preventing them from teleporting out
     *
     * @since 1.0.0
     */
    boolean withTeleportLock;

    /**
     * @param lock Locks the tiny in the slot, preventing them from teleporting out
     *
     * @return this
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public ASIPSImplementedHoldPoint withTeleportLock(boolean lock) { withTeleportLock = lock; return this; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public boolean canBeEscapedByTeleporting(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityDuality, @NotNull EntityTeleportEvent event) { return !withTeleportLock; }

    /**
     * Cosmetic option for some hold points that make held entities
     * display their fall animation while held in them
     *
     * @since 1.0.0
     */
    boolean canDangle;

    /**
     * @param dangle Cosmetic option for some hold points that make held entities
     *               display their fall animation while held in them
     *
     * @return this
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public ASIPSImplementedHoldPoint withDangling(boolean dangle) { canDangle = dangle; return this; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public boolean isDangling(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityDuality) { return canDangle; }

    /**
     * Enables the dismounting by gliding off with an elytra
     *
     * @since 1.0.0
     */
    boolean canElytraDismount;

    /**
     * @param dismount Enables the dismounting by gliding off with an elytra
     *
     * @return this
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public ASIPSImplementedHoldPoint withElytraDismount(boolean dismount) { canElytraDismount = dismount; return this; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public boolean canBeGlidedOff(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityDuality) { return canElytraDismount; }

    /**
     * The relative scale from holder to tiny that can be held in this slot,
     * by default 0.25x (which means, the holder must be 4x larger)
     *
     * @since 1.0.0
     */
    double canSustainScale = 0.25;

    /**
     * @param sustain The relative scale from holder to tiny that can be held in this slot,
     *                by default 0.25x (which means, the holder must be 4x larger)
     *
     * @return this
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public ASIPSImplementedHoldPoint withSustainScale(double sustain) { canSustainScale = sustain; return this; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public boolean canSustainHold(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityDuality) {
        return !ASIUtilities.meetsScaleRequirement((Entity) entityDuality, (Entity) holder, canSustainScale);
    }

    /**
     * The pose that players held in this hold point have
     *
     * @since 1.0.0
     */
    @Nullable ASIPSTinyPoseProfile tinyPose;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public @Nullable ASIPSTinyPoseProfile getTinyPose(@NotNull Player tiny) { return tinyPose; }
}
