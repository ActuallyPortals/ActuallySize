package actually.portals.ActuallySize.pickup;

import actually.portals.ActuallySize.ActuallyClientConfig;
import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.netcode.packets.clientbound.ASINCHoldPointsSyncReply;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityAction;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityDeactivationAction;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityEscapeAction;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityFluxAction;
import actually.portals.ActuallySize.pickup.events.ASIPSBuildLocalPlayerHoldPointsEvent;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSHoldPointRegistry;
import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoints;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSRegisterableHoldPoint;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSStaticHoldRegistry;
import actually.portals.ActuallySize.pickup.item.ASIPSHeldEntityItem;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemDualityCounterpart;
import gunging.ootilities.GungingOotilitiesMod.exploring.entities.ISEExplorerStatements;
import gunging.ootilities.GungingOotilitiesMod.exploring.players.ISPExplorerStatements;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * The class that handles the Entity Pickup system of the mod,
 * focusing on turning entities into items, turning them back,
 * and what happens while they are items.
 * <p>
 * As far as Player entities are concerned, this system only
 * applies while they are kept outside the inventory dimension.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIPickupSystemManager {

    /**
     * Registry of items added by this mod
     *
     * @since 1.0.0
     */
    public static final DeferredRegister<Item> ITEM_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, ActuallySizeInteractions.MODID);

    /**
     * An item that represents a non-player entity while being held as an item
     *
     * @since 1.0.0
     */
    public static final RegistryObject<Item> HELD_LIVING_ENTITY = ITEM_REGISTRY.register("held_living_entity",
            () -> new ASIPSHeldEntityItem(new Item.Properties().stacksTo(1).food(new FoodProperties.Builder().alwaysEat().nutrition(2).saturationMod(2f).build())));

    /**
     * An item that represents a non-living entity while being held as an item
     *
     * @since 1.0.0
     */
    public static final RegistryObject<Item> HELD_NONLIVING_ENTITY = ITEM_REGISTRY.register("held_nonliving_entity",
            () -> new ASIPSHeldEntityItem(new Item.Properties().stacksTo(1)));

    /**
     * An item that represents a player while being held as an item
     *
     * @since 1.0.0
     */
    public static final RegistryObject<Item> HELD_PLAYER = ITEM_REGISTRY.register("held_player",
            () -> new ASIPSHeldEntityItem(new Item.Properties().stacksTo(1).food(new FoodProperties.Builder().alwaysEat().nutrition(2).saturationMod(2f).build())));

    /**
     * An index of all active Item-Entity dualities.
     *
     * @since 1.0.0
     */
    @NotNull public static HashMap<UUID, Entity> activeEntityDualityCounterparts = new HashMap<>();

    /**
     * @param entity Entity to add to the Item-Entity duality index
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void registerActiveEntityCounterpart(@NotNull Entity entity) {
        activeEntityDualityCounterparts.put(entity.getUUID(), entity);
    }

    /**
     * @param entity Entity to remove from to the Item-Entity duality index
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void unregisterActiveEntityCounterpart(@NotNull Entity entity) {
        activeEntityDualityCounterparts.remove(entity.getUUID());
    }

    /**
     * This will iterate through all active entity counterparts,
     * also keeping track of those invalid ones to remove.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void saveAllActiveEntityCounterparts() {

        // Cancel if empty
        if (activeEntityDualityCounterparts.isEmpty()) { return; }

        // Ready to clear the bad ones
        ArrayList<Entity> toRemove = new ArrayList<>();

        /*
         * Iterate all the active entity counterpart and save them into their item
         */
        for (Entity entityCounterpart : activeEntityDualityCounterparts.values()) {
            EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) entityCounterpart;
            ItemStack itemCounterpart = dualityEntity.actuallysize$getItemCounterpart();

            // If it doesn't have an item, it is invalid
            if (itemCounterpart == null) {
                toRemove.add(entityCounterpart);
                continue;
            }

            // If it is somehow removed or dead, it is also invalid
            if (!entityCounterpart.isAlive()) {
                toRemove.add(entityCounterpart);
                continue;
            }

            // Skip player, those need no saving in the item
            if (entityCounterpart instanceof Player) {
                /*HDA*/ActuallySizeInteractions.Log("ASI &4 HDM &r Ticking player duality " + entityCounterpart.getScoreboardName() + " held by " + ((Entity) dualityEntity.actuallysize$getItemEntityHolder()).getScoreboardName());
                continue; }

            // Save it
            saveNonPlayerIntoItemNBT(entityCounterpart, itemCounterpart);
        }

        /*
         * Remove the removed ones
         */
        for (Entity entityCounterpart : toRemove) {

            // Identify the parts
            EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) entityCounterpart;
            ItemStack itemCounterpart = dualityEntity.actuallysize$getItemCounterpart();

            /*
             * If it doesn't have an item, that means it was partially unregistered from ASI
             * which is weird, not much I can do about that tbh.
             */
            if (itemCounterpart == null) {

                /*
                 * Strictly speaking, if they are here, they haven't been unregistered.
                 *
                 * ASI will unregister them whenever we are saving them to the item, so
                 * we can be sure this is only happening when the entities were removed
                 * by reasons outside ASI. Then we also destroy the item, for compatibility
                 * reasons since anything that is removing entities will naturally want
                 * them gone for good.
                 *
                 * Anyway, this is called below by the action so it is only necessary here
                 */
                unregisterActiveEntityCounterpart(entityCounterpart);

                ActuallySizeInteractions.Log("ASI PSM &4 PARTIALLY-UNREGISTERED ENTITY COUNTERPART FOUND. This should be impossible. ");
                continue;
            }

            /*
             * The other reason they are here is that they are dying or got sent
             * to the nether or something. The point is they were removed from
             * the world or are being removed from it.
             */
            if (!entityCounterpart.isAlive()) {
                ASIPSDualityEscapeAction action = new ASIPSDualityEscapeAction(dualityEntity);
                action.tryResolve();
            }
        }
    }

    /**
     * Load this system onto the mod during mod loading initialization
     *
     * @param context Mod Loading context
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void OnModLoadInitialize(FMLJavaModLoadingContext context) {
        ITEM_REGISTRY.register(context.getModEventBus());
        registerASIHoldPoints();
    }

    /**
     * Some entities are interacted with when right-clicking them,
     * for example villagers open their trades or mine carts are
     * ridden. Then you'd only want to pick them up if you are
     * crouching for example to allow beegs to still trade.
     *
     * @param isCrouching If the beeg is currently crouching
     *
     * @param tiny The tiny being picked uo
     *
     * @return If this entity + crouching state combination allows to pick up the tiny
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static boolean canPickupIfCrouching(boolean isCrouching, @NotNull Entity tiny) {

        // Villagers need crouching
        if (tiny instanceof Merchant) { return isCrouching; }
        if (tiny instanceof ContainerEntity) { return isCrouching; }
        if (tiny instanceof AbstractMinecart) { return isCrouching; }
        if (tiny instanceof ItemFrame) { return isCrouching; }
        if (tiny instanceof Boat) { return isCrouching; }

        // Allow all others
        return true;
    }

    /**
     * In here, strip is not so much as removing their clothes but preparing
     * this entity to be removed from the world abruptly. Some interactions
     * of this mod will turn entities into items so the original must vanish!
     *
     * @param targetEntity The entity to "strip"
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void stripEntity(@NotNull Entity targetEntity) {

        // Remove passengers
        //for (var passenger : targetEntity.getPassengers()) { passenger.stopRiding(); }

        // Remove vehicle
        targetEntity.stopRiding();

        // Remove leash
        if(targetEntity instanceof Mob mob && ((Mob) targetEntity).isLeashed() && targetEntity.shouldBeSaved()) {
            if(mob.isLeashed()){ mob.dropLeash(true, true); } }

        // Remove raid
        if (targetEntity instanceof Raider raider && raider.hasActiveRaid()) {
            //noinspection DataFlowIssue
            raider.getCurrentRaid().removeFromRaid(raider, false); }

        // Remove villager specs
        if (targetEntity instanceof Villager villager) {
            villager.releasePoi(MemoryModuleType.HOME);
            villager.releasePoi(MemoryModuleType.JOB_SITE);
            villager.releasePoi(MemoryModuleType.POTENTIAL_JOB_SITE);
            villager.releasePoi(MemoryModuleType.MEETING_POINT); }
    }

    /**
     * This method does not remove the entity and just updates the NBT tag in the item.
     *
     * @param tiny The entity being saved into an item NBT
     * @param item The item where this entity will be saved
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void saveNonPlayerIntoItemNBT(@NotNull Entity tiny, @NotNull ItemStack item) {
        if (tiny instanceof Player) { throw new UnsupportedOperationException("The tiny cannot be a player, the method name explictly says so! "); }

        // Extract original UUID
        UUID original = null;
        ItemDualityCounterpart itemCounterpart = (ItemDualityCounterpart) (Object) item;
        if (itemCounterpart.actuallysize$isDualityActive()) {
            Entity enclosed = itemCounterpart.actuallysize$getEnclosedEntity(tiny.level());
            if (enclosed == null) { throw new UnsupportedOperationException("An active Item-Entity duality has no enclosed entity? Impossible. "); }
            original = enclosed.getUUID();
        }

        // Extract entity NBT
        ASIPickupSystemManager.stripEntity(tiny);
        CompoundTag entityTag = ASIPickupSystemManager.saveEntityToTag(tiny, original);
        Component heldName = tiny.getName();

        // Update item
        item.getOrCreateTag().put(ASIPSHeldEntityItem.TAG_ENTITY, entityTag);
        item.getOrCreateTag().putUUID(ASIPSHeldEntityItem.TAG_ENTITY_UUID, tiny.getUUID());
        item.getOrCreateTag().putString(ASIPSHeldEntityItem.TAG_ENTITY_NAME, heldName.getString());
    }

    /**
     * This method does not remove the entity and just updates the NBT tag in the item.
     *
     * @param tiny The entity being saved into an item NBT
     * @param item The item where this entity will be saved
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void inscribePlayerIntoItemNBT(@NotNull Player tiny, @NotNull ItemStack item) {

        // Extract entity NBT
        CompoundTag entityTag = item.getOrCreateTag();
        entityTag.putUUID(ASIPSHeldEntityItem.TAG_ENTITY_UUID, tiny.getUUID());
        entityTag.putString(ASIPSHeldEntityItem.TAG_ENTITY_NAME, tiny.getScoreboardName());
    }

    /**
     * @param tiny The tiny to inscribe onto a held-entity item
     *
     * @return The item with this entity inscribed
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public static ItemStack generateHeldItem(@NotNull Entity tiny) {

        // Use the appropriate item
        ItemStack ret = tiny instanceof Player ? new ItemStack(ASIPickupSystemManager.HELD_PLAYER.get()) :
                tiny instanceof LivingEntity ? new ItemStack(ASIPickupSystemManager.HELD_LIVING_ENTITY.get()) :
                        new ItemStack(ASIPickupSystemManager.HELD_NONLIVING_ENTITY.get());

        // Inscribe entity
        if (tiny instanceof Player) { ASIPickupSystemManager.inscribePlayerIntoItemNBT((Player) tiny, ret); }
        else { ASIPickupSystemManager.saveNonPlayerIntoItemNBT(tiny, ret); }

        // Return
        return ret;
    }

    /**
     * Checks if this player is inscribed into this item NBT tag
     *
     * @param tiny The entity that may be saved into an item NBT
     * @param item The item where this entity is supposed to have been saved
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static boolean isPlayerInscribedInNBT(@NotNull Player tiny, @NotNull ItemStack item) {

        // Check the item tag
        return tiny.getUUID().equals(getInscribedPlayer(item));
    }
    /**
     * @param item The item where a player is supposed to have been saved
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static UUID getInscribedPlayer(@NotNull ItemStack item) {

        // Check existing tags
        CompoundTag tag = item.getTag();
        if (tag == null) { return null; }

        // If anything is inscribed, that's it
        try {
            return tag.getUUID(ASIPSHeldEntityItem.TAG_ENTITY_UUID);

        // Failure
        } catch (IllegalArgumentException ignored) { return null; }
    }

    /**
     * Do not forget to add your entity to the world afterward,
     * if so you wish, using {@link Level#addFreshEntity(Entity)}
     *
     * @param level The world to define this entity in, though it does not spawn yet
     * @param tag The compound tag that encodes for an entity
     *
     * @return The entity rebuilt from a compound tag
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable
    public static Entity loadEntityFromTag(@NotNull CompoundTag tag, @NotNull Level level) {
        return loadEntityFromTag(tag, level, null);
    }

    /**
     * Do not forget to add your entity to the world afterward,
     * if so you wish, using {@link Level#addFreshEntity(Entity)}
     *
     * @param level The world to define this entity in, though it does not spawn yet
     * @param tag The compound tag that encodes for an entity
     * @param overrideUUID Load this entity with this UUID instead of whatever it has saved
     *
     * @return The entity rebuilt from a compound tag
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable
    public static Entity loadEntityFromTag(@NotNull CompoundTag tag, @NotNull Level level, @Nullable UUID overrideUUID) {

        // Decide if replacing the UUID is worth it
        CompoundTag effectiveTag = tag;
        if (overrideUUID != null && effectiveTag.hasUUID(Entity.UUID_TAG)) {
            effectiveTag = tag.copy();
            effectiveTag.putUUID(Entity.UUID_TAG, overrideUUID);}

        // Load entity from this tag
        Entity entity = null;
        if (tag.contains("id")) { entity = EntityType.loadEntityRecursive(effectiveTag, level, Function.identity()); }

        // The entity was empty? Just create a new one then
        if (entity == null) {
            String id = effectiveTag.getString(ASIPSHeldEntityItem.TAG_ENTITY_ID);
            Optional<EntityType<?>> type = EntityType.byString(id);
            if (type.isPresent()) { entity = type.get().create(level); } }

        // Yay
        return entity;
    }

    /**
     * @param entity The entity to turn into a compound tag
     *
     * @return A compound tag that can be decoded by {@link #loadEntityFromTag(CompoundTag, Level)}
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public static CompoundTag saveEntityToTag(@NotNull Entity entity, @Nullable UUID originalUUID) {


        // Entities can already be saved into tag compounds
        CompoundTag tag = new CompoundTag();
        try {

            // Write its Encode ID
            String encodeID = entity.getEncodeId();
            tag.putString("id", encodeID == null ? EntityType.getKey(entity.getType()).toString() : encodeID);
            entity.saveWithoutId(tag);

        // Cancel if exception
        } catch (Throwable e) { tag = new CompoundTag(); }

        // This entity has no NBT, store only its Entity Type
        if (tag.isEmpty()) {
            tag.putString(ASIPSHeldEntityItem.TAG_ENTITY_ID, EntityType.getKey(entity.getType()).toString());

        // If it is an actual entity, attempt to restore its original UUID
        } else {

            // Override UUID
            if (originalUUID != null) { tag.putUUID(Entity.UUID_TAG, originalUUID);}
        }

        // Done
        return tag;
    }

    /**
     * Sometimes, dualities are unregistered from one slot only to be
     * registered to another slot. We are trying to optimize this, so
     * maybe if we detect when this happens we can coalesce the duality
     * activation and deactivations to smoothen them.
     *
     * @since 1.0.0
     */
    @NotNull static HashMap<Entity, ArrayList<ASIPSDualityAction>> dualityFlux = new HashMap<>();

    /**
     * Sometimes, duality actions come in pairs - deactivate it somewhere only
     * to reactivate it elsewhere. This generates waste and it is unsmooth. Then
     * it is useful to compare all the actions that happened together to figure out
     * if there's a way to combine them into one transfer action instead.
     * <p>
     * However, some other duality actions must resolve immediately, so this is not
     * desirable every time. That's why this is a special method / procedure called
     * from places where such transitions are expected to happen.
     *
     * @param action The entity duality action happening this instant
     *
     * @return If this action was successfully registered to the flux pass to be resolved
     *         at the beginning of next server tick.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static boolean probableDualityFlux(@NotNull ASIPSDualityAction action) {

        if (dualityFluxing) { return false; }

        // Duality flux operations only make sense while the Entity Counterpart exists in the world
        Entity entityCounterpart = action.getEntityCounterpart();
        if (entityCounterpart == null) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &3 IED 1F &7 Probable-Flux REJECTION: &c No Entity");
            return false; }

        // Fetch the list of duality flux for this tick
        ArrayList<ASIPSDualityAction> acts = dualityFlux.computeIfAbsent(entityCounterpart, k -> new ArrayList<>());

        // Include this action
        acts.add(action);
        /*HDA*/ActuallySizeInteractions.Log("ASI &3 IED 1F &7 Probable-Flux &a ACCEPTED " + entityCounterpart.getScoreboardName());
        return true;
    }

    /**
     * The point of duality flux is to consolidate deactivations immediately
     * followed by activations with the aim to reuse the duality entity that
     * was already alive in the world.
     * <p>
     * Sometimes, the entity will escape or deactivate terminally due to anything
     * that evaluates instantly, such as a player disconnecting, and makes no sense
     * anymore to resolve at the beginning of next tick.
     * <p>
     * In such cases, this method will throw away all flux associated with this entity
     *
     * @param who Entity who is being terminated from the duality flux evaluation
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void clearDualityFlux(@NotNull Entity who) { if (dualityFluxing) { return; } dualityFlux.remove(who); }

    /**
     * When currently in a duality flux evalutation
     *
     * @since 1.0.0
     */
    static boolean dualityFluxing;

    /**
     * Checks the item-entity duality activations and deactivations
     * of last tick to see
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void resolveDualityFlux() {
        if (dualityFlux.isEmpty()) { return; }
        dualityFluxing = true;

        // Clear map
        HashMap<Entity, ArrayList<ASIPSDualityAction>> fluxx = new HashMap<>();

        // We simply resolve all the flux calculations for all the entities in flux
        for (Map.Entry<Entity, ArrayList<ASIPSDualityAction>> flux : dualityFlux.entrySet()) {
            ArrayList<ASIPSDualityAction> actions = flux.getValue();
            if (actions.isEmpty()) { continue; }
            Entity entityCounterpart = flux.getKey();
            ArrayList<ASIPSDualityAction> flex = new ArrayList<>();

            // Results of flux
            ASIPSDualityAction from = null;
            ASIPSDualityAction to = null;

            // Changes in flux
            ASIPSDualityAction two = null;

            // Calculate the ultimate changes
            for (ASIPSDualityAction act : actions) {
                boolean isFrom = (act instanceof ASIPSDualityDeactivationAction) || (act instanceof ASIPSDualityEscapeAction);

                // This action is removing the entity from somewhere
                if (isFrom) {

                    /*
                     * If this removes an immediate "to" of the same slot,
                     * it's kinda silly that they ever were added to that
                     * slot in the first place.
                     */
                    if (to != null && act.getStackLocation() != null && act.getStackLocation().equals(to.getStackLocation())) {

                        // Restore "to" to its previous state and ignore this interaction completely
                        to = two;
                        continue;
                    }

                    // When we haven't decided where we are from
                    if (from == null) { from = act; }

                // This action is placing the entity somewhere (activating it)
                } else {

                    // Even if we know where we are going, the final destination gets overwritten
                    two = to;
                    to = act;
                }
            }

            // There is no destination?
            if (to == null) {

                // At least resolve from if it exists
                if (from != null) {

                    // Give it one more pass and then let it resolve
                    if (from.getAttempts() < 1) {
                        /*HDA*/ActuallySizeInteractions.Log("ASI &a RDF &f Flux [From] Deferred: &e " + (from.getStackLocation() == null ? "UNKNOWN" : from.getStackLocation().getStatement()));
                        flex.add(from); from.logAttempt(); } else { from.tryResolve();  }
                }

            // There is a destination
            } else {

                /*
                 * Sometimes, an active entity duality swaps slot with another, then
                 * both are registered to their [To] slots and no [From] slot. This
                 * is undesirable, check if this entity is already active and thus
                 * has a [From] slot.
                 *
                 * But also, this only makes sense if it will be allowed and verified.
                 * Otherwise, we get a random deactivation event later on that is
                 * extraneous.
                 */
                if (from == null && to.isVerified() && to.isAllowed()) {
                    EntityDualityCounterpart entityDuality = (EntityDualityCounterpart) entityCounterpart;
                    if (entityDuality.actuallysize$isActive()) { from = new ASIPSDualityDeactivationAction(entityDuality); }
                }

                // At least resolve to, since it existed
                if (from == null) {

                    // Give it one more pass and then let it resolve
                    if (to.getAttempts() < 1) {
                        /*HDA*/ActuallySizeInteractions.Log("ASI &a RDF &f Flux [To] Deferred: &e " + (to.getStackLocation() == null ? "UNKNOWN" : to.getStackLocation().getStatement()));
                        flex.add(to); to.logAttempt(); } else { to.tryResolve();  }
                } else {

                    // Both to and from, turn this into a FLUX action and run it
                    ASIPSDualityFluxAction action = new ASIPSDualityFluxAction(from, to);
                    if (!action.tryResolve()) {

                        // If the flux fails to resolve, resolve the two parts individually
                        from.tryResolve();
                        to.tryResolve();
                    }
                }
            }

            // Remember for next tick
            if (!flex.isEmpty()) { fluxx.put(entityCounterpart, flex); }

            // For now, quote me the event
            /*HDA*/ActuallySizeInteractions.Log("ASI &a RDF &7 Flux From: &e " + (from == null ? "null" : (from.getStackLocation() == null ? "UNKNOWN" : from.getStackLocation().getStatement())));
            /*HDA*/ActuallySizeInteractions.Log("ASI &a RDF &7 Flux To: &6 " + (to == null ? "null" : (to.getStackLocation() == null ? "UNKNOWN" : to.getStackLocation().getStatement())));
        }

        // The duality flux of last tick is officially resolved
        dualityFlux = fluxx;
        dualityFluxing = false;
    }

    //region Hold Points
    /**
     * A default and global registry of hold points
     *
     * @since 1.0.0
     */
    @NotNull public static ASIPSStaticHoldRegistry HOLD_POINT_REGISTRY = new ASIPSStaticHoldRegistry();

    /**
     * Registers all ASI-included hold points onto the hold points system
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void registerASIHoldPoints() {
        HOLD_POINT_REGISTRY.registerHoldPoint(ISEExplorerStatements.MAINHAND, ASIPSHoldPoints.MAINHAND);
        HOLD_POINT_REGISTRY.registerHoldPoint(ISEExplorerStatements.OFFHAND, ASIPSHoldPoints.OFFHAND);
        HOLD_POINT_REGISTRY.registerHoldPoint(ISEExplorerStatements.HEAD, ASIPSHoldPoints.HEAD);
        HOLD_POINT_REGISTRY.registerHoldPoint(ISEExplorerStatements.CHEST, ASIPSHoldPoints.RIGHT_SHOULDER);
        HOLD_POINT_REGISTRY.registerHoldPoint(ISEExplorerStatements.LEGS, ASIPSHoldPoints.LEFT_POCKET);
        HOLD_POINT_REGISTRY.registerHoldPoint(ISEExplorerStatements.FEET, ASIPSHoldPoints.RIGHT_BOOT);
        HOLD_POINT_REGISTRY.registerHoldPoint(ISPExplorerStatements.CURSOR, ASIPSHoldPoints.PINCH);
    }

    /**
     * @return A hold point registry as specified by the local player in the config
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public static ASIPSHoldPointRegistry buildLocalPlayerPreferredHoldPoints() {
        ASIPSHoldPointRegistry ret = new ASIPSHoldPointRegistry();

        // Read from client config
        ASIPSRegisterableHoldPoint point = HOLD_POINT_REGISTRY.getHoldPoint(ActuallyClientConfig.holdHead);
        if (point != null) { ret.registerHoldPoint(ISEExplorerStatements.HEAD, point); }

        point = HOLD_POINT_REGISTRY.getHoldPoint(ActuallyClientConfig.holdChest);
        if (point != null) { ret.registerHoldPoint(ISEExplorerStatements.CHEST, point); }

        point = HOLD_POINT_REGISTRY.getHoldPoint(ActuallyClientConfig.holdLegs);
        if (point != null) { ret.registerHoldPoint(ISEExplorerStatements.LEGS, point); }

        point = HOLD_POINT_REGISTRY.getHoldPoint(ActuallyClientConfig.holdFeet);
        if (point != null) { ret.registerHoldPoint(ISEExplorerStatements.FEET, point); }

        point = HOLD_POINT_REGISTRY.getHoldPoint(ActuallyClientConfig.holdMainhand);
        if (point != null) { ret.registerHoldPoint(ISEExplorerStatements.MAINHAND, point); }

        point = HOLD_POINT_REGISTRY.getHoldPoint(ActuallyClientConfig.holdOffhand);
        if (point != null) { ret.registerHoldPoint(ISEExplorerStatements.OFFHAND, point); }

        point = HOLD_POINT_REGISTRY.getHoldPoint(ActuallyClientConfig.holdCursor);
        if (point != null) { ret.registerHoldPoint(ISPExplorerStatements.CURSOR, point); }

        // Run event
        ASIPSBuildLocalPlayerHoldPointsEvent broadcast = new ASIPSBuildLocalPlayerHoldPointsEvent(ret);
        MinecraftForge.EVENT_BUS.post(broadcast);

        // Finish
        return ret;
    }

    /**
     * Receives a list of namespaced statements to adjust their network index
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void receiveNetworkSync(@NotNull ASINCHoldPointsSyncReply packet) {
        HashMap<String, ASIPSRegisterableHoldPoint> statements = HOLD_POINT_REGISTRY.getByNamespaces().get(packet.getNamespace());
        for (Map.Entry<String, Integer> syn : packet.getSynced().entrySet()) {
            ASIPSRegisterableHoldPoint found = statements.get(syn.getKey());
            if (found == null) { continue; }

            // Adopt index
            found.setOrdinal(syn.getValue());
            HOLD_POINT_REGISTRY.getByOrdinal().put(syn.getValue(), found);
        }
    }
    //endregion
}
