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
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.ConfigValue<String> HEAD_HOLD_POINT = CONFIG_BUILDER
            .comment("")
            .comment("#### --------------------------------------------------")
            .comment("#### Hold Slots - Where do you want to hold tinies?")
            .comment("#### ")
            .comment("#### I personally could not decide, for example, if holding the item")
            .comment("#### of someone should let them sit on your shoulder instead of your")
            .comment("#### hand, or something like that. Then you have the option to! Try it:")
            .comment("#### ")
            .comment("#### Change the 'offhandHold' option to 'actuallysize:right_shoulder'")
            .comment("#### --------------------------------------------------")
            .comment("")
            .comment("When holding an entity in your head slot, where does it show up on your player? ")
            .define("headHold", "actuallysize:hat");

    /**
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.ConfigValue<String> CHEST_HOLD_POINT = CONFIG_BUILDER
            .comment("When holding an entity in your chestplate slot, where does it show up on your player? ")
            .define("chestHold", "actuallysize:right_shoulder");

    /**
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.ConfigValue<String> LEGS_HOLD_POINT = CONFIG_BUILDER
            .comment("When holding an entity in your leggings slot, where does it show up on your player? ")
            .define("legsHold", "actuallysize:left_pocket");

    /**
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.ConfigValue<String> FEET_HOLD_POINT = CONFIG_BUILDER
            .comment("When holding an entity in your boots slot, where does it show up on your player? ")
            .define("bootsHold", "actuallysize:right_boot");

    /**
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.ConfigValue<String> MAINHAND_HOLD_POINT = CONFIG_BUILDER
            .comment("When holding an entity in your main hand, where does it show up on your player? ")
            .define("mainhandHold", "actuallysize:mainhand");

    /**
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.ConfigValue<String> OFFHAND_HOLD_POINT = CONFIG_BUILDER
            .comment("When holding an entity in your offhand, where does it show up on your player? ")
            .define("offhandHold", "actuallysize:offhand");

    /**
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.ConfigValue<String> CURSOR_HOLD_POINT = CONFIG_BUILDER
            .comment("While you move around an entity in your inventory, where does it show up on your player? ")
            .define("cursorHold", "actuallysize:pinch");

    /**
     * Honestly holding animals in the same slots you hold players just feels off sometimes (real).
     *
     * @since 1.0.0
     */
    @NotNull private static final ForgeConfigSpec.BooleanValue ONLY_PLAYERS = CONFIG_BUILDER
            .comment("When this is enabled, these custom hold points will only apply when holding other players. ")
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
