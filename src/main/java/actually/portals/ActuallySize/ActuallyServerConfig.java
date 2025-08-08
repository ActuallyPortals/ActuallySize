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
            .comment("Some entities are already small, and some are already big. This option will make it easier to pickup chickens compared to ravagers, basically. ")
            .define("usePracticalSize", true);

    /**
     * Whether the "Pickup Entity" system is enabled at all
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.BooleanValue ENABLE_ENTITY_PICKUP = CONFIG_BUILDER
            .comment("Allow players to pickup entities smaller than them. ")
            .define("allowPickup", true);

    /**
     * The relative scale between a player and an entity so that the player can pick it up with one hand.
     * 
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.DoubleValue SCALE_REQUIREMENT_ONE_HANDED_PICKUP = CONFIG_BUILDER
            .comment("The relative scale (ex. 4x) required to pickup entities with one hand. ")
            .defineInRange("scaleReqOneHandedPickup", 4D, 0D, Double.MAX_VALUE);

    /**
     * The config builder itself
     *
     * @since 1.0.0
     */
    @NotNull static final ForgeConfigSpec SPEC = CONFIG_BUILDER.build();
    //endregion

    //region Config Object
    public static boolean enableEntityPickup;
    public static boolean usePracticalSize;
    public static double scaleReqOneHandedPickup;

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
        usePracticalSize = USE_PRACTICAL_SIZE.get();
        scaleReqOneHandedPickup = SCALE_REQUIREMENT_ONE_HANDED_PICKUP.get();
    }
    //endregion
}
