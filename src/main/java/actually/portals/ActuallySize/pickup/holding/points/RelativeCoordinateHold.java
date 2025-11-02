package actually.portals.ActuallySize.pickup.holding.points;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.pickup.holding.pose.smol.ASIPSTinyPoseProfile;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityVectors;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.SVFLBit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A hold point that puts the held entity at a specific set of
 * coordinates relative to the holder's facing direction.
 *
 * @author Actually Portals
 * @since 1.0.0
 */
public abstract class RelativeCoordinateHold extends ASIPSImplementedHoldPoint {

    /**
     * A ride point relative to the holder's pitch, yaw, and world coordinates.
     *
     * @param nk The namespaced key to name this slot
     * @param svf The SVF coordinate information
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public RelativeCoordinateHold(@NotNull ResourceLocation nk, @Nullable ASIPSTinyPoseProfile tinyPose, @NotNull SVFLBit svf) {
        super(nk, tinyPose);
        this.coordinates = svf;
    }

    /**
     * The relative coordinate information for this hold
     *
     * @since 1.0.0
     */
    @NotNull final SVFLBit coordinates;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public @NotNull SVFLBit getCoordinates() { return coordinates; }

    /**
     * The transformation applied to the origin of this hold point, if it
     * were to depend on the holder or held entity for whatever reason.
     *
     * @param holder The entity doing the holder
     * @param entityDuality The entity being held
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public @NotNull SVFLBit getCoordinates(@NotNull Entity holder, @NotNull EntityDualityCounterpart entityDuality) { return getCoordinates(); }

    /**
     * @param holder The entity doing the holder
     * @param entityDuality The entity being held
     *
     * @return The origin for this relative coordinate hold
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public abstract Vec3 getOrigin(@NotNull Entity holder, @NotNull EntityDualityCounterpart entityDuality);

    /**
     * @param holder The entity doing the holder
     * @param entityDuality The entity being held
     *
     * @return The angle to the side axis, in radians, where 0° is completely horizontal and 90° is completely downward
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public abstract double getPitch(@NotNull Entity holder, @NotNull EntityDualityCounterpart entityDuality);

    /**
     * @param holder The entity doing the holder
     * @param entityDuality The entity being held
     *
     * @return The angle around vertical axis, in radians, where 0° is along positive forward and 270° is along positive side
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public abstract double getYaw(@NotNull Entity holder, @NotNull EntityDualityCounterpart entityDuality);

    /**
     * @param holder The entity doing the holder
     * @param entityDuality The entity being held
     *
     * @return The angle around forward axis, in radians, where 0° makes the side vector completely flat.
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public double getRoll(@NotNull Entity holder, @NotNull EntityDualityCounterpart entityDuality) { return 0; }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public void serversidePositionHeldEntity(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityDuality) {

        // This slot only supports entities to be holders
        if (!(holder instanceof Entity)) { entityDuality.actuallysize$escapeDuality(); return; }

        // First identify the parts
        Entity entityCounterpart = (Entity) entityDuality;
        Entity holderEntity = (Entity) holder;
        double size = ASIUtilities.getEntityScale(holderEntity);
        Vec3 svf;

        // Transformation relative to origin
        SVFLBit bit = getCoordinates(holderEntity, entityDuality);
        if (!bit.isZero()) {

            // Roll operation support (funny)
            double roll = getRoll(holderEntity, entityDuality);
            double rolledSide = bit.getS() * Math.cos(roll) - bit.getV() * Math.sin(roll);
            double rolledVertical = bit.getV() * Math.cos(roll) + bit.getS() * Math.sin(roll);

            // Transform S V F L scaled by the holders' scale
            svf = OotilityVectors.transformSVFL(getPitch(holderEntity, entityDuality), getYaw(holderEntity, entityDuality),
                    rolledSide, rolledVertical, bit.getF(), bit.getL(),
                    bit.getX(), bit.getY(), bit.getZ()).scale(size);

        // No need to apply any transformation
        } else { svf = Vec3.ZERO; }

        // Freeze velocity and set position to the calculated offsets
        entityCounterpart.setDeltaMovement(Vec3.ZERO);
        Vec3 origin = getOrigin(holderEntity, entityDuality);
        Vec3 result = new Vec3(origin.x() + svf.x(), origin.y() + svf.y(), origin.z() + svf.z());

        /*/ Cap distance and sanitize
        double distance = result.distanceToSqr(holderEntity.position());
        double max = 3;
        if (size > 1) { max *= size; }
        if (distance > (max * max)) { result = result.normalize().scale(max); }
        if (Double.isNaN(result.x) || Double.isNaN(result.y) || Double.isNaN(result.z)) { result = holderEntity.position(); }   //*/

        entityCounterpart.setPos(result);
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public void throwHeldEntity(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityDuality) {

        // This slot only supports entities to be holders
        if (!(holder instanceof Entity)) { entityDuality.actuallysize$escapeDuality(); return; }

        // Position it where normal
        serversidePositionHeldEntity(holder, entityDuality);

        // First identify the parts
        Entity entityCounterpart = (Entity) entityDuality;
        Entity holderEntity = (Entity) holder;
        double beegScale = ASIUtilities.getEffectiveSize(holderEntity);
        double tinyScale = ASIUtilities.getEffectiveSize(entityCounterpart);
        double gauss = OotilityNumbers.gaussianRev(0, 2, (beegScale / tinyScale));
        double strength = 3.8 * ASIUtilities.beegBalanceEnhance(beegScale, 3, 0.1);

        /*
         * Full strength at relative size 5x and forth (asymptotic)
         * Half strength at about 2.5x relative size
         * Same size is a tenth of the strength
         *
         * Strength is 0.8 at base scale, about 4 at 10x scale,
         * but then it tapers off so that it is 10 at 100x scale
         * and asymptotic never exceeding 10
         */
        //THR//ActuallySizeInteractions.Log("ASI &6 HDA [" + getNamespacedKey() + "] &7 Thrown at &b " + strength + " STR &f x &3 " + gauss + " = " + (gauss * strength) + ", SZ " + (beegScale / tinyScale));

        // Add forward force
        entityCounterpart.setDeltaMovement(OotilityVectors.entityForward(holderEntity).scale(gauss * strength));
    }
}
