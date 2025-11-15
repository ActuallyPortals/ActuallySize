package actually.portals.ActuallySize.pickup.holding;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.pickup.holding.model.ASIPSModelPartHoldPoint;
import actually.portals.ActuallySize.pickup.holding.pose.beeg.ASIPSItemPoserHold;
import actually.portals.ActuallySize.pickup.holding.model.vanilla.ASIMPLForwardNZVerticalNY;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSRegisterableHoldPoint;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSRelativeFacingHold;
import actually.portals.ActuallySize.pickup.holding.pose.smol.ASIPSVanillaPoseProfile;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.SVFLBit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Pose;
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

    //region Internal
    /**
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint INTERNAL_HEAD =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "i_head"),
            new ASIPSVanillaPoseProfile(Pose.SITTING, Pose.CROUCHING),
            new SVFLBit(0D/16D, 5.8D/16D, -1D/16D),
            new SVFLBit(0, 0, 0, 0.1, 0, 1.8, 0),
            new ASIMPLForwardNZVerticalNY("head"));
    /**
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint INTERNAL_CHEST =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "i_chest"),
            new ASIPSVanillaPoseProfile(Pose.STANDING, Pose.CROUCHING),
            new SVFLBit(-3D/16D, -2.5D/16D, 2.5D/16D),
            new SVFLBit(0, 0, 0, 0.1, 0, 1.25, 0),
            new ASIMPLForwardNZVerticalNY("body"));
    /**
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint INTERNAL_LEGS =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "i_legs"),
            new ASIPSVanillaPoseProfile(Pose.CROUCHING, Pose.CROUCHING),
            new SVFLBit(1.5D/16D, 0D/16D, 0.6D/16D),
            new SVFLBit(0.3, 0, 0, 0, 0, 0.7, 0),
            new ASIMPLForwardNZVerticalNY("left_leg"));
    /**
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint INTERNAL_BOOTS =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "i_feet"),
            new ASIPSVanillaPoseProfile(Pose.SWIMMING, Pose.SWIMMING),
            new SVFLBit(0D/16D, -11.5D/16D, -1D/16D),
            new SVFLBit(-0.15, 0, 0, 0, 0, 0.1, 0),
            new ASIMPLForwardNZVerticalNY("right_leg"));
    /**
     * A hold point that positions the held entity in the mainhand of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint INTERNAL_MAIN =  new ASIPSItemPoserHold(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "i_right"),
            new ASIPSVanillaPoseProfile(Pose.STANDING, Pose.CROUCHING),
            new SVFLBit(-0.5D/16D, -8.5D/16D, 0.2D/16D),
            new SVFLBit(-0.4, 0, 0, 0.2, 0, 1.2, 0),
            new ASIMPLForwardNZVerticalNY("right_arm"));
    /**
     * A hold point that positions the held entity in the mainhand of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint INTERNAL_OFF =  new ASIPSItemPoserHold(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "i_left"),
            new ASIPSVanillaPoseProfile(Pose.STANDING, Pose.CROUCHING),
            new SVFLBit(0.5D/16D, -8.5D/16D, 0.2D/16D),
            new SVFLBit(0.4, 0, 0, 0.2, 0, 1.2, 0),
            new ASIMPLForwardNZVerticalNY("left_arm"));
    /**
     * A hold point that positions the held entity in front of you
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint PINCH =  new ASIPSRelativeFacingHold(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "pinch"),
            new ASIPSVanillaPoseProfile(Pose.STANDING, Pose.SPIN_ATTACK),
            new SVFLBit(-0.1, 0, 0.2, 0.2, 0, 1.6, 0));
    //endregion

    /**
     * A hold point that positions the held entity in the mainhand of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint RIGHT_HAND =  new ASIPSItemPoserHold(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "right_hand"),
            new ASIPSVanillaPoseProfile(Pose.STANDING, Pose.CROUCHING),
            new SVFLBit(0D/16D, -9D/16D, 0.2D/16D),
            new SVFLBit(-0.4, 0, 0, 0.2, 0, 1.2, 0),
            new ASIMPLForwardNZVerticalNY("right_arm"));
    /**
     * A hold point that positions the held entity in the offhand of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint LEFT_HAND =  new ASIPSItemPoserHold(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "left_hand"),
            new ASIPSVanillaPoseProfile(Pose.STANDING, Pose.CROUCHING),
            new SVFLBit(0D/16D, -9D/16D, 0.2D/16D),
            new SVFLBit(0.4, 0, 0, 0.2, 0, 1.2, 0),
            new ASIMPLForwardNZVerticalNY("left_arm"));
    /**
     * A powerful hold point where tinies are held deep in the right fist
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint RIGHT_FIST =  new ASIPSItemPoserHold(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "right_fist"),
            new ASIPSVanillaPoseProfile(Pose.STANDING, Pose.CROUCHING),
            new SVFLBit(0D/16D, -8.7D/16D, -0.8D/16D),
            new SVFLBit(-0.4, 0, 0, 0.2, 0, 1.2, 0),
            new ASIMPLForwardNZVerticalNY("right_arm"));
    /**
     * A powerful hold point where tinies are held deep in the left fist
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint LEFT_FIST =  new ASIPSItemPoserHold(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "left_fist"),
            new ASIPSVanillaPoseProfile(Pose.STANDING, Pose.CROUCHING),
            new SVFLBit(0D/16D, -8.7D/16D, -0.8D/16D),
            new SVFLBit(0.4, 0, 0, 0.2, 0, 1.2, 0),
            new ASIMPLForwardNZVerticalNY("left_arm"));
    /**
     * A hold point that positions the held entity on the head of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint HAT =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "hat"),
            new ASIPSVanillaPoseProfile(Pose.SITTING, Pose.CROUCHING),
            new SVFLBit(0D/16D, 5.8D/16D, 0D/16D),
            new SVFLBit(0, 0, 0, 0, 0, 1.8, 0),
            new ASIMPLForwardNZVerticalNY("head"));
    /**
     * A hold point that positions the held entity on the head of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint HEAD =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "head"),
            new ASIPSVanillaPoseProfile(Pose.SITTING, Pose.CROUCHING),
            new SVFLBit(0D/16D, 5.8D/16D, 3D/16D),
            new SVFLBit(0, 0, 0, 0.1, 0, 1.8, 0),
            new ASIMPLForwardNZVerticalNY("head"));
    /**
     * A hold point that positions the held entity in the right shoulder of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint RIGHT_SHOULDER =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "right_shoulder"),
            new ASIPSVanillaPoseProfile(Pose.SITTING, Pose.CROUCHING),
            new SVFLBit(-5.5D/16D, -1.2D/16D, 0.5D/16D),
            new SVFLBit(-0.3, 0, 0, 0, 0, 1.4, 0),
            new ASIMPLForwardNZVerticalNY("body"));
    /**
     * A hold point that positions the held entity in the left shoulder of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint LEFT_SHOULDER =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "left_shoulder"),
            new ASIPSVanillaPoseProfile(Pose.SITTING, Pose.CROUCHING),
            new SVFLBit(5.5D/16D, -1.2D/16D, 0.5D/16D),
            new SVFLBit(0.3, 0, 0, 0, 0, 1.4, 0),
            new ASIMPLForwardNZVerticalNY("body"));
    /**
     * A hold point that positions the held entity in the right pocket of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint RIGHT_POCKET =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "right_pocket"),
            new ASIPSVanillaPoseProfile(Pose.CROUCHING, Pose.CROUCHING),
            new SVFLBit(-3.8D/16D, -11D/16D, 1.2D/16D),
            new SVFLBit(-0.3, 0, 0, 0, 0, 1.0, 0),
            new ASIMPLForwardNZVerticalNY("body"));
    /**
     * A hold point that positions the held entity in the left pocket of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint LEFT_POCKET =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "left_pocket"),
            new ASIPSVanillaPoseProfile(Pose.CROUCHING, Pose.CROUCHING),
            new SVFLBit(3.8D/16D, -11D/16D, 1.2D/16D),
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
            new ASIPSVanillaPoseProfile(Pose.CROUCHING, Pose.CROUCHING),
            new SVFLBit(-1.5D/16D, -3D/16D, 0.6D/16D),
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
            new ASIPSVanillaPoseProfile(Pose.CROUCHING, Pose.CROUCHING),
            new SVFLBit(1.5D/16D, -3D/16D, 0.6D/16D),
            new SVFLBit(0.3, 0, 0, 0, 0, 0.7, 0),
            new ASIMPLForwardNZVerticalNY("left_leg"));
    /**
     * A hold point that positions the held entity in the right boot of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint RIGHT_BOOT =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "right_boot"),
            new ASIPSVanillaPoseProfile(Pose.SWIMMING, Pose.SWIMMING),
            new SVFLBit(0D/16D, -11.5D/16D, 1D/16D),
            new SVFLBit(-0.15, 0, 0, 0, 0, 0.1, 0),
            new ASIMPLForwardNZVerticalNY("right_leg"));
    /**
     * A hold point that positions the held entity in the left boot of the player model.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint LEFT_BOOT =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "left_boot"),
            new ASIPSVanillaPoseProfile(Pose.SWIMMING, Pose.SWIMMING),
            new SVFLBit(0D/16D, -11.5D/16D, 1D/16D),
            new SVFLBit(0.15, 0, 0, 0, 0, 0.1, 0),
            new ASIMPLForwardNZVerticalNY("left_leg"));
    /**
     * Something around belly height, such as those wide pockets hoodies have
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint HOODIE_POCKET =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "hoodie_pocket"),
            new ASIPSVanillaPoseProfile(Pose.STANDING, Pose.CROUCHING),
            new SVFLBit(0D/16D, -9.5D/16D, 2.5D/16D),
            new SVFLBit(0, 0, 0, 0.1, 0, 1.25, 0),
            new ASIMPLForwardNZVerticalNY("body"));
    /**
     * Something around chest height and to the side, such as those pockets shirts sometimes have
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint CHEST_POCKET =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "chest_pocket"),
            new ASIPSVanillaPoseProfile(Pose.STANDING, Pose.CROUCHING),
            new SVFLBit(3D/16D, -2.5D/16D, 2.5D/16D),
            new SVFLBit(0, 0, 0, 0.1, 0, 1.25, 0),
            new ASIMPLForwardNZVerticalNY("body"));
    /**
     * Something around chest height, as if dangling from a necklace or something
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint NECKLACE =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "necklace"),
            new ASIPSVanillaPoseProfile(Pose.STANDING, Pose.CROUCHING),
            new SVFLBit(0D/16D, -3.5D/16D, 2.5D/16D),
            new SVFLBit(0, 0, 0, 0.1, 0, 1.25, 0),
            new ASIMPLForwardNZVerticalNY("body"));
    /**
     * In front of the head of the player model as if holding with lips, presumably
     * because your hands are busy with pickaxes or something.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint NOMF =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "nomf"),
            new ASIPSVanillaPoseProfile(Pose.SWIMMING, Pose.SWIMMING),
            new SVFLBit(0D/16D, 1D/16D, 3D/16D),
            new SVFLBit(0, 0, 0, 0.07, 0, 1.4, 0),
            new ASIMPLForwardNZVerticalNY("head"));
    /**
     * In front of the head of the player model as if holding with lips, presumably
     * because your hands are busy with pickaxes or something, but one pixel lower.
     *
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint NOMF_LOW =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "nomf_low"),
            new ASIPSVanillaPoseProfile(Pose.SWIMMING, Pose.SWIMMING),
            new SVFLBit(0D/16D, 0.05D/16D, 3D/16D),
            new SVFLBit(0, 0, 0, 0.07, 0, 1.4, 0),
            new ASIMPLForwardNZVerticalNY("head"));
    /**
     * @since 1.0.0
     */
    @NotNull public static final ASIPSRegisterableHoldPoint FLUSH =  new ASIPSModelPartHoldPoint(
            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "flush"),
            new ASIPSVanillaPoseProfile(Pose.SWIMMING, Pose.CROUCHING),
            new SVFLBit(0D/16D, -13D/16D, 1D/16D),
            new SVFLBit(0, 0, 0, 0, 0, 1, 0),
            new ASIMPLForwardNZVerticalNY("body"));
}
