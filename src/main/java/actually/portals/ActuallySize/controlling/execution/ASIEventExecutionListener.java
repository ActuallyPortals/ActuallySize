package actually.portals.ActuallySize.controlling.execution;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallyServerConfig;
import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.netcode.packets.serverbound.ASINSPreferredSize;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityActivationAction;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityDeactivationAction;
import actually.portals.ActuallySize.netcode.ASIClientsidePacketHandler;
import actually.portals.ActuallySize.pickup.ASIPickupSystemManager;
import actually.portals.ActuallySize.pickup.actions.ASIPSHoldingSyncAction;
import actually.portals.ActuallySize.pickup.events.ASIPSFoodPropertiesEvent;
import actually.portals.ActuallySize.pickup.events.ASIPSPickupToInventoryEvent;
import actually.portals.ActuallySize.pickup.item.ASIPSHeldEntityItem;
import actually.portals.ActuallySize.pickup.mixininterfaces.*;
import actually.portals.ActuallySize.world.mixininterfaces.PreferentialOptionable;
import gunging.ootilities.GungingOotilitiesMod.events.extension.ServersideEntityEquipmentChangeEvent;
import gunging.ootilities.GungingOotilitiesMod.exploring.players.ISPExplorerStatements;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import gunging.ootilities.GungingOotilitiesMod.scheduling.SCHTenTicksEvent;
import gunging.ootilities.GungingOotilitiesMod.scheduling.SCHTwentyTicksEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
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

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @SubscribeEvent
    public static void OnPlayerRespawn(@NotNull PlayerEvent.Clone event) {

        // Only runs on server
        if (!(event.getEntity() instanceof ServerPlayer)) { return; }
        ServerPlayer player = (ServerPlayer) event.getEntity();

        // On death, restore prefs
        if (event.isWasDeath()) {
            PreferentialOptionable optionable = (PreferentialOptionable) player;
            optionable.actuallysize$setPreferredOptionsApplied(false);

            // Get prefs
            ASINSPreferredSize prefs = ASINSPreferredSize.GetPreferredSize(player);
            if (prefs == null) { return; }

            // Apply prefs
            prefs.applyTo(player); }

        // Sync hold points and dualities to client
        HoldPointConfigurable newer = (HoldPointConfigurable) event.getEntity();
        ASIPSHoldingSyncAction syncing = new ASIPSHoldingSyncAction(event.getEntity());
        syncing.withActiveDualities();
        syncing.withConfigurables();
        syncing.withBroadcast(newer.actuallysize$getLocalHoldPoints().getRegisteredPoints());
        syncing.resolve();

        /*HDA*/ActuallySizeInteractions.LogHDA(ASIEventExecutionListener.class, "DTR", "Respawn copy over hold points &e x{0}", newer.actuallysize$getLocalHoldPoints().getRegisteredPoints().size());
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @SubscribeEvent
    public static void OnTeleportationHold(@NotNull EntityTeleportEvent event) {

        /*
         * If the entity is held, teleporting away will make it stop being
         * held (normally, unless like, enchanted or something who knows).
         */
        EntityDualityCounterpart entityDuality = (EntityDualityCounterpart) event.getEntity();
        if (!entityDuality.actuallysize$isHeld()) { return; }

        //noinspection DataFlowIssue
        if (entityDuality.actuallysize$getHoldPoint().canBeEscapedByTeleporting(entityDuality.actuallysize$getItemEntityHolder(), entityDuality, event)) {

            // If it can be escaped by teleporting, escape the duality
            entityDuality.actuallysize$escapeDuality();
        } else {
            event.setCanceled(true);
        }
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void OnTeleportation(@NotNull EntityTeleportEvent event) {

        // Only for server players
        if (!(event.getEntity() instanceof ServerPlayer)) { return; }

        // We don't care if it was cancelled
        if (event.isCanceled()) { return; }

        // Travelling more than 10 chunks? Request dualities
        if (!event.getPrev().closerThan(event.getTarget(), 160)) {
            HoldPointConfigurable asConfigurable = (HoldPointConfigurable) event.getEntity();

            // Sync hold points and dualities to client
            ASIPSHoldingSyncAction syncing = new ASIPSHoldingSyncAction((Player) event.getEntity());
            syncing.withActiveDualities();
            syncing.withBroadcast(asConfigurable.actuallysize$getLocalHoldPoints().getRegisteredPoints());
            syncing.resolve();

            /*HDA*/ActuallySizeInteractions.LogHDA(ASIEventExecutionListener.class, "DTR", "Teleportation synced dualities");
        }
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @SubscribeEvent
    public static void OnDimensionalTravel(@NotNull PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer)) { return; }

        // Transfer server-side hold point configuration
        HoldPointConfigurable asConfigurableOld = (HoldPointConfigurable) event.getEntity();

        /*HDA*/ActuallySizeInteractions.LogHDA(ASIEventExecutionListener.class, "DTR", "Dimensional copied over hold points &e x{0}", asConfigurableOld.actuallysize$getLocalHoldPoints().getRegisteredPoints().size());
        //asConfigurableOld.actuallysize$getLocalHoldPoints().log();

        // Sync hold point configurations to client
        ASIPSHoldingSyncAction syncing = new ASIPSHoldingSyncAction(event.getEntity());
        syncing.withConfigurables();
        syncing.withBroadcast(asConfigurableOld.actuallysize$getLocalHoldPoints().getRegisteredPoints());
        syncing.resolve();
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @SubscribeEvent
    public static void OnDualityFluxResolution(@NotNull TickEvent.ServerTickEvent event) {

        // The duality flux calculations resolve at the beginning of the server tick
        if (event.phase == TickEvent.Phase.START) { ASIPickupSystemManager.resolveDualityFlux(); }
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void OnEquipmentChange(@NotNull ServersideEntityEquipmentChangeEvent event) {
        if (!ActuallyServerConfig.enableEntityHolding) { return; }
        /*HDA*/ActuallySizeInteractions.LogHDA(true, ASIEventExecutionListener.class, "EQP", "ServersideEntityEquipmentChangeEvent");
        /*HDA*/ActuallySizeInteractions.LogHDA(ASIEventExecutionListener.class, "EQP", "Reason &f {0}", event.getReason());
        /*HDA*/ActuallySizeInteractions.LogHDA(ASIEventExecutionListener.class, "EQP", "Entity &f {0}", event.getEntity().getScoreboardName());
        /*HDA*/ActuallySizeInteractions.LogHDA(ASIEventExecutionListener.class, "EQP", "Slot &e {0}", event.getStackLocation().getStatement());
        /*HDA*/ActuallySizeInteractions.LogHDA(ASIEventExecutionListener.class, "EQP", "Item &e {0}", event.getCurrentItemStack().getDisplayName().getString());

        // Create an action and try to resolve it :based:
        ASIPSDualityActivationAction action = new ASIPSDualityActivationAction(event.getStackLocation());
        if (action.isVerified()) {
            /*HDA*/ActuallySizeInteractions.LogHDA(ASIEventExecutionListener.class, "EQP", "Verified, registering [TO] into Probable-Flux:");

            // Register to flux evaluation
            if (!ASIPickupSystemManager.probableDualityFlux(action)) {
                /*HDA*/ActuallySizeInteractions.LogHDA(ASIEventExecutionListener.class, "EQP", "Inadmissible to Probable-Flux, resolving:");
                action.tryResolve(); }


        // If it does not verify, attempt to deactivate it
        } else {
            /*HDA*/ActuallySizeInteractions.LogHDA(ASIEventExecutionListener.class, "EQP", "Unverified, registering [FROM] into Probable-Flux:");

            /*
             * So we failed to verify a new Item Duality. Most
             * likely that is because this is not an Item-Duality
             *
             * Ultimately, equipment changed, so try to deactivate
             * if existing a current one in this slot :p
             */
            ASIPSDualityDeactivationAction inaction = new ASIPSDualityDeactivationAction(event.getStackLocation());
            if (!ASIPickupSystemManager.probableDualityFlux(inaction)) {
                /*HDA*/ActuallySizeInteractions.LogHDA(ASIEventExecutionListener.class, "EQP", "Inadmissible to Probable-Flux, resolving:");
                inaction.tryResolve(); }}


        // Process hotbar changes when selected item changes, or when hotbar changes
        boolean isCursor = event.getStackLocation().getStatement().equals(ISPExplorerStatements.CURSOR);
        if (event.getEntity() instanceof ServerPlayer && (isCursor || event.getStackLocation().getStatement().equals(ISPExplorerStatements.MAINHAND))) {
            ASIPickupSystemManager.processHotbarSlots((ServerPlayer) event.getEntity(), isCursor);
        }

        /*HDA*/ActuallySizeInteractions.LogHDA(false, ASIEventExecutionListener.class, "EQP", "ServersideEntityEquipmentChangeEvent");
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
    public static void OnASIEntityPickup(@NotNull ASIPSPickupToInventoryEvent event) {

        // This should always only ever run server-side

        Entity beeg = event.getEntity();
        Entity tiny = event.getTiny();

        if (beeg instanceof Player) {

            Player player = (Player) beeg;

            // Cancel if it requires crouching to pick up
            if (!ASIPickupSystemManager.canPickupIfCrouching(player.isCrouching(), tiny)) { event.setCanceled(true); }
        }
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @SubscribeEvent
    public static void OnPlayerLogout(@NotNull PlayerEvent.PlayerLoggedOutEvent event) {
        //ActuallySizeInteractions.Log("ASI OCU &a PLAYER LOGOUT?! " + event.getEntity().getScoreboardName());

        // Deactivate all the dualities associated with this player
        ((ItemEntityDualityHolder) event.getEntity()).actuallysize$deactivateAllDualities();
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @SubscribeEvent
    public static void OnEveryTenTicks(@NotNull SCHTenTicksEvent event) {

        // Client resolves enqueued packets
        if (event.isClientSide()) {
            ASIClientsidePacketHandler.tryResolveAllEnqueued();
        }
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @SubscribeEvent
    public static void OnEverySecond(@NotNull SCHTwentyTicksEvent event) {

        // Client resolves enqueued packets
        if (!event.isClientSide()) {
            ASIPickupSystemManager.saveAllActiveEntityCounterparts();
        }
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @SubscribeEvent
    public static void OnUseItemStart(@NotNull LivingEntityUseItemEvent.Start event) {

        /*
         * "Hungry Beegs" option that makes players eat food
         * faster or slower depending on their size, how silly.
         *
         * ASI Held Entities are actually exempted. Players
         * straight-up take twice as long just for the hell
         * of it.
         */
        if (!(event.getEntity() instanceof Player)) { return; }
        if (!event.getItem().isEdible()) { return; }
        //FOO//ActuallySizeInteractions.Log("ASI &1 FOO &7 Recalculating food duration, clientside? " + event.getEntity().level().isClientSide);

        // ASI Held Entities bypass this nerf
        UseTimed cacheTime = ((UseTimed) (Object) event.getItem());
        if (event.getItem().getItem() instanceof ASIPSHeldEntityItem) {
            if (((ASIPSHeldEntityItem) event.getItem().getItem()).isPlayer()) { event.setDuration(event.getDuration() * 2); }
            //FOO//ActuallySizeInteractions.Log("ASI &1 FOO &7 Living food exemption");
            cacheTime.actuallysize$setUseTimeTicks(event.getDuration());
            return; }
        if (!ActuallyServerConfig.hungryBeegs) { return; }

        // Okay now, the duration scales with inversely with your size
        Player player = (Player) event.getEntity();
        double size = ASIUtilities.getEffectiveSize(player);
        double sizeAmplifier = ASIUtilities.beegBalanceResist(size, 3, 0.35);

        // It also scales directly with food nutrition level
        int nutrition = event.getItem().getFoodProperties(player).getNutrition();
        if (nutrition < 1) { nutrition = 1; }
        double nutritionAmplifier = 1;

        // For tinies, food nutrition results in large increases
        if (size < 0.25) {
            nutritionAmplifier = ASIUtilities.beegBalanceEnhance((nutrition * 0.85 + 0.5) + 0.7, 8, 1);

        // When not so large, it is more of a silly gimmick
        } else {
            nutritionAmplifier = ASIUtilities.beegBalanceEnhance((nutrition + 3) * 0.25, 2, 1);
        }

        // Combine amplifiers
        int result = OotilityNumbers.ceil(event.getDuration() * sizeAmplifier * nutritionAmplifier);
        //FOO//ActuallySizeInteractions.Log("ASI &1 FOO &7 Duration food (x" + size + " = " + sizeAmplifier + ", N" + nutrition + " = " + nutritionAmplifier+ ") &b " + event.getDuration() + " &r to &3 " + result);

        // What is one more tick in the grand scheme of things?
        event.setDuration(result);
        cacheTime.actuallysize$setUseTimeTicks(event.getDuration());
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @SubscribeEvent
    public static void OnEdaciousPropertiesBuild(@NotNull ASIPSFoodPropertiesEvent event) {
        //FOO//ActuallySizeInteractions.Log("ASI &1 FOO &r Food Properties Event for &3 " + event.getEntity().getClass().getSimpleName());

        /*
         * Some entities simply have special effects
         */
        LivingEntity edacious = event.getEntity();
        if (edacious instanceof Player) {
            event.setSaturation(event.getSaturation() + (float) (10 * ASIUtilities.beegBalanceEnhance(event.getSize(), 4, 0.25)));

        // Funny glow squid with night vision
        } else if (edacious instanceof GlowSquid) {
            event.getBuilder().effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 500, 0), 1F);
        }

        // Animals have saturation
        if (edacious instanceof Animal) {
            event.setSaturation(event.getSaturation() + (float) (5 * ASIUtilities.beegBalanceEnhance(event.getSize(), 4, 0.25)));
        }

        // Skeletons have no nutritional value
        if (edacious.getType().is(EntityTypeTags.SKELETONS)) {
            event.setNutrition(0);
            event.setSaturation(0);
        }

        // Golems have no nutritional value
        if (edacious instanceof AbstractGolem) {
            event.setNutrition(0);
            event.setSaturation(0);
        }

        // Raiders have saturation
        if (edacious.getType().is(EntityTypeTags.RAIDERS)) {
            event.getBuilder().effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 500, 0), 1F);
        }
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @SubscribeEvent
    public static void OnEffectEvent(@NotNull MobEffectEvent.Added event) {
        if (!(event.getEntity() instanceof ServerPlayer)) { return; }
        if (event.getEffectInstance().isInfiniteDuration()) { return; }
        TimeDurationModifiable duration = (TimeDurationModifiable) event.getEffectInstance();

        /*
         * When ASI hunger mode is enabled, beegs are resistant to hunger effect
         */
        if (ActuallyServerConfig.hungryBeegs) {

            // If the effect being added is hunger
            if (event.getEffectInstance().getEffect().equals(MobEffects.HUNGER)) {
                double size = ASIUtilities.getEffectiveSize(event.getEntity());
                duration.actuallysize$setDuration(OotilityNumbers.ceil(duration.actuallysize$getDuration() * ASIUtilities.beegBalanceResist(size * 2, 1, 0)));
                return;
            }
        }

        /*
         * Beegs in general resist a few combat-related potion effects
         */

        // If the effect being added is combat-related
        if (event.getEffectInstance().getEffect().equals(MobEffects.POISON) ||
            event.getEffectInstance().getEffect().equals(MobEffects.WITHER) ||
            event.getEffectInstance().getEffect().equals(MobEffects.SLOW_FALLING)) {
            double size = ASIUtilities.getEffectiveSize(event.getEntity());
            duration.actuallysize$setDuration(OotilityNumbers.ceil(duration.actuallysize$getDuration() * ASIUtilities.beegBalanceResist(size * 2, 1, 0.1)));
            return;
        }

        // If the effect being added is blindness that doesn't even let you see your feet
        if (event.getEffectInstance().getEffect().equals(MobEffects.BLINDNESS)) {
            double size = ASIUtilities.getEffectiveSize(event.getEntity());
            duration.actuallysize$setDuration(OotilityNumbers.ceil(duration.actuallysize$getDuration() * ASIUtilities.beegBalanceResist(size * 3, 1, 0)));
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
