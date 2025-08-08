package actually.portals.ActuallySize.pickup.holding;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSRegisterableHoldPoint;
import actually.portals.ActuallySize.pickup.holding.points.FacingRelativeHold;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * The ASI hold points behave like a glorified enum. You may treat these as an
 * enum, but also register your own if through a third-party plugin that wants
 * compatibility.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIPSHoldPoints {

    //region As Enum
    /**
     * A hold point that positions the held entity in the mainhand of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint MAINHAND =  new FacingRelativeHold(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "mainhand"),
            -0.3, 0, 0.3, 0.3, 0, 1.1, 0);

    /**
     * A hold point that positions the held entity in front of you
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint PINCH =  new FacingRelativeHold(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "pinch"),
            -0.1, 0, 0.2, 0.2, 0, 1.6, 0);

    /**
     * A hold point that positions the held entity in the offhand of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint OFFHAND =  new FacingRelativeHold(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "offhand"),
            0.3, 0, 0.3, 0.3, 0, 1.1, 0);

    /**
     * A hold point that positions the held entity in the head of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint HEAD =  new FacingRelativeHold(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "hat"),
            0, 0.5, 0, 0, 1.3, 0);

    /**
     * A hold point that positions the held entity in the right shoulder of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint RIGHT_SHOULDER =  new FacingRelativeHold(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "right_shoulder"),
            -0.3, 0, 0, 0, 1.3, 0);

    /**
     * A hold point that positions the held entity in the left pocket of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint LEFT_POCKET =  new FacingRelativeHold(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "left_pocket"),
            0.15, 0, 0, 0.2, 0, 0.7, 0);

    /**
     * A hold point that positions the held entity in the right foot of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint RIGHT_BOOT =  new FacingRelativeHold(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "right_boot"),
            -0.15, 0, 0, 0, 0.1, 0);
    //endregion
}
