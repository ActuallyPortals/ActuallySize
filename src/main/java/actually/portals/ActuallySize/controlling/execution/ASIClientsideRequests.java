package actually.portals.ActuallySize.controlling.execution;

import actually.portals.ActuallySize.ActuallyClientConfig;
import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.netcode.ASINetworkManager;
import actually.portals.ActuallySize.netcode.packets.serverbound.ASINSPreferredSize;
import actually.portals.ActuallySize.pickup.actions.ASIPSHoldingSyncAction;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import gunging.ootilities.GungingOotilitiesMod.exploring.entities.ISEEquipmentSlotted;
import gunging.ootilities.GungingOotilitiesMod.instants.GOOMClientsidePlayerLoginEvent;
import gunging.ootilities.GungingOotilitiesMod.scheduling.SchedulingManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
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
     * True after the AFTER_RENDERING_SKY render step and before AFTER_RENDERING_ENTITIES
     *
     * @since 1.0.0
     */
    public static boolean RENDERING_LEVEL_ENTITIES;

    /**
     * True after the AFTER_RENDERING_LEVEL render step and before AFTER_RENDERING_SKY
     *
     * @since 1.0.0
     */
    public static boolean OFF_LEVEL_RENDERING;

    /**
     * @param entity The entity being rendered
     *
     * @return If it should not be rendered because the GUI overlay is going to render it
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static boolean isInvisibleBecauseHeld(@NotNull Entity entity) {

        // If we are not rendering level entities, then this method has no meaning
        if (!RENDERING_LEVEL_ENTITIES) { return false; }

        // The camera mode must be in first person for this to have any meaning
        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson()) { return false; }

        // It is only invisible because held... when held
        EntityDualityCounterpart entityDuality = (EntityDualityCounterpart) entity;
        if (!entityDuality.actuallysize$isHeld()) { return false; }

        // The holder must not only exist, but also be the Local Player
        ItemEntityDualityHolder holder = entityDuality.actuallysize$getItemEntityHolder();
        if (!(holder instanceof Entity)) { return false; }
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) { return false; }
        if (!localPlayer.getUUID().equals(((Entity) holder).getUUID())) { return false; }

        /*
         * Only really hides the entities in the hands, since
         * those are the ones that are being rendered in overlay
         */
        ItemStackLocation<? extends Entity> stackLocation = entityDuality.actuallysize$getItemStackLocation();
        if (stackLocation == null) { return false; }
        if (!(stackLocation.getStatement() instanceof ISEEquipmentSlotted)) { return false; }
        EquipmentSlot slot = ((ISEEquipmentSlotted) stackLocation.getStatement()).getEquipmentSlot();
        return slot.equals(EquipmentSlot.MAINHAND) || slot.equals(EquipmentSlot.OFFHAND);
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @SubscribeEvent
    public static void OnRenderStageAdvance(RenderLevelStageEvent event) {
        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_SKY)) {
            OFF_LEVEL_RENDERING = false;
            RENDERING_LEVEL_ENTITIES = true;
        } else if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_ENTITIES)) {
            RENDERING_LEVEL_ENTITIES = false;
        } else if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_LEVEL)) {
            OFF_LEVEL_RENDERING = true;
        }
    }

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

            // Initial world join sync
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
