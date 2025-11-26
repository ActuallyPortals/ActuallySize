package actually.portals.ActuallySize;

import actually.portals.ActuallySize.controlling.execution.ASIClientsideRequests;
import actually.portals.ActuallySize.netcode.ASINetworkManager;
import actually.portals.ActuallySize.pickup.ASIPickupSystemManager;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

/**
 * <b>Actually Size Interactions</b> ASI
 * <br><br>
 * A mod where size matters. It hooks onto existing size-changing mods (such as Pehkui) and changes
 * how you interact with the world when beeg or smol, from picking up tinies to dealing massive
 * crush damage to being carried away by water currents to being put in someone's pocket or shoulder.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
@Mod(ActuallySizeInteractions.MODID)
public class ActuallySizeInteractions {

    /**
     * The Manager for the system that allows you
     * to pickup entities smaller than you.
     *
     * @since 1.0.0
     */
    public static ASIPickupSystemManager PICKUP_SYSTEM = null;

    /**
     * @return The Pickup System manager
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public ASIPickupSystemManager getPickupSystem() {
        if (PICKUP_SYSTEM!= null) { return PICKUP_SYSTEM; }

        // Just create a new Renderer without level
        PICKUP_SYSTEM = new ASIPickupSystemManager();
        return PICKUP_SYSTEM;
    }

    /**
     * The ModID used everywhere that this modID is required
     *
     * @since 1.0.0
     */
    public static final String MODID = "actuallysize";

    /**
     * Registering the Actually Size Interactions plugin
     *
     * @param context The mod loading context
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ActuallySizeInteractions(FMLJavaModLoadingContext context) {

        // Register this mod onto Forge
        MinecraftForge.EVENT_BUS.register(this);
        context.getModEventBus().addListener(this::OnCommonSetup);

        // Register Mod Configuration
        context.registerConfig(ModConfig.Type.SERVER, ActuallyServerConfig.SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, ActuallyClientConfig.SPEC);

        // Register the various systems
        getPickupSystem().OnModLoadInitialize(context);
    }

    /**
     * For now, it only registers netcode.
     *
     * @param event The common setup event call
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    private void OnCommonSetup(final FMLCommonSetupEvent event) {

        /*
         * The event does not run on the main thread, we must
         * enqueue this for it to run in the main thread.
         */

        event.enqueueWork(() -> {
            ASINetworkManager.register();
        });
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            IS_CLIENT_DEV = true;
        }
    }

    /**
     * If we are running client-sided, but it is poorly tested.
     * Only really useful for my Grep Console IntelliJ plugin setup.
     *
     * @since 1.0.0
     */
    public static boolean IS_CLIENT_DEV;

    /**
     * @param log A string to show in the console
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void Log(@Nullable String log, @Nullable Object... replaces) {

        // Bake replaces
        String baked = log;
        if (log != null && replaces.length > 0) {
            for (int i = 0; i < replaces.length; i++) {
                Object rep = replaces[i];
                baked = baked.replace("{" + i + "}", rep == null ? "nulL" : String.valueOf(rep));
            }
        }

        // Display in console
        if (IS_CLIENT_DEV) {
            System.out.println("GREP [CHAT] <Dev> " + baked);
            ASIClientsideRequests.Log(baked);

        } else {
            System.out.println("GREP [Not Secure] <Dev> " + baked);
        }
    }

    /**
     * Very often do I have to debug the holding of entities, it is worth
     * to have an actual structure by which to log this system.
     *
     * @since 1.0.0
     */
    static final boolean HDA_LOGGING = false;

    /**
     * Logs specifically a line for the Holding System
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public static void LogHDA(@NotNull Class<?> from, @NotNull String key, @NotNull String base, @Nullable Object... replaces) {
        if (!HDA_LOGGING) { return; }
        Log("ASI &a "+ key + " &8 [" + from.getSimpleName() + "] " + base, replaces);
    }

    /**
     * Logs specifically a division line for the Holding System
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public static void LogHDA(boolean start, @NotNull Class<?> from, @NotNull String key, @NotNull String base, @Nullable Object... replaces) {
        if (!HDA_LOGGING) { return; }
        Log("ASI &a "+ key + " &8 [" + from.getSimpleName() + "] ~~~~~~ " + base + (start ? " Start" : " End") + " ~~~~~~", replaces);
    }

    /**
     * A console logger that is preferred used when I am not running this from within JetBrains
     * IntelliJ, considering that the only reason I use System.out.println in that case is because
     * of my Grep Console plugin configuration that adds pretty colors to the stuff I print out.
     *
     * @since 1.0.0
     */
    private static final Logger FORGE_LOGGER = LogUtils.getLogger();
}
