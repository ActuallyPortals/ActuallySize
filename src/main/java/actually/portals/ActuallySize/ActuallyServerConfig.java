package actually.portals.ActuallySize;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Configuration of the allowed interactions. Mostly server-sided.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
@Mod.EventBusSubscriber(modid = ActuallySizeInteractions.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ActuallyServerConfig {

    //region Config Parameters

    /**
     * The Forge API to build configuration settings
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.Builder CONFIG_BUILDER = new ForgeConfigSpec.Builder();

    /**
     * Whether the "Pickup Entity" system is enabled at all
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.BooleanValue USE_PRACTICAL_SIZE = CONFIG_BUILDER
            .comment("")
            .comment("#### --------------------------------------------------")
            .comment("#### Global Systems - General ASI Configuration")
            .comment("#### --------------------------------------------------")
            .comment("")
            .comment("#### ----|    Practical Size    |----")
            .comment("Some entities are already small, and some are pretty big. For example,")
            .comment("a ravager VS a chicken. For size calculations, it feels better to use")
            .comment("bigger numbers for massive entities, I call this their 'practical size.'")
            .define("usePracticalSize", true);

    /**
     * Whether the "Pickup Entity" system is enabled at all
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.BooleanValue ENABLE_ENTITY_PICKUP = CONFIG_BUILDER
            .comment("")
            .comment("#### ----|    Allow Pickup    |----")
            .comment("Enables the ability to pick up entities as items, if you")
            .comment("are big enough to hold them in your mainhand or offhand.")
            .comment("To pick up players, the Allow Hold must also be enabled.")
            .define("allowPickup", true);

    /**
     * Whether the "Holding Entity" system is enabled at all
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.BooleanValue ENABLE_ENTITY_HOLDING = CONFIG_BUILDER
            .comment("")
            .comment("#### ----|    Allow Hold    |----")
            .comment("When holding an entity item picked up via the pickup system,")
            .comment("they will also be alive in the world and able to interact live.")
            .comment("Required to pickup players.")
            .define("allowHold", true);

    /**
     * The default size for beegs, clients that enabled it in their config will join like this.
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.DoubleValue BEEG_SIZE = CONFIG_BUILDER
            .comment("")
            .comment("#### --------------------------------------------------")
            .comment("#### Client Services - These affect players' client configs")
            .comment("#### --------------------------------------------------")
            .comment("")
            .comment("#### ----|    Beeg Size    |----")
            .comment("Players may indicate they prefer to be beeg, this is the")
            .comment("size they will have by default when joining and respawning.")
            .comment("Set to '1' to disable this feature.")
            .defineInRange("beegSize", 8, 0.05, 25);

    /**
     * The default size for tinies, clients that enabled it in their config will join like this.
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.DoubleValue TINY_SIZE = CONFIG_BUILDER
            .comment("")
            .comment("#### ----|    Tiny Size    |----")
            .comment("Players may indicate they prefer to be tiny, this is the")
            .comment("size they will have by default when joining and respawning.")
            .comment("Set to '1' to disable this feature.")
            .defineInRange("tinySize", 0.13, 0.05, 25);

    /**
     * The default size for tinies, clients that enabled it in their config will join like this.
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.BooleanValue FREE_SIZE = CONFIG_BUILDER
            .comment("")
            .comment("#### ----|    Allow Free Size    |----")
            .comment("Gives players the option to freely choose whatever scale they want")
            .comment("between 0.05x and 25x, to have as their default scale when joining.")
            .define("allowFreeSize", false);

    /**
     * If beegs receive less damage and knockback from all sources
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.BooleanValue BEEGS_ARE_TANKY = CONFIG_BUILDER
            .comment("")
            .comment("#### --------------------------------------------------")
            .comment("#### Combat Settings - Rampage of beegs over tinies lol")
            .comment("#### --------------------------------------------------")
            .comment("")
            .comment("#### ----|    Tanky Beegs    |----")
            .comment("Beegs will receive less damage from most sources the bigger they are. ")
            .comment("In combat damage, larger attackers suffer less damage reduction. ")
            .comment("Knockback is also reduced just for the fact of being beeg. ")
            .define("beegsAreTanky", true);

    /**
     * If beegs deal more melee damage, unless they crouch to avoid hitting too hard.
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.BooleanValue BEEGS_ARE_STRONG = CONFIG_BUILDER
            .comment("")
            .comment("#### ----|    Strong Beegs    |----")
            .comment("Beegs will deal more damage in melee attacks to entities smaller than them. ")
            .comment("You may crouch while punching unarmed to disable this for that one punch, so it will be a normal vanilla punch. ")
            .comment("Also affects knockback between different-sized combatants. ")
            .define("beegsAreStrong", true);

    /**
     * If tinies receive more damage from all sources (does not affect knockback)
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.BooleanValue TINIES_ARE_DELICATE = CONFIG_BUILDER
            .comment("")
            .comment("#### ----|    Delicate Tinies    |----")
            .comment("Tinies will receive more damage from most sources the smaller they are. ")
            .comment("In combat damage, smaller attackers get less damage amplification. ")
            .comment("When punched by a player, if that player is crouching, their damage is not amplified at all. ")
            .comment("The [beegsAreStrong] takes precedence in combat (so that only [beegsAreStrong] is applied). ")
            .define("tiniesAreDelicate", true);

    /**
     * If beegs deal more melee damage, unless they crouch to avoid hitting too hard.
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.DoubleValue SIZE_DAMAGE_LIMIT = CONFIG_BUILDER
            .comment("")
            .comment("#### ----|    Size Damage Amplifier    |----")
            .comment("If you would take more damage because you are small, the maximum multiplier. ")
            .comment("If you would deal bonus damage due to being beeg, the maximum multiplier. ")
            .defineInRange("sizeDamageAmplifier", 25D, 1D, Double.MAX_VALUE);

    /**
     * The relative scale between a player and an entity so that the player can pick it up with one hand.
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.DoubleValue SCALE_LIMIT_RIDE = CONFIG_BUILDER
            .comment("")
            .comment("#### --------------------------------------------------")
            .comment("#### Miscellaneous Settings - Minor systems and fixes")
            .comment("#### --------------------------------------------------")
            .comment("")
            .comment("#### ----|    Riding Scale Limit    |----")
            .comment("The relative scale (ex. 2x) upper limit to riding another entity.")
            .comment("If you are this much bigger, you won't be able to ride the other.")
            .comment("Prevents giants from riding normal horses and such, but there will")
            .comment("be no problem riding giant horses. Size matters!")
            .defineInRange("ridingScaleLimit", 2D, 0D, Double.MAX_VALUE);

    /**
     * The config builder itself
     *
     * @since 1.0.0
     */
    @NotNull static final ForgeConfigSpec SPEC = CONFIG_BUILDER.build();
    //endregion

    //region Config Object
    public static boolean enableEntityPickup;
    public static boolean enableEntityHolding;
    public static boolean enableFreeSize;

    public static boolean usePracticalSize;
    public static double scaleLimitRider;
    public static boolean tankyBeegs;
    public static boolean strongBeegs;
    public static boolean delicateTinies;
    public static double strongestBeeg;
    public static double beegSize, tinySize;

    /**
     * Reads the values specified in the config and loads them
     * to their static variables to be accessed from anywhere
     * in the mod.
     *
     * @param event The mod config loading event
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() != SPEC) { return; }

        enableEntityPickup = ENABLE_ENTITY_PICKUP.get();
        enableEntityHolding = ENABLE_ENTITY_HOLDING.get();

        usePracticalSize = USE_PRACTICAL_SIZE.get();
        scaleLimitRider = SCALE_LIMIT_RIDE.get();
        tankyBeegs = BEEGS_ARE_TANKY.get();
        strongBeegs = BEEGS_ARE_STRONG.get();
        delicateTinies = TINIES_ARE_DELICATE.get();
        strongestBeeg = SIZE_DAMAGE_LIMIT.get();

        enableFreeSize = FREE_SIZE.get();
        beegSize = BEEG_SIZE.get();
        tinySize = TINY_SIZE.get();
    }
    //endregion
}
