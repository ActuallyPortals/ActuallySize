package actually.portals.ActuallySize.controlling.execution;

import actually.portals.ActuallySize.ActuallyClientConfig;
import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.netcode.ASINetworkManager;
import actually.portals.ActuallySize.netcode.packets.serverbound.ASINSPreferredSize;
import actually.portals.ActuallySize.pickup.actions.ASIPSHoldingSyncAction;
import gunging.ootilities.GungingOotilitiesMod.instants.GOOMClientsidePlayerLoginEvent;
import gunging.ootilities.GungingOotilitiesMod.scheduling.SchedulingManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

/**
 * Listeners for events that may only happen on the client-side
 *
 * @since 1.0.0
 * @author Actually Portals
 */
@Mod.EventBusSubscriber(modid = ActuallySizeInteractions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ASIClientsideRequests {

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @SubscribeEvent
    public static void OnServerJoin(@NotNull GOOMClientsidePlayerLoginEvent event) {

        /*
         * Send a preferred size packet
         *
         * Ideally this would happen when logging in the server rather than
         * joining the world, but there is no event readily accessible for that
         */
        ASINSPreferredSize prefs = new ASINSPreferredSize(ActuallyClientConfig.preferredScale, ActuallyClientConfig.isPreferBeeg, ActuallyClientConfig.isPreferTiny);
        ASINetworkManager.playerToServer(prefs);
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @SubscribeEvent
    public static void OnWorldLoad(@NotNull EntityJoinLevelEvent event) {
        LocalPlayer me = Minecraft.getInstance().player;
        if (me == null) { return; }
        if (me.getId() == event.getEntity().getId()) {

            // Identify world
            //HDA//ActuallySizeInteractions.Log("ASI OWL &ePLAYER JOINED WORLD FOR THE FIRST TIME");

            SchedulingManager.scheduleTask(() -> {

                // Resolve without asking
                ASIPSHoldingSyncAction syncing = new ASIPSHoldingSyncAction(me);
                syncing.withActiveDualities();
                syncing.resolve();

                /*/ Iterate entities within like 32 chunks, should be enough
                Level world = me.level();
                List<Entity> found = world.getEntities(me, me.getBoundingBox().inflate(512, 512, 512));
                for (Entity anon : found) {

                    // Request Entity
                    ASINSEntityDualitySyncRequest request = new ASINSEntityDualitySyncRequest(anon);
                    ASINetworkManager.playerToServer(request);
                }   //*/

                }, 5, true);
        }
    }
}
