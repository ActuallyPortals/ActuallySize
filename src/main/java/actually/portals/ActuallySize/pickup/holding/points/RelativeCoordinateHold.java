package actually.portals.ActuallySize.pickup.holding.points;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityVectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * A hold point that puts the held entity at a specific set of
 * coordinates relative to the holder's facing direction.
 *
 * @author Actually Portals
 * @since 1.0.0
 */
public abstract class RelativeCoordinateHold extends ASIPSRegisterableHoldPoint {

    /**
     * Creates a ride point with a specific relative transformation.
     *
     * @param nk The namespaced key to name this slot
     * @param sideOffset Relative sideways offset
     * @param verticalOffset Relative vertical offset
     * @param forwardOffset Relative forward offset
     * @param xOffset Absolute X offset
     * @param yOffset Absolute Y offset
     * @param zOffset Absolute Z offset
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public RelativeCoordinateHold(@NotNull ResourceLocation nk, double sideOffset, double verticalOffset, double forwardOffset, double xOffset, double yOffset, double zOffset) {
        super(nk);
        this.verticalOffset = verticalOffset;
        this.forwardOffset = forwardOffset;
        this.sideOffset = sideOffset;
        this.xOffset = xOffset;
        this.zOffset = zOffset;
        this.yOffset = yOffset;
    }

    /**
     * Creates a ride point with a specific relative transformation.
     *
     * @param nk The namespaced key to name this slot
     * @param sideOffset Relative sideways offset
     * @param verticalOffset Relative vertical offset
     * @param forwardOffset Relative forward offset
     * @param levelOffset Relative forward offset with no vertical component (along the Y axis)
     * @param xOffset Absolute X offset
     * @param yOffset Absolute Y offset
     * @param zOffset Absolute Z offset
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public RelativeCoordinateHold(@NotNull ResourceLocation nk, double sideOffset, double verticalOffset, double forwardOffset, double levelOffset, double xOffset, double yOffset, double zOffset) {
        super(nk);
        this.verticalOffset = verticalOffset;
        this.forwardOffset = forwardOffset;
        this.sideOffset = sideOffset;
        this.levelOffset = levelOffset;
        this.xOffset = xOffset;
        this.zOffset = zOffset;
        this.yOffset = yOffset;
    }

    /**
     * The offset in the direction the holder is facing
     *
     * @since 1.0.0
     */
    double forwardOffset;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public double getForwardOffset() { return forwardOffset; }

    /**
     * The offset in the direction upward relative to the holder
     *
     * @since 1.0.0
     */
    double verticalOffset;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public double getVerticalOffset() { return verticalOffset; }

    /**
     * The offset in the direction sideways relative to the facing
     * direction where a positive number is leftward (probably).
     *
     * @since 1.0.0
     */
    double sideOffset;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public double getSideOffset() { return sideOffset; }

    /**
     * The offset in the direction forward relative to the facing
     * direction, except it has no vertical component.
     *
     * @since 1.0.0
     */
    double levelOffset;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public double getLevelOffset() { return levelOffset; }

    /**
     * The offset in absolute X direction
     *
     * @since 1.0.0
     */
    double xOffset;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public double getXOffset() { return xOffset; }

    /**
     * The offset in absolute Y direction
     *
     * @since 1.0.0
     */
    double yOffset;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public double getYOffset() { return yOffset; }

    /**
     * The offset in the absolute Z direction
     *
     * @since 1.0.0
     */
    double zOffset;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public double getZOffset() { return zOffset; }

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

        // Roll operation support (funny)
        double roll = getRoll(holderEntity, entityDuality);
        double rolledSide = getSideOffset() * Math.cos(roll) - getVerticalOffset() * Math.sin(roll);
        double rolledVertical = getVerticalOffset() * Math.cos(roll) + getSideOffset() * Math.sin(roll);

        // Transform S V F L scaled by the holders' scale
        Vec3 svf = OotilityVectors.transformSVFL(getPitch(holderEntity, entityDuality), getYaw(holderEntity, entityDuality),
                        rolledSide, rolledVertical, getForwardOffset(), getLevelOffset(),
                        getXOffset(), getYOffset(), getZOffset()).scale(size);

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
