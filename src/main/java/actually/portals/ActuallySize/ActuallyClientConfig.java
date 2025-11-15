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
            .comment(" #### hand, or something like that. Then you can choose either!")
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
            .define("chestHold", "actuallysize:chest_pocket");

    /**
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.ConfigValue<String> LEGS_HOLD_POINT = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Leggings Armor Slot    |----")
            .comment(" When holding an entity in your leggings slot, where does it show up on your player? ")
            .define("legsHold", "actuallysize:hoodie_pocket");

    /**
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.ConfigValue<String> FEET_HOLD_POINT = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Boots Armor Slot    |----")
            .comment(" When holding an entity in your boots slot, where does it show up on your player? ")
            .define("bootsHold", "actuallysize:left_boot");

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
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.ConfigValue<String> HOTBAR1_HOLD_POINT = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### --------------------------------------------------")
            .comment(" #### Hotbar Slots - More Hold Slots~")
            .comment(" #### ")
            .comment(" #### These are only engaged when holding players you have")
            .comment(" #### in your hotbar. You can change hold points around all")
            .comment(" #### you want, all ASI hold points work in any slot.")
            .comment(" #### --------------------------------------------------")
            .comment(" ")
            .comment(" #### ----|    Hotbar #1    |----")
            .comment(" The first, leftmost, hotbar slot ")
            .define("hotbarHold1", "actuallysize:nomf");
    /**
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.ConfigValue<String> HOTBAR2_HOLD_POINT = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Hotbar #2    |----")
            .define("hotbarHold2", "actuallysize:right_shoulder");
    /**
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.ConfigValue<String> HOTBAR3_HOLD_POINT = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Hotbar #3    |----")
            .define("hotbarHold3", "actuallysize:left_thigh");
    /**
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.ConfigValue<String> HOTBAR4_HOLD_POINT = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Hotbar #4    |----")
            .define("hotbarHold4", "actuallysize:right_pocket");
    /**
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.ConfigValue<String> HOTBAR5_HOLD_POINT = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Hotbar #5    |----")
            .define("hotbarHold5", "actuallysize:right_thigh");
    /**
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.ConfigValue<String> HOTBAR6_HOLD_POINT = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Hotbar #6    |----")
            .define("hotbarHold6", "actuallysize:left_shoulder");
    /**
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.ConfigValue<String> HOTBAR7_HOLD_POINT = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Hotbar #7    |----")
            .define("hotbarHold7", "actuallysize:left_pocket");
    /**
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.ConfigValue<String> HOTBAR8_HOLD_POINT = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Hotbar #8    |----")
            .define("hotbarHold8", "actuallysize:right_boot");
    /**
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.ConfigValue<String> HOTBAR9_HOLD_POINT = CONFIG_BUILDER
            .comment(" ")
            .comment(" #### ----|    Hotbar #9    |----")
            .define("hotbarHold9", "actuallysize:head");

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
    @Nullable public static final ResourceLocation[] holdHotbar = new ResourceLocation[9];

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

        // Read Equipment
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

        // Read Hotbar
        holdHotbar[0] = tryRead(HOTBAR1_HOLD_POINT.get());
        holdHotbar[1] = tryRead(HOTBAR2_HOLD_POINT.get());
        holdHotbar[2] = tryRead(HOTBAR3_HOLD_POINT.get());
        holdHotbar[3] = tryRead(HOTBAR4_HOLD_POINT.get());
        holdHotbar[4] = tryRead(HOTBAR5_HOLD_POINT.get());
        holdHotbar[5] = tryRead(HOTBAR6_HOLD_POINT.get());
        holdHotbar[6] = tryRead(HOTBAR7_HOLD_POINT.get());
        holdHotbar[7] = tryRead(HOTBAR8_HOLD_POINT.get());
        holdHotbar[8] = tryRead(HOTBAR9_HOLD_POINT.get());
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
