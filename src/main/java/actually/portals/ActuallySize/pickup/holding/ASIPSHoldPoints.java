package actually.portals.ActuallySize.pickup.holding;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.pickup.holding.model.ASIPSModelPartHoldPoint;
import actually.portals.ActuallySize.pickup.holding.model.vanilla.ASIMPLForwardNZVerticalNY;
import actually.portals.ActuallySize.pickup.holding.model.vanilla.ASIMPLForwardYVerticalNZ;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSRegisterableHoldPoint;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSRelativeFacingHold;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.SVFLBit;
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
     * A hold point that positions the held entity in front of you
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint PINCH =  new ASIPSRelativeFacingHold(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "pinch"),
            new SVFLBit(-0.1, 0, 0.2, 0.2, 0, 1.6, 0));

    /**
     * A hold point that positions the held entity in the mainhand of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint MAINHAND =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "mainhand"),
            new SVFLBit(0D/16D, -10D/16D, 1.5D/16D),
            new SVFLBit(-0.4, 0, 0, 0.2, 0, 1.2, 0),
            new ASIMPLForwardNZVerticalNY("right_arm"));

    /**
     * A hold point that positions the held entity in the offhand of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint OFFHAND =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "offhand"),
            new SVFLBit(0D/16D, -10D/16D, 1.5D/16D),
            new SVFLBit(0.4, 0, 0, 0.2, 0, 1.2, 0),
            new ASIMPLForwardNZVerticalNY("left_arm"));

    /**
     * A hold point that positions the held entity in the head of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint HAT =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "hat"),
            new SVFLBit(0D/16D, 7D/16D, 0D/16D),
            new SVFLBit(0, 0, 0, 0, 0, 1.8, 0),
            new ASIMPLForwardNZVerticalNY("head"));

    /**
     * A hold point that positions the held entity in the head of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint HEAD =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "head"),
            new SVFLBit(0D/16D, 7D/16D, 3D/16D),
            new SVFLBit(0, 0, 0, 0.1, 0, 1.8, 0),
            new ASIMPLForwardNZVerticalNY("head"));

    /**
     * A hold point that positions the held entity in the right shoulder of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint RIGHT_SHOULDER =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "right_shoulder"),
            new SVFLBit(-4.5D/16D, 0D/16D, 0.5D/16D),
            new SVFLBit(-0.3, 0, 0, 0, 0, 1.4, 0),
            new ASIMPLForwardNZVerticalNY("body"));

    /**
     * A hold point that positions the held entity in the left shoulder of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint LEFT_SHOULDER =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "left_shoulder"),
            new SVFLBit(4.5D/16D, 0D/16D, 0.5D/16D),
            new SVFLBit(0.3, 0, 0, 0, 0, 1.4, 0),
            new ASIMPLForwardNZVerticalNY("body"));

    /**
     * A hold point that positions the held entity in the right pocket of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint RIGHT_POCKET =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "right_pocket"),
            new SVFLBit(-3.5D/16D, -11D/16D, 0D/16D),
            new SVFLBit(-0.3, 0, 0, 0, 0, 1.0, 0),
            new ASIMPLForwardNZVerticalNY("body"));

    /**
     * A hold point that positions the held entity in the left pocket of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint LEFT_POCKET =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "left_pocket"),
            new SVFLBit(3.5D/16D, -11D/16D, 0D/16D),
            new SVFLBit(0.3, 0, 0, 0, 0, 1.0, 0),
            new ASIMPLForwardNZVerticalNY("body"));

    /**
     * A hold point that positions the held entity in the right thigh of the player model,
     * essentially the right pocket but just a little lower so that it swings when you walk
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint RIGHT_THIGH =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "right_thigh"),
            new SVFLBit(-1.5D/16D, -3D/16D, 0D/16D),
            new SVFLBit(-0.3, 0, 0, 0, 0, 0.7, 0),
            new ASIMPLForwardNZVerticalNY("right_leg"));

    /**
     * A hold point that positions the held entity in the left thigh of the player model,
     * essentially the left pocket but just a little lower so that it swings when you walk
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint LEFT_THIGH =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "left_thigh"),
            new SVFLBit(1.5D/16D, -3D/16D, 0D/16D),
            new SVFLBit(0.3, 0, 0, 0, 0, 0.7, 0),
            new ASIMPLForwardNZVerticalNY("left_leg"));

    /**
     * A hold point that positions the held entity in the right foot of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint RIGHT_BOOT =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "right_boot"),
            new SVFLBit(0D/16D, -11.5D/16D, 0D/16D),
            new SVFLBit(-0.15, 0, 0, 0, 0, 0.1, 0),
            new ASIMPLForwardNZVerticalNY("right_leg"));

    /**
     * A hold point that positions the held entity in the left foot of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint LEFT_BOOT =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "left_boot"),
            new SVFLBit(0D/16D, -11.5D/16D, 0D/16D),
            new SVFLBit(0.15, 0, 0, 0, 0, 0.1, 0),
            new ASIMPLForwardNZVerticalNY("left_leg"));


    /**
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint FLUSH =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "flush"),
            new SVFLBit(0D/16D, -13D/16D, 1D/16D),
            new SVFLBit(0, 0, 0, 0, 0, 1, 0),
            new ASIMPLForwardNZVerticalNY("body"));

    /**
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint SHED =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "shed"),
            new SVFLBit(0D/16D, -3.5D/16D, 2.5D/16D),
            new SVFLBit(0, 0, 0, 0.1, 0, 1.25, 0),
            new ASIMPLForwardNZVerticalNY("body"));

    /**
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint NOMF =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "nomf"),
            new SVFLBit(0D/16D, 0D/16D, 2.5D/16D),
            new SVFLBit(0, 0, 0, 0.07, 0, 1.4, 0),
            new ASIMPLForwardNZVerticalNY("head"));

    //endregion
}
