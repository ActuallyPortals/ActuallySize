package actually.portals.ActuallySize.controlling.input;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallyServerConfig;
import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.pickup.actions.ASIPSPickupAction;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * The ASI Server Interaction Listener listens to what is happening
 * server-side to decide if a minecraft interaction is to be hijacked
 * by ActuallySizeInteractions into an interaction for this mod.
 * <p>
 * In a way, this is the design of minecraft plugins that is something
 * I am very used to working with, and you could argue that processing
 * some of this in the client would be good, but it is more secure to
 * just do this server sided.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
@Mod.EventBusSubscriber(modid = ActuallySizeInteractions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ASIServerInteractionListener {

    @SubscribeEvent
    public static void OnPlayerInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget().level().isClientSide()) { return; }

        // Do not bother if cancelled
        if (event.isCancelable()) { if (event.isCanceled()) { return; } }

        // Attempt to interpret this as a "Pickup Entity" event
        if (ActuallyServerConfig.enableEntityPickup && (new ASIPSPickupAction(event).tryResolve())) { event.setCanceled(true); return; }

        //todo Presumably check for other systems like feed or idk
    }

}
