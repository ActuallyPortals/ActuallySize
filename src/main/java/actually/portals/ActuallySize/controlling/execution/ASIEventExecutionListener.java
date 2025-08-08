package actually.portals.ActuallySize.controlling.execution;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityActivationAction;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityDeactivationAction;
import actually.portals.ActuallySize.netcode.ASIClientsidePacketHandler;
import actually.portals.ActuallySize.pickup.ASIPickupSystemManager;
import actually.portals.ActuallySize.pickup.events.ASIPSPickupToInventoryEvent;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import gunging.ootilities.GungingOotilitiesMod.events.extension.ServersideEntityEquipmentChangeEvent;
import gunging.ootilities.GungingOotilitiesMod.scheduling.SCHTenTicksEvent;
import gunging.ootilities.GungingOotilitiesMod.scheduling.SCHTwentyTicksEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

/**
 * Listens to various events, even events from ASI itself,
 * that are not directly fired by players intending to
 * interact with the world around them.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
@Mod.EventBusSubscriber(modid = ActuallySizeInteractions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ASIEventExecutionListener {

    @SubscribeEvent
    public static void OnDualityFluxResolution(@NotNull TickEvent.ServerTickEvent event) {

        // The duality flux calculations resolve at the beginning of the server tick
        if (event.phase == TickEvent.Phase.START) { ASIPickupSystemManager.resolveDualityFlux(); }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void OnEquipmentChange(@NotNull ServersideEntityEquipmentChangeEvent event) {
        /*HDA*/ActuallySizeInteractions.Log("ASI &3 IED 1 &7 Intercepted &f " + event.getReason() + " &b " + event.getStackLocation().getStatement() + " &e Player " + event.getEntity().getScoreboardName() + " &r Item " + event.getCurrentItemStack().getDisplayName().getString() + ", VERIFYING...");

        // Create an action and try to resolve it :based:
        ASIPSDualityActivationAction action = new ASIPSDualityActivationAction(event.getStackLocation());
        if (!action.isVerified()) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &3 IED 1 &7 Unverified, registering [FROM] into Probable-Flux: " + event.getStackLocation().getStatement());

            /*
             * So we failed to verify a new Item Duality. Most
             * likely that is because this is not an Item-Duality
             *
             * Ultimately, equipment changed, so try to deactivate
             * if existing a current one in this slot :p
             */
            ASIPSDualityDeactivationAction inaction = new ASIPSDualityDeactivationAction(event.getStackLocation());
            if (!ASIPickupSystemManager.probableDualityFlux(inaction)) { inaction.tryResolve(); }   // If it makes no flux sense, resolve instant

        // If it verifies, attempt to activate it
        } else {
            /*HDA*/ActuallySizeInteractions.Log("ASI &3 IED 1 &7 Verified, registering [TO] into Probable-Flux: " + event.getStackLocation().getStatement());

            // Register to flux evaluation
            if (!ASIPickupSystemManager.probableDualityFlux(action)) { action.tryResolve(); }   // If it makes no flux sense, resolve instant
        }
    }

    /**
     * This event blocks villagers and other entities that have a right-click
     * interaction from being picked up unless you are crouching. This is striking
     * design against the usual "crouch to not interact" convention, but I argue
     * crouching to pickup tinies is even more natural than not crouching.
     *
     * @param event An entity about to be picked up
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @SubscribeEvent
    public static void OnEntityPickup(@NotNull ASIPSPickupToInventoryEvent event) {

        // This should always only ever run server-side

        Entity beeg = event.getEntity();
        Entity tiny = event.getTiny();

        if (beeg instanceof Player) {

            Player player = (Player) beeg;

            // Cancel if it requires crouching to pick up
            if (!ASIPickupSystemManager.canPickupIfCrouching(player.isCrouching(), tiny)) { event.setCanceled(true); }
        }
    }

    @SubscribeEvent
    public static void OnPlayerLogout(@NotNull PlayerEvent.PlayerLoggedOutEvent event) {
        //ActuallySizeInteractions.Log("ASI OCU &a PLAYER LOGOUT?! " + event.getEntity().getScoreboardName());

        // Deactivate all the dualities associated with this player
        ((ItemEntityDualityHolder) event.getEntity()).actuallysize$deactivateAllDualities();
    }

    @SubscribeEvent
    public static void OnEveryTick(@NotNull SCHTenTicksEvent event) {

        // Client resolves enqueued packets
        if (event.isClientSide()) {
            ASIClientsidePacketHandler.tryResolveAllEnqueued();
        }
    }

    @SubscribeEvent
    public static void OnEverySecond(@NotNull SCHTwentyTicksEvent event) {

        // Client resolves enqueued packets
        if (!event.isClientSide()) {
            ASIPickupSystemManager.saveAllActiveEntityCounterparts();
        }
    }

    /*
    @SubscribeEvent
    public static void OnEvery5Sec(@NotNull GOOMHundredTicksEvent event) {

        // Only on server
        if (!event.isClientSide()) {

            MinecraftServer server = event.getServer();
            PlayerList list = server.getPlayerList();
            for (ServerPlayer player : list.getPlayers()) {
                ActuallySizeInteractions.Log("ASI EEL &3 E5S &7 Release-Ticking Player &f " + player.getScoreboardName());

                // Silly holder test
                ItemEntityDualityHolder holder = (ItemEntityDualityHolder) player;
                for (Map.Entry<EquipmentSlot, ? extends EntityDualityCounterpart> entry : holder.actuallysize$getHeldItemDualities().entrySet()) {

                    Entity entity = (Entity) entry.getValue();

                    // Has something in the slot? Escape!
                    ActuallySizeInteractions.Log("ASI EEL &3 E5S &7 Releasing entity &f " + entity.getScoreboardName());
                    ASIPSDualityEscapeAction action = new ASIPSDualityEscapeAction(entry.getValue());
                    action.tryResolve();
                }
            }
        }
    }   //*/
}
