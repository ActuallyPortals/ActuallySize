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
            .comment(" ")
            .comment(" #### --------------------------------------------------")
            .comment(" #### Global Systems - General ASI Configuration")
            .comment(" #### --------------------------------------------------")
            .comment(" ")
            .comment(" #### ----|    Practical Size    |----")
            .comment(" Some entities are already small, and some are pretty big. For example,")
            .comment(" a ravager VS a chicken. For size calculations, it feels better to use")
            .comment(" bigger numbers for massive entities, I call this their 'practical size.'")
            .define("usePracticalSize", true);

    /**
     * Whether the "Pickup Entity" system is enabled at all
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.BooleanValue ENABLE_ENTITY_PICKUP = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Allow Pickup    |----")
            .comment(" Enables the ability to pick up entities as items, if you")
            .comment(" are big enough to hold them in your mainhand or offhand.")
            .comment(" To pick up players, the Allow Hold must also be enabled.")
            .define("allowPickup", true);

    /**
     * Whether the "Holding Entity" system is enabled at all
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.BooleanValue ENABLE_ENTITY_HOLDING = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Allow Hold    |----")
            .comment(" When holding an entity item picked up via the pickup system,")
            .comment(" they will also be alive in the world and able to interact live.")
            .comment(" Required to pickup players.")
            .define("allowHold", true);

    /**
     * The default size for beegs, clients that enabled it in their config will join like this.
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.DoubleValue BEEG_SIZE = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### --------------------------------------------------")
            .comment(" #### Client Services - These affect players' client configs")
            .comment(" #### --------------------------------------------------")
            .comment(" ")
            .comment(" #### ----|    Beeg Size    |----")
            .comment(" Players may indicate they prefer to be beeg, this is the")
            .comment(" size they will have by default when joining and respawning.")
            .comment(" Set to '1' to disable this feature.")
            .defineInRange("beegSize", 8, 0.05, 25);

    /**
     * The default size for tinies, clients that enabled it in their config will join like this.
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.DoubleValue TINY_SIZE = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Tiny Size    |----")
            .comment(" Players may indicate they prefer to be tiny, this is the")
            .comment(" size they will have by default when joining and respawning.")
            .comment(" Set to '1' to disable this feature.")
            .defineInRange("tinySize", 0.13, 0.05, 25);

    /**
     * The default size for tinies, clients that enabled it in their config will join like this.
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.BooleanValue FREE_SIZE = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Allow Free Size    |----")
            .comment(" Gives players the option to freely choose whatever scale they want")
            .comment(" between 0.05x and 25x, to have as their default scale when joining.")
            .define("allowFreeSize", true);

    /**
     * If beegs receive less damage and knockback from all sources
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.BooleanValue BEEGS_ARE_TANKY = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### --------------------------------------------------")
            .comment(" #### Combat Settings - Rampage of beegs over tinies lol")
            .comment(" #### --------------------------------------------------")
            .comment(" ")
            .comment(" #### ----|    Tanky Beegs    |----")
            .comment(" Beegs will receive less damage from most sources the bigger they are. ")
            .comment(" In combat damage, larger attackers suffer less damage reduction. ")
            .comment(" Knockback is also reduced just for the fact of being beeg. ")
            .define("beegsAreTanky", true);

    /**
     * If beegs deal more melee damage, unless they crouch to avoid hitting too hard.
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.BooleanValue BEEGS_ARE_STRONG = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Strong Beegs    |----")
            .comment(" Beegs will deal more damage in melee attacks to entities smaller than them. ")
            .comment(" You may crouch while punching unarmed to disable this for that one punch, so it will be a normal vanilla punch. ")
            .comment(" Also affects knockback between different-sized combatants. ")
            .define("beegsAreStrong", true);

    /**
     * If tinies receive more damage from all sources (does not affect knockback)
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.BooleanValue TINIES_ARE_DELICATE = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Delicate Tinies    |----")
            .comment(" Tinies will receive more damage from most sources the smaller they are. ")
            .comment(" In combat damage, smaller attackers get less damage amplification. ")
            .comment(" When punched by a player, if that player is crouching, their damage is not amplified at all. ")
            .comment(" The [beegsAreStrong] option takes precedence in combat (so that only [beegsAreStrong] is applied). ")
            .define("tiniesAreDelicate", true);

    /**
     * If beegs deal more melee damage, unless they crouch to avoid hitting too hard.
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.DoubleValue SIZE_DAMAGE_LIMIT = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Size Damage Amplifier    |----")
            .comment(" If you would take more damage because you are small, the maximum multiplier. ")
            .comment(" If you would deal bonus damage due to being beeg, the maximum multiplier. ")
            .defineInRange("sizeDamageAmplifier", 25D, 1D, Double.MAX_VALUE);

    /**
     * The relative scale between a player and an entity so that the player can ride the entity
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.DoubleValue SCALE_LIMIT_RIDE = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### --------------------------------------------------")
            .comment(" #### Miscellaneous Settings - Minor systems and fixes")
            .comment(" #### --------------------------------------------------")
            .comment(" ")
            .comment(" #### ----|    Riding Scale Limit    |----")
            .comment(" The relative scale (ex. 2x) upper limit to riding another entity.")
            .comment(" If you are this much bigger, you won't be able to ride the other.")
            .comment(" Prevents giants from riding normal horses and such, but there will")
            .comment(" be no problem riding giant horses. Size matters!")
            .defineInRange("ridingScaleLimit", 2D, 0D, Double.MAX_VALUE);

    /**
     * Whether food is nerfed for giants and buffed for tinies. Also affects eat animation time.
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.BooleanValue BEEGS_ARE_HUNGRY = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Beegs are Hungry    |----")
            .comment(" This will make food a lot less effective the bigger")
            .comment(" you are, and feed and saturate more if you are small.")
            .comment(" Also results in beegs eating faster and tinies taking")
            .comment(" longer to eat, depending on the nutritional value of food.")
            .comment(" (silly tinies will prefer berries, with low nutrition hehe)")
            .define("beegsAreHungry", true);

    /**
     * Extends the duration of effects granted upon consumption of held entities
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.DoubleValue BEEGS_ARE_HUNGRY_DURATION = CONFIG_BUILDER
            .comment(" ")
            .comment(" This is a multiplier to increase or decrease effects")
            .comment(" granted when beegs eat animals (such as strength from")
            .comment(" horses) or other mobs. Disables this feature when ZERO")
            .defineInRange("foodEntityDurationMultiplier", 1D,0D, Double.MAX_VALUE);
    /**
     * Increases the frequency of effects granted upon consumption of held entities
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.DoubleValue BEEGS_ARE_HUNGRY_FREQUENCY = CONFIG_BUILDER
            .comment(" ")
            .comment(" This is a multiplier to how often effects are granted")
            .comment(" by eating live animals. Does not affect boss entities.")
            .defineInRange("foodEntityFrequencyMultiplier", 1D,0D, Double.MAX_VALUE);
    /**
     * At what scale does your size cause panic on monsters
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.DoubleValue FEAR_THRESHOLD = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Fear    |----")
            .comment(" This is the scale where monsters begin to fear you")
            .comment(" if you are a beeg, some may even panic. For example,")
            .comment(" 4.0 means monsters fear giants 4x bigger than them")
            .defineInRange("fearThreshold", 4D,1D, Double.MAX_VALUE);
    /**
     * If beegs will build in a grid their scale
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.BooleanValue BEEG_BUILDING = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Beeg Building    |----")
            .comment(" Enables giants to build and break blocks in a grid")
            .comment(" their size, so an 8X beeg will break in 8x8x8. This")
            .comment(" also nerfs block pickup by your scale squared and")
            .comment(" nerfs block placing by a factor of your scale. ")
            .define("beegBuilding", true);
    /**
     * Allows to disable block ratio fixes
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.BooleanValue BEEG_BUILDING_DROP_RATE = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Beeg Building Drop Rate    |----")
            .comment(" Picking up scaled-up building blocks will multiply")
            .comment(" them in your inventory, but also picking up blocks")
            .comment(" smaller than your scale will decrease them.")
            .comment(" This works to balance BEEG BUILDING option, because")
            .comment(" giants break and place down TONS of blocks.")
            .define("beegBuildingDropRate", true);

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
    public static boolean beegBuilding, beegBuildingDropRate;

    public static boolean usePracticalSize;
    public static double scaleLimitRider;
    public static boolean tankyBeegs;
    public static boolean strongBeegs;
    public static boolean hungryBeegs;
    public static boolean delicateTinies;
    public static double strongestBeeg;
    public static double beegSize, tinySize;
    public static double foodDuration, foodFrequency;
    public static double fearThreshold;

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
        hungryBeegs = BEEGS_ARE_HUNGRY.get();
        fearThreshold = FEAR_THRESHOLD.get();
        beegBuilding = BEEG_BUILDING.get();
        beegBuildingDropRate = BEEG_BUILDING_DROP_RATE.get();

        foodDuration = BEEGS_ARE_HUNGRY_DURATION.get();
        foodFrequency = BEEGS_ARE_HUNGRY_FREQUENCY.get();

        enableFreeSize = FREE_SIZE.get();
        beegSize = BEEG_SIZE.get();
        tinySize = TINY_SIZE.get();
    }
    //endregion
}
