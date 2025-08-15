package actually.portals.ActuallySize.pickup.actions;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.netcode.ASINetworkManager;
import actually.portals.ActuallySize.netcode.packets.clientbound.ASINCItemEntityActivationPacket;
import actually.portals.ActuallySize.pickup.ASIPickupSystemManager;
import actually.portals.ActuallySize.pickup.events.ASIPSActivateItemEntityEvent;
import actually.portals.ActuallySize.pickup.events.ASIPSTryActivateItemEntityEvent;
import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import gunging.ootilities.GungingOotilitiesMod.exploring.players.ISPExplorerStatements;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This duality activation action is meant to activate any
 * one Item-Entity duality item. It can be anywhere and in
 * any state provided it exists. This event will:
 * <p>
 * (1) Handle clearing its previous activation state
 * <p>
 * (2) Spawn the entity and sync it over the network
 * <p>
 * (3) Set up the correct variables between Item, Entity and Holder
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIPSDualityActivationAction extends ASIPSDualityAction {

    /**
     * @see #getStackLocation()
     * @since 1.0.0
     */
    @Nullable ItemStackLocation<? extends Entity> stackLocation;
    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override public @Nullable ItemStackLocation<? extends Entity> getStackLocation() { return stackLocation; }
    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override public void setStackLocation(@Nullable ItemStackLocation<? extends Entity> isl) { stackLocation = isl; }

    /**
     * @see #getItemCounterpart()
     * @since 1.0.0
     */
    @Nullable ItemStack itemCounterpart;
    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override public @Nullable ItemStack getItemCounterpart() { return itemCounterpart; }
    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override public void setItemCounterpart(@Nullable ItemStack item) { itemCounterpart = item;}

    /**
     * @see #getEntityCounterpart()
     * @since 1.0.0
     */
    @Nullable Entity entityCounterpart;
    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override @Nullable public Entity getEntityCounterpart() { return entityCounterpart; }
    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override public void setEntityCounterpart(@Nullable Entity entity) { entityCounterpart = entity; }

    /**
     * @see #getHoldPoint()
     * @since 1.0.0
     */
    @Nullable ASIPSHoldPoint holdPoint;
    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override @Nullable public ASIPSHoldPoint getHoldPoint() { return holdPoint; }
    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override public void setHoldPoint(@Nullable ASIPSHoldPoint hold) { holdPoint = hold; }

    /**
     * The world where the Entity counterpart is found / will be found
     *
     * @since 1.0.0
     */
    @Nullable Level world;
    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable public Level getWorld() { return world; }
    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public void setWorld(@Nullable Level level) { world = level; }

    /**
     * To handle activation of Item-Entities in the client-side, where
     * "activation" means registering the trio Item-Entity-Holder.
     *
     * @param fromPacket The packet that arrived from the network
     *
     * @param world The best guess for the world where the entity and holder are.
     *              It is unknown if it is possible to receive packets from other
     *              worlds not the local player's, or if an entity duality can be
     *              activated in another world or so.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSDualityActivationAction(@NotNull ASINCItemEntityActivationPacket fromPacket, @Nullable Level world) {

        // I mean, we are checking that the world exists later on...
        if (world == null) { return; }

        // How serendipitous, the packet gives us everything
        entityCounterpart = fromPacket.getEntityCounterpart(world);
        stackLocation = fromPacket.getStackLocation(world);
        this.world = world;
        if (stackLocation != null) {
            itemCounterpart = stackLocation.getItemStack();
            holdPoint = ((ItemEntityDualityHolder) stackLocation.getHolder()).actuallysize$getHoldPoint(stackLocation);
        }
    }

    /**
     * To handle activation of Item-Entities in the server-side, where
     * "activation" means registering the trio Item-Entity-Holder,
     * spawning the entity, and syncing it to the clients.
     *
     * @param stackLocation The Item in the holder's inventory that is
     *                      producing an Item-Entity duality activation
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSDualityActivationAction(@NotNull ItemStackLocation<? extends Entity> stackLocation) {

        // The location of the item gives us the item and entity
        this.stackLocation = stackLocation;
        world = stackLocation.getHolder().level();
        holdPoint = ((ItemEntityDualityHolder) this.stackLocation.getHolder()).actuallysize$getHoldPoint(this.stackLocation);
        itemCounterpart = stackLocation.getItemStack();

        /*
         * Identify the entity in question
         *
         * This works because the server will spawn the entity
         * FROM the item by literally reading it here. When matching
         * an entity to an item in the client side, the entity must
         * be provided.
         */
        ItemDualityCounterpart itemDuality = (ItemDualityCounterpart) (Object) itemCounterpart;
        if (itemDuality != null) {
            if (itemDuality.actuallysize$isDualityActive()) {
                entityCounterpart = itemDuality.actuallysize$getEntityCounterpart();
                /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &7 Linked to counterpart? " + (entityCounterpart != null ? entityCounterpart.getScoreboardName() : "null"));
            } else {
                itemDuality.actuallysize$hasEnclosedEntity(world);  // Readies the enclosed entity for the non-player case
                entityCounterpart = itemDuality.actuallysize$readyEntityCounterpart(world);
                /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &7 Genned Entity? " + (entityCounterpart != null ? entityCounterpart.getScoreboardName() : "null"));
            }
        }
    }

    /**
     * To handle activation of Item-Entities in the server-side, where
     * "activation" means registering the trio Item-Entity-Holder,
     * adjusting an existing entity, and syncing it to the clients.
     *
     * @param stackLocation The Item in the holder's inventory that is
     *                      producing an Item-Entity duality activation
     *
     * @param existing The entity that already exists and is alive in
     *                 the world right now, to be configured as an
     *                 Item-Entity duality.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSDualityActivationAction(@NotNull ItemStackLocation<? extends Entity> stackLocation, @NotNull ItemStack preparedItem, @NotNull Entity existing) {

        // The stack location, item, and entity are provided
        this.stackLocation = stackLocation;
        world = stackLocation.getHolder().level();
        holdPoint = ((ItemEntityDualityHolder) stackLocation.getHolder()).actuallysize$getHoldPoint(stackLocation);
        entityCounterpart = existing;
        itemCounterpart = preparedItem;
    }

    @Override
    public boolean isVerified() {

        // Need world
        if (world == null) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &c World Unverified");
            return false; }

        // Need Item, always
        if (stackLocation == null) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &c Stack Location Unverified");
            return false; }

        if (holdPoint == null) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &c No hold point was found for this ItemStack Location");
            return false; }

        // The entity must exist (it should already have been assigned in the constructor)
        if (entityCounterpart == null) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &c Entity Unverified");
            return false; }

        /*
         * Server-side requires these to be verified, but client-side will stop it during the ALLOW phase
         *
         * This is because in clientside, sometimes the packet for Activation arrives before the packet for
         * inventory update, and the item in this slot makes no sense yet but will in the future. Then, it
         * is an [Allowed] kind of definition for blocking it.
         */
        if (!world.isClientSide) {

            // Item must have an enclosed entity, naturally
            if (itemCounterpart == null) {
                /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &c Item Duality Unverified");
                return false; }

            // When binding a player to an item, it gets a pass whether it is enclosed in the item
            if (entityCounterpart instanceof Player) {

                // But any non-player MUST be registered within the item.
                if (ASIPickupSystemManager.getInscribedPlayer(itemCounterpart) == null) {
                    /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &c No inscribed player :(");
                    return false; }

            } else {

                // But any non-player MUST be registered within the item.
                if (!((ItemDualityCounterpart) (Object) itemCounterpart).actuallysize$hasEnclosedEntity(world)) {
                    /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &c No enclosing entity :(");
                    return false; }
            }
        }

        /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &2 Verification Passed " + entityCounterpart.getScoreboardName());
        // Requirements met
        return true;
    }

    @Override
    public boolean isAllowed() {

        // Should have been confirmed during verification step but
        if (world == null) { return false; }
        if (stackLocation == null) { return false; }
        if (entityCounterpart == null) { return false; }
        boolean isClientSide = world.isClientSide;

        // Refresh the item counterpart, in case of delayed net packet
        if (isClientSide) { itemCounterpart = stackLocation.getItemStack(); }

        // Identify further
        ItemDualityCounterpart itemDuality = (ItemDualityCounterpart) (Object) itemCounterpart;
        if (itemDuality == null) { return false; }
        /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &r Allow-checking item &2 " + itemCounterpart.getDisplayName().getString() + " in " + stackLocation.getStatement());

        // Ignore the redundant operation of re-activating the same item
        if (itemDuality.actuallysize$isDualityActive()) {
            EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) entityCounterpart;
            ItemStackLocation<? extends Entity> dualityLocation = dualityEntity.actuallysize$getItemStackLocation();
            /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &e Is Active? " + stackLocation.getStatement() + " VS " + dualityLocation.getStatement());

            // Same exact location (holder & slot)?! Skip!
            if (stackLocation.equals(dualityLocation)) {
                /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &6 Identical duality at location");

                // In the clientside, a repeat here is a duplicate packet sent somewhere
                if (isClientSide) {
                    /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &6 Clientside duplicate packet invalidation");
                    stackLocation = null;
                }

                // Done
                return false;
            }
        }

        /*
         *  In the client-side, we verify that the item and entity makes sense. Somehow.
         */
        if (isClientSide) {
            boolean remoteOverride = false;

            /*
             * In the client-side, remote players do not have a cursor
             * slot. Then this makes for a special case when activating
             * a duality actually creates that item.
             */
            if (stackLocation.getHolder() instanceof net.minecraft.client.player.RemotePlayer) {
                if (stackLocation.getStatement().equals(ISPExplorerStatements.CURSOR)) {

                    // Yeah
                    remoteOverride = true;
                }
            }

            // If not overridden by the special cursor access in remote player, we check that the item exists
            if (!remoteOverride) {

                // If not a player, verify that the item here has this entity enclosed within itself
                if (entityCounterpart instanceof Player) {
                    if (!ASIPickupSystemManager.isPlayerInscribedInNBT((Player) entityCounterpart, itemCounterpart)) {
                        /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &c Mismatched/missing inscribed player");
                        return false; }

                } else {
                    if (!itemDuality.actuallysize$hasEnclosedEntity(world)) {
                        /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &c No enclosing entity :(");
                        return false; }

                    // The client must check that the entity is the correct type
                    if (!itemDuality.actuallysize$getEnclosedEntity(world).getType().equals(entityCounterpart.getType())) {
                        /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &c Different Entity Types");
                        return false; }
                }
            }

        // In the server side, run the event
        } else {

                // If the event is not cancelled, we are allowed!
                /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &r Running try activation event");
                ASIPSTryActivateItemEntityEvent event = new ASIPSTryActivateItemEntityEvent(stackLocation, entityCounterpart);
                if (MinecraftForge.EVENT_BUS.post(event)) {
                    /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &c Failed Try Activate Event");
                    return false; }
        }

        /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &7 Permission Passed. Client? &b " + isClientSide + " &r | " + entityCounterpart.getScoreboardName() + " in &3 " + stackLocation.getStatement() +  " " + ((ItemStack) (Object) itemDuality).getDisplayName().getString());
        return true;
    }

    @Override
    public void resolve() {
        /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &r Item-Entity Activation &b RESOLVING");

        // Full identify
        ItemDualityCounterpart itemDuality = (ItemDualityCounterpart) (Object) itemCounterpart;
        EntityDualityCounterpart entityDuality = (EntityDualityCounterpart) entityCounterpart;

        if (world == null) { return; }
        if (entityDuality == null) { return; }
        boolean isServer = !world.isClientSide;

        /*
         * In the client-side, remote players do not have a cursor
         * slot. Then this makes for a special case when activating
         * a duality actually creates that item.
         */
        if (!isServer) {
            if (stackLocation.getHolder() instanceof net.minecraft.client.player.RemotePlayer) {
                if (stackLocation.getStatement().equals(ISPExplorerStatements.CURSOR)) {

                    // That's funny haha, involving ourselves with the cursor slot of a remote player...
                    Player player = (Player) stackLocation.getHolder();
                    ItemStack drop = ASIPickupSystemManager.generateHeldItem(entityCounterpart);
                    player.inventoryMenu.setCarried(drop);
                    itemCounterpart = drop;
                    itemDuality = (ItemDualityCounterpart) (Object) itemCounterpart;
                }
            }
        }

        // This would have been checked in verify but okay
        if (holdPoint == null) { return; }
        if (itemDuality == null) { return; }
        if (stackLocation == null) { return; }
        if (entityCounterpart == null) { return; }
        ItemEntityDualityHolder dualityHolder = (ItemEntityDualityHolder) stackLocation.getHolder();

        /*
         * #1   Deactivate this entity if previously active, resetting it to inactive
         */
        if (isServer && itemDuality.actuallysize$isDualityActive()) {
            /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &e DEACTIVATING FIRST");

            /*
             * Is this Item-Entity duality already active? We should remove the old version first.
             *
             * Sometimes an item is exchanged directly between two slots that can proc active Item-Entity dualities,
             * which results in it being deactivated and immediately activated again due to being removed from one
             * slot and added to another.
             *
             * Or even worse, it is added before being deleted which crashes minecraft due to the same Entity class
             * spawning twice and being tracked by the client. Anyway, in short, it is volatile.
             */
            ItemEntityDualityHolder oldDualityHolder = itemDuality.actuallysize$getItemEntityHolder();
            ItemStackLocation<? extends Entity> oldDualityLocation = itemDuality.actuallysize$getItemStackLocation();

            // If there is indeed such a thing as the old duality holder, tell them to deactivate this entity
            if (oldDualityHolder != null && oldDualityLocation != null) {
                /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &e Deactivate Pass");

                /*
                 * Use the previous Item Stack Location to de-register this Item-Entity
                 * from that other holder / slot where it is active. Note this will loop
                 * itself infinitely when removing it from the same holder and slot lol.
                 */

                ASIPSDualityDeactivationAction removeOld = new ASIPSDualityDeactivationAction(itemDuality);
                removeOld.tryResolve();
                /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &e Deactivate Passed");
            }
        }

        /*
         * #2   Deactivate the previous Item-Entity in this slot of the holder
         * #3   Register this Item-Entity duality onto the holder
         */
        /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &r Registering onto holder");
        dualityHolder.actuallysize$setHeldItemEntityDuality(holdPoint, (EntityDualityCounterpart) entityCounterpart);

        /*
         * #4   Registering onto the entity counterpart
         */
        /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &r Registering onto entity counterpart");
        entityDuality.actuallysize$setItemEntityHolder(dualityHolder);
        entityDuality.actuallysize$setItemCounterpart((ItemStack) (Object) itemDuality);
        entityDuality.actuallysize$setItemStackLocation(stackLocation);
        entityDuality.actuallysize$setHoldPoint(holdPoint);

        /*
         * #5   Registering onto the item counterpart
         */
        /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &r Registering onto item counterpart");
        itemDuality.actuallysize$setItemEntityHolder(dualityHolder);
        itemDuality.actuallysize$setEntityCounterpart(entityCounterpart);
        itemDuality.actuallysize$setItemStackLocation(stackLocation);

        /*
         * #6   Run event
         */
        /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &r Entity activation event run");
        ASIPSActivateItemEntityEvent broadcast = new ASIPSActivateItemEntityEvent(stackLocation, entityCounterpart);
        MinecraftForge.EVENT_BUS.post(broadcast);

        // Done for clientside
        if (!isServer) { return; }

        /*
         * #7   Spawn the entity
         */
        if (entityCounterpart.isAddedToWorld()) {

            /*
             * #8   Sync it over the network
             */

            // Create packet to send over the network and send to those tracking this entity
            /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &r Activation packet send. ");
            ASINCItemEntityActivationPacket packet = new ASINCItemEntityActivationPacket(stackLocation, entityCounterpart);
            ASINetworkManager.broadcastEntityUpdate(entityCounterpart, packet);

        } else {

            //todo HELD-RIDE Maybe someday allow riding of held entities

            // Spawn entity
            /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &r Entity counterpart spawn. ");
            if (holdPoint != null) {
                holdPoint.positionHeldEntity(dualityHolder, entityDuality);
            } else {
                entityCounterpart.setPos(stackLocation.getHolder().position()); }
            entityCounterpart.revive();
            if (world.addFreshEntity(entityCounterpart)) {

                /*
                 * #8   Sync it over the network
                 */

                // Create packet to send over the network and send to those tracking this entity
                /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &r Activation packet send. ");
                ASINCItemEntityActivationPacket packet = new ASINCItemEntityActivationPacket(stackLocation, entityCounterpart);
                ASINetworkManager.broadcastEntityUpdate(entityCounterpart, packet);
            }
        }

        /*
         * #9 Register to Item-Entity duality kitty
         */
        ASIPickupSystemManager.registerActiveEntityCounterpart(entityCounterpart);
        /*HDA*/ActuallySizeInteractions.Log("ASI &6 HDA &r Item-Entity duality activation &a COMPLETED ");
    }
}
