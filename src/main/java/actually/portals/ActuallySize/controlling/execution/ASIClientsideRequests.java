package actually.portals.ActuallySize.controlling.execution;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.netcode.ASINetworkManager;
import actually.portals.ActuallySize.netcode.packets.serverbound.ASINSEntityDualitySyncRequest;
import actually.portals.ActuallySize.pickup.actions.ASIPSHoldingSyncAction;
import gunging.ootilities.GungingOotilitiesMod.scheduling.SchedulingManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Listeners for events that may only happen on the client-side
 *
 * @since 1.0.0
 * @author Actually Portals
 */
@Mod.EventBusSubscriber(modid = ActuallySizeInteractions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ASIClientsideRequests {

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
                syncing.resolve();;

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
