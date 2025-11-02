package actually.portals.ActuallySize;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The configuration of what the client's preferences are
 *
 * @since 1.0.0
 * @author Actually Portals
 */
@Mod.EventBusSubscriber(modid = ActuallySizeInteractions.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ActuallyClientConfig {

    //region Config Parameters
    /**
     * The Forge API to build configuration settings
     *
     * @since 1.0.0
     */
    @NotNull
    private static final ForgeConfigSpec.Builder CONFIG_BUILDER = new ForgeConfigSpec.Builder();

    /**
     * If this player prefers to be beeg
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.BooleanValue PREFERRED_BEEG = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### --------------------------------------------------")
            .comment(" #### Global Preferences - General ASI Configuration")
            .comment(" #### --------------------------------------------------")
            .comment(" ")
            .comment(" #### ----|    Prefers to be Big    |----")
            .comment(" For servers that have a beeg size configured, you will")
            .comment(" spawn beeg by default without having to do anything else.")
            .comment(" Also affects the size at which you respawn.")
            .define("preferablyBeeg", false);

    /**
     * If this player prefers to be smol
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.BooleanValue PREFERRED_TINY = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Prefers to be Smol    |----")
            .comment(" For servers that have a tiny size configured, you will")
            .comment(" spawn tiny by default without having to do anything else.")
            .comment(" Also affects the size at which you respawn.")
            .define("preferablySmol", false);

    /**
     * If this player prefers to be smol
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.DoubleValue PREFERRED_SCALE = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Preferred Scale    |----")
            .comment(" For servers that allow you to freely choose your")
            .comment(" size, what scale do you want to be by default?")
            .comment(" Also affects the size at which you respawn.")
            .comment(" Set to '1' to disable this feature.")
            .defineInRange("preferredScale", 1, 0.05, 25);

    /**
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.ConfigValue<String> HEAD_HOLD_POINT = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### --------------------------------------------------")
            .comment(" #### Hold Slots - Where do you want to hold tinies?")
            .comment(" #### ")
            .comment(" #### I personally could not decide, for example, if holding the item")
            .comment(" #### of someone should let them sit on your shoulder instead of your")
            .comment(" #### hand, or something like that. Then you have the option to! Try it:")
            .comment(" #### ")
            .comment(" #### Change the 'offhandHold' option to 'actuallysize:right_shoulder'")
            .comment(" #### --------------------------------------------------")
            .comment(" ")
            .comment(" #### ----|    Helmet Armor Slot    |----")
            .comment(" When holding an entity in your head slot, where does it show up on your player? ")
            .define("headHold", "actuallysize:hat");

    /**
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.ConfigValue<String> CHEST_HOLD_POINT = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Chestplate Armor Slot    |----")
            .comment(" When holding an entity in your chestplate slot, where does it show up on your player? ")
            .define("chestHold", "actuallysize:right_shoulder");

    /**
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.ConfigValue<String> LEGS_HOLD_POINT = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Leggings Armor Slot    |----")
            .comment(" When holding an entity in your leggings slot, where does it show up on your player? ")
            .define("legsHold", "actuallysize:left_pocket");

    /**
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.ConfigValue<String> FEET_HOLD_POINT = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Boots Armor Slot    |----")
            .comment(" When holding an entity in your boots slot, where does it show up on your player? ")
            .define("bootsHold", "actuallysize:right_boot");

    /**
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.ConfigValue<String> MAINHAND_HOLD_POINT = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Selected hotbar Slot    |----")
            .comment(" When holding an entity in your main hand, where does it show up on your player? ")
            .define("mainhandHold", "actuallysize:right_hand");

    /**
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.ConfigValue<String> OFFHAND_HOLD_POINT = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Offhand Slot    |----")
            .comment(" When holding an entity in your offhand, where does it show up on your player? ")
            .define("offhandHold", "actuallysize:left_fist");

    /**
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.ConfigValue<String> CURSOR_HOLD_POINT = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    GUI Cursor Slot    |----")
            .comment(" While you move around an entity in your inventory, where does it show up on your player? ")
            .define("cursorHold", "actuallysize:pinch");

    /**
     * Honestly holding animals in the same slots you hold players just feels off sometimes (real).
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.BooleanValue ONLY_PLAYERS = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Only For Players    |----")
            .comment(" When this is enabled, these custom hold points will only apply when holding other players. ")
            .comment(" All other mobs will be held in the default slots while this is enabled.")
            .define("onlyForPlayers", true);

    /**
     * The config builder itself
     *
     * @since 1.0.0
     */
    @NotNull static final ForgeConfigSpec SPEC = CONFIG_BUILDER.build();
    //endregion

    //region Config Object
    /**
     * The hold points where this player has specified they want to hold tinies in
     *
     * @since 1.0.0
     */
    @Nullable public static ResourceLocation holdHead, holdChest, holdLegs, holdFeet, holdMainhand, holdOffhand, holdCursor;

    /**
     * The size preferences of this player
     *
     * @since 1.0.0
     */
    public static boolean isPreferBeeg, isPreferTiny;

    /**
     * The size preference of this player
     *
     * @since 1.0.0
     */
    public static double preferredScale;

    /**
     * If the special hold points are only used when holding players
     *
     * @since 1.0.0
     */
    public static boolean onlySpecialHoldPlayers;

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
        holdHead = tryRead(HEAD_HOLD_POINT.get());
        holdChest = tryRead(CHEST_HOLD_POINT.get());
        holdLegs = tryRead(LEGS_HOLD_POINT.get());
        holdFeet = tryRead(FEET_HOLD_POINT.get());
        holdMainhand = tryRead(MAINHAND_HOLD_POINT.get());
        holdOffhand = tryRead(OFFHAND_HOLD_POINT.get());
        holdCursor = tryRead(CURSOR_HOLD_POINT.get());
        onlySpecialHoldPlayers = ONLY_PLAYERS.get();
        preferredScale = PREFERRED_SCALE.get();
        isPreferBeeg = PREFERRED_BEEG.get();
        isPreferTiny = PREFERRED_TINY.get();
    }

    /**
     * @param compound A string that encodes for a Hold Point namespace and path
     *
     * @return The namespaced key it encodes for
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static @Nullable ResourceLocation tryRead(@NotNull String compound) {
        if (compound.isEmpty()) { return null; }
        if (!ResourceLocation.isValidResourceLocation(compound)) { return null; }
        int col = compound.indexOf(":");

        // Default to ASI Namespace
        if (col < 0) { return ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, compound); }

        // Read from parse
        return ResourceLocation.fromNamespaceAndPath(compound.substring(0, col), compound.substring(col + 1));
    }
    //endregion
}
