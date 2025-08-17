package actually.portals.ActuallySize.pickup.holding.points;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityVectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * A hold point that puts the held entity at a specific set of
 * coordinates relative to the holder's facing direction.
 *
 * @author Actually Portals
 * @since 1.0.0
 */
public class FacingRelativeHold extends ASIPSRegisterableHoldPoint implements ASIPSHoldPoint {

    /**
     * Creates a ride point that targets a specific
     * transformation relative to the holder's facing.
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
    public FacingRelativeHold(@NotNull ResourceLocation nk, double sideOffset, double verticalOffset, double forwardOffset, double xOffset, double yOffset, double zOffset) {
        super(nk);
        this.verticalOffset = verticalOffset;
        this.forwardOffset = forwardOffset;
        this.sideOffset = sideOffset;
        this.xOffset = xOffset;
        this.zOffset = zOffset;
        this.yOffset = yOffset;
    }

    /**
     * Creates a ride point that targets a specific
     * transformation relative to the holder's facing.
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
    public FacingRelativeHold(@NotNull ResourceLocation nk, double sideOffset, double verticalOffset, double forwardOffset, double levelOffset, double xOffset, double yOffset, double zOffset) {
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
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public void positionHeldEntity(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityDuality) {

        // This slot only supports entities to be holders
        if (!(holder instanceof Entity)) { entityDuality.actuallysize$escapeDuality(); return; }

        // First identify the parts
        Entity entityCounterpart = (Entity) entityDuality;
        Entity holderEntity = (Entity) holder;

        // Transform F V S L scaled by the holders' scale
        Vec3 svf = OotilityVectors.entityTransformSVFL(holderEntity,
                getSideOffset(), getVerticalOffset(), getForwardOffset(), getLevelOffset(),
                getXOffset(), getYOffset(), getZOffset())
                .scale(ASIUtilities.getEntityScale(holderEntity));

        // Freeze velocity and set position to the calculated offsets
        entityCounterpart.setDeltaMovement(Vec3.ZERO);
        entityCounterpart.setPos(new Vec3(holderEntity.getX() + svf.x(), holderEntity.getY() + svf.y(), holderEntity.getZ() + svf.z()));
    }

    @Override
    public void throwHeldEntity(@NotNull ItemEntityDualityHolder holder, @NotNull EntityDualityCounterpart entityDuality) {

        // This slot only supports entities to be holders
        if (!(holder instanceof Entity)) { entityDuality.actuallysize$escapeDuality(); return; }

        // Position it where normal
        positionHeldEntity(holder, entityDuality);

        // First identify the parts
        Entity entityCounterpart = (Entity) entityDuality;
        Entity holderEntity = (Entity) holder;
        double beegScale = ASIUtilities.getEffectiveSize(holderEntity);
        double tinyScale = ASIUtilities.getEffectiveSize(entityCounterpart);
        double gauss = OotilityNumbers.gaussianRev(0, 2, (beegScale / tinyScale));
        double strength = 3.8 * ASIUtilities.beegBalanceEnhance(beegScale, 5, 0.1);

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
