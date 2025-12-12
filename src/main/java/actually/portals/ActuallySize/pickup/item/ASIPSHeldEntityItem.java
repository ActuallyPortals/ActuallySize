package actually.portals.ActuallySize.pickup.item;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.controlling.execution.ASIEventExecutionListener;
import actually.portals.ActuallySize.pickup.ASIPickupSystemManager;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityEscapeAction;
import actually.portals.ActuallySize.pickup.events.ASIPSFoodPropertiesEvent;
import actually.portals.ActuallySize.pickup.mixininterfaces.*;
import actually.portals.ActuallySize.world.grid.construction.ASIGConstructor;
import actually.portals.ActuallySize.world.grid.construction.cube.ASIGCEvenShell;
import actually.portals.ActuallySize.world.grid.construction.cube.ASIGCLayered;
import actually.portals.ActuallySize.world.grid.construction.cube.ASIGCShelled;
import actually.portals.ActuallySize.world.grid.construction.rect.ASIGRectConstructor;
import gunging.ootilities.GungingOotilitiesMod.instants.GOOMPlayerMomentumSync;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import gunging.ootilities.GungingOotilitiesMod.scheduling.SchedulingManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Represents a NON-PLAYER entity item, one able to be saved and loaded without bothering too
 * much about them. Basically it is fine if you grab a cow and stash it in a chest forever, it
 * is just data of a computer. Players are treated much differently.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIPSHeldEntityItem extends Item {

    /**
     * The NBT Tag where the held entity is stored.
     *
     * @since 1.0.0
     */
    @NotNull public final static String TAG_ENTITY = "HeldEntity";

    /**
     * The NBT Tag where the held entity UUID is stored.
     *
     * @since 1.0.0
     */
    @NotNull public final static String TAG_ENTITY_UUID = "HeldEntityUUID";

    /**
     * The NBT Tag where the held entity display name is stored.
     *
     * @since 1.0.0
     */
    @NotNull public final static String TAG_ENTITY_NAME = "HeldEntityName";

    /**
     * If for some reason the held entity could not reproduce a compound
     * tag to be saved as, at least we save their ID to spawn a similar
     * one if it makes sense.
     *
     * @since 1.0.0
     */
    @NotNull public final static String TAG_ENTITY_ID = "HeldEntityID";

    /**
     * If this held entity item is the PLAYER variant.
     *
     * @since 1.0.0
     */
    boolean player;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public boolean isPlayer() { return player; }

    @Override
    public @NotNull SoundEvent getEatingSound() {
        return SoundEvents.PLAYER_HURT_DROWN;
    }

    /**
     * This will register the custom item renderer that renders
     * the enclosed entity instead of a model or so.
     *
     * @param consumer The client initialization consumer
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {

        /*
         * Normally, items are displayed as that simple 2D image made thick
         * in the players' arm. However, we don't want to display a static
         * image like this, we want to display the entity being held.
         *
         * Therefore, we must override Minecraft's rendering of this item in
         * inventory, so it does not bother drawing a texture onto the screen,
         * instead we must trick it into showing the entity encoded in the item.
         *
         * Almost as if the entity was alive and moving in the world, you know,
         * like usual, but currently it is in an inventory slot.
         */
        consumer.accept(new ASIPSHeldEntityExtension());
    }

    /**
     * @param pProperties The properties of this item
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIPSHeldEntityItem(@NotNull Properties pProperties) { this(pProperties, false); }

    /**
     * @param pProperties The properties of this item
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIPSHeldEntityItem(@NotNull Properties pProperties, boolean isPlayer) { super(pProperties); player = isPlayer; }

    /**
     * @param pStack The actual existing ItemStack instance
     * @return The name of the entity contained in this ItemStack
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {

        // If the duality is active, the name is obtained straight from the entity
        ItemDualityCounterpart itemDuality = (ItemDualityCounterpart) (Object) pStack;
        Entity entityCounterpart = itemDuality.actuallysize$getEntityCounterpart();
        if (entityCounterpart != null) { return entityCounterpart.getName(); }

        // Not sure when this would happen
        if (!pStack.hasTag()) { return Component.translatable("tooltip.actuallysizeinteractions.invalid"); }

        // Also a strange situation
        CompoundTag compoundTag = pStack.getTag();
        if (compoundTag == null) { return Component.translatable("tooltip.actuallysizeinteractions.invalid"); }

        // Decode tag
        String entityName = compoundTag.getString(TAG_ENTITY_NAME);
        return Component.literal(entityName);
    }

    @Nullable public Entity counterpartOrRebuild(@Nullable Level world, @Nullable ItemStack source, boolean forceNewEntity, boolean escapeIfSuccess) {
        if (source == null) { return null; }

        /*
         * When in creative mode, you want to force new entities because
         * you can spawn as many as you like. This will not do in survival.
         *
         * Anyway, when not forcing, if an entity exists in the world it
         * takes priority over generating one from the item.
         *
         * Being players forces to use the one in the world tho
         */
        if (!forceNewEntity || isPlayer()) {

            // Check for existing entity and accept it
            ItemDualityCounterpart itemDuality = (ItemDualityCounterpart) (Object) source;
            Entity entityCounterpart = itemDuality.actuallysize$getEntityCounterpart();
            if (entityCounterpart != null) {

                // Escaping the entity from duality-ness is important sometimes
                if (escapeIfSuccess) {

                    // Run a special non-consume escape action
                    ASIPSDualityEscapeAction action = new ASIPSDualityEscapeAction((EntityDualityCounterpart) entityCounterpart);
                    action.setAndRemoveItem(false);

                    // Only accept this entity if releasing worked
                    if (action.tryResolve()) { return entityCounterpart; }

                // No need to escape it? We are done
                } else { return entityCounterpart; }
            }
        }

        // Either we are forcing a new entity or rebuilding from tag
        if (isPlayer()) { return null; }
        if (world == null) { return null; }
        if (!source.hasTag()) { return null; }
        CompoundTag compoundTag = source.getTag();
        if (compoundTag == null) { return null; }
        //HDA//ActuallySizeInteractions.Log("ASI &6 REC &r Generating from ASIPSHeldEntityItem.counterpartOrRebuild(Level, ItemStack, boolean, boolean)");

        // Rebuild entity
        Entity rebuilt = ASIPickupSystemManager.loadEntityFromTag(compoundTag.getCompound(TAG_ENTITY), world);
        if (rebuilt == null) { return null; }

        // Generate new UUID if forcing a new
        if (forceNewEntity) { rebuilt.setUUID(UUID.randomUUID()); }

        // Done
        return rebuilt;
    }

    /**
     * Attempts to place down this mob at the location you are looking at
     *
     * @param useContext The context by which the item is being used
     * @return The result of this interaction
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext useContext) {

        // Need to have a count
        if (useContext.getItemInHand().getCount() < 1) { return InteractionResult.PASS; }

        // Only works server side
        if (useContext.getLevel().isClientSide) {

            /*
             * For the local player, the position of the entity gets set
             * in preparation for when it will be placed down in the server
             */
            ItemDualityCounterpart itemDuality = (ItemDualityCounterpart) (Object) useContext.getItemInHand();
            Entity entityCounterpart = itemDuality.actuallysize$getEntityCounterpart();
            if (entityCounterpart != null) {

                Vec3 sim = entityPlaceOn(entityCounterpart, useContext.getClickedFace(), useContext.getClickLocation());
                entityCounterpart.setPos(sim);
                entityCounterpart.setOldPosAndRot();
            }

            return InteractionResult.PASS; }

        // Must be out of grab cooldown
        ItemStack itemCounterpart = useContext.getItemInHand();
        if (itemCounterpart.getPopTime() > 0) { return InteractionResult.PASS; }

        // Only makes sense when used by a player
        Player holderPlayer = useContext.getPlayer();
        if (holderPlayer == null) { return InteractionResult.PASS; }

        // Needs to be crouching to place, otherwise eaten as foodie
        if (!holderPlayer.isShiftKeyDown()) { return InteractionResult.PASS; }
        //PUT//ActuallySizeInteractions.Log("HEI Placing down " + getName(itemCounterpart).getString());

        // Fetch the entity to be placed down
        ItemDualityCounterpart itemDuality = (ItemDualityCounterpart) (Object) itemCounterpart;
        EntityDualityCounterpart entityDuality = (EntityDualityCounterpart) itemDuality.actuallysize$getEntityCounterpart();
        Entity entityCounterpart;
        if (entityDuality != null) {
            //PUT//ActuallySizeInteractions.Log("HEI Found Active Duality " + ((Entity) entityDuality).getScoreboardName());

            // Force new if creative (unless players, players cannot be duped)
            if (holderPlayer.getAbilities().instabuild && !isPlayer()) {
                Level level = holderPlayer.level();
                Entity rebuilt = counterpartOrRebuild(level, itemCounterpart, true, true);
                if (rebuilt != null) {
                    //PUT//ActuallySizeInteractions.Log("HEI Forced rebuilding to " + rebuilt.getScoreboardName());
                    entityDuality = (EntityDualityCounterpart) rebuilt; }
            }

            entityCounterpart = (Entity) entityDuality;

            // Set position, and nullify momentum, and escape
            entityCounterpart.setDeltaMovement(Vec3.ZERO);
            entityCounterpart.setPos(entityPlaceOn(entityCounterpart, useContext.getClickedFace(), useContext.getClickLocation()));
            entityCounterpart.fallDistance = 0;
            entityDuality.actuallysize$escapeDuality();
            ((Entity) entityDuality).revive();
            if (!entityCounterpart.isAddedToWorld()) { holderPlayer.level().addFreshEntity(entityCounterpart); }

            if (entityDuality instanceof Player) {

                // If player, they must be momentum-notified
                GOOMPlayerMomentumSync sync = new GOOMPlayerMomentumSync((Player) entityDuality);
                sync.tryResolve();
            }

        // Rebuild entity and throw
        } else {

            // Rebuild entity
            Level level = holderPlayer.level();
            Entity rebuilt = counterpartOrRebuild(level, itemCounterpart, holderPlayer.getAbilities().instabuild, true);
            if (rebuilt == null) {
                //PUT//ActuallySizeInteractions.Log("HEI Rebuild Failure");
                return InteractionResult.PASS; }

            // Set position, and nullify momentum
            rebuilt.setDeltaMovement(Vec3.ZERO);
            rebuilt.setPos(entityPlaceOn(rebuilt, useContext.getClickedFace(), useContext.getClickLocation()));
            rebuilt.fallDistance = 0;

            // Deploy (added to world before slot adjusts position)
            if (!rebuilt.isAddedToWorld()) { level.addFreshEntity(rebuilt); }
            //PUT//ActuallySizeInteractions.Log("HEI Found enclosed " + rebuilt.getScoreboardName());
            entityCounterpart = rebuilt;
        }

        // Item count decrease
        if (!holderPlayer.getAbilities().instabuild || isPlayer()) { itemCounterpart.shrink(1); }

        // Consumed
        return InteractionResult.CONSUME;
    }

    /**
     * But we don't want it to suffocate, so place it a comfortable
     * distance away from the solid block so that they are good.
     *
     * @return The position to place this entity down
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public Vec3 entityPlaceOn(@NotNull Entity rebuilt, @NotNull Direction clickedFace, @NotNull Vec3 clickLocation) {

        // Must check the size of the entity as well as the face
        double margin = ASIUtilities.getEffectiveSize(rebuilt) * 0.2, mx = 0, my = 0, mz = 0, width = 0;
        switch (clickedFace) {

            case EAST:
            case WEST:
            case NORTH:
            case SOUTH:

                /*
                 * The origin in sideways directions is at the center, which
                 * means half of the width is to be used for in the margin
                 */
                width = (rebuilt.getBbWidth() * 0.5) + margin;
                mx = width * clickedFace.getNormal().getX();
                mz = width * clickedFace.getNormal().getZ();
                break;

            case DOWN:

                /*
                 * The origin when it comes to height is at the bottom, which
                 * means the full height needs to be used in the margin
                 */
                width = rebuilt.getBbHeight() + margin;
                my = -width;
                break;

            case UP:
            default:

                /*
                 * The origin is at the bottom, which means we need
                 * to account for no added component when translating the
                 * margin.
                 */
                my = margin;
                break;
        }

        // Done
        return clickLocation.add(mx, my, mz);
    }

    /**
     * @param pStack The actual ItemStack instance
     * @return The animation that is used when using
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return pStack.getItem().isEdible() ? UseAnim.DRINK : UseAnim.BLOCK;
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public @Nullable FoodProperties getFoodProperties(ItemStack itemCounterpart, @Nullable LivingEntity entity) {

        // The system where the size of the held entity is taken into account
        long serverTick = SchedulingManager.getServerTicks() + SchedulingManager.getClientTicks();
        if (serverTick > sizeFoodTick || sizeFoodContribution == null) {
            sizeFoodTick = serverTick + 200;
            sizeFoodNutrition = 1;

            // Start with the base properties
            FoodProperties base = new FoodProperties.Builder().alwaysEat().build();

            // Rebuild the entity in attempts to extract its size
            FoodProperties enclosedContribution = null;
            Entity rebuilt = counterpartOrRebuild(entity != null ? entity.level() : null, itemCounterpart, false, false);
            if (rebuilt instanceof LivingEntity) {
                ASIPSFoodPropertiesEvent food = new ASIPSFoodPropertiesEvent((LivingEntity) rebuilt);
                //FOO//ActuallySizeInteractions.Log("ASI &1 FOO &r Posted event. Clientside? &6 " + rebuilt.level().isClientSide);
                MinecraftForge.EVENT_BUS.post(food);
                food.getBuilder().nutrition(food.getNutrition());
                food.getBuilder().saturationMod(food.getSaturation());
                enclosedContribution = food.getBuilder().build();
                sizeFoodNutrition = enclosedContribution.getNutrition();
            }
            //FOO//ActuallySizeInteractions.Log("ASI &1 FOO &r Size requested &6 " + (enclosedContribution == null ? "null" : "N" + enclosedContribution.getNutrition() + " S" + enclosedContribution.getSaturationModifier()));

            // The system where edible drops of eaten tinies are consumed
            FoodProperties edaciousContribution = ((Edacious) (Object) itemCounterpart).actuallysize$getEdaciousProperties();
            //FOO//ActuallySizeInteractions.Log("ASI &1 FOO &r Edacious requested &6 " + (edaciousContribution == null ? "null" : "N" + edaciousContribution.getNutrition() + " S" + edaciousContribution.getSaturationModifier()));

            // Prepare and return
            sizeFoodContribution = ASIPickupSystemManager.prepareFoodProperties(base, edaciousContribution, enclosedContribution);
            return sizeFoodContribution;

        // Not time to regenerate it
        } else { return sizeFoodContribution; }
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override public @Nullable FoodProperties getFoodProperties() { return sizeFoodContribution; }

    /**
     * Food nutrition added depending on the enclosed entity
     *
     * @since 1.0.0
     */
    @Nullable FoodProperties sizeFoodContribution;

    /**
     * The amount of nutrition provided by the enclosed entity
     *
     * @since 1.0.0
     */
    int sizeFoodNutrition;

    /**
     * Server tick to avoid regenerating the food properties too fast
     *
     * @since 1.0.0
     */
    long sizeFoodTick;

    /**
     * Forces recalculation of Food Properties
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public void resetFoodTick() { sizeFoodTick = 0; }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        ItemDualityCounterpart itemDuality = (ItemDualityCounterpart) (Object) stack;
        Level world = entity.level();
        if (itemDuality == null) { return false; }

        Entity entityCounterpart = null;

        // Being dropped to the ground releases the entity enclosed and destroys the item
        Entity enclosed = itemDuality.actuallysize$getEnclosedEntity(world);
        if (enclosed != null) {

            /*
             * If it is added, then we'll pick it up in the next statement below,
             * and if it is not picked up it will generate one with a brand-new
             * UUID to be our entity counterpart.
             *
             * This statement will preferably create one with identical UUID to
             * the enclosed entity, but this will conflict if it is already in
             * the world.
             */
            if (!enclosed.isAddedToWorld()) { entityCounterpart = enclosed; }
        }

        // If it wasn't readied before, ready it now
        if (entityCounterpart == null) {
            entityCounterpart = itemDuality.actuallysize$readyEntityCounterpart(world);
        }

        // No entity counterpart? Destroy this invalid item
        if (entityCounterpart == null) {
            entity.getItem().setCount(0);
            entity.discard();
            return true;
        }

        // Release the entity, make sure they are added to the world
        if (entityCounterpart.isAddedToWorld()) {
            ((EntityDualityCounterpart) entityCounterpart).actuallysize$escapeDuality();
        } else {
            entityCounterpart.revive();
            world.addFreshEntity(entityCounterpart);
        }

        // Entity acquires position and velocity as if dropped
        entityCounterpart.setPos(entity.position());
        entityCounterpart.setDeltaMovement(entity.getDeltaMovement());
        if (entityCounterpart instanceof Player) {

            // Add ticks of grace impulse
            GraceImpulsable imp = (GraceImpulsable) entityCounterpart;
            imp.actuallysize$addGraceImpulse(40);

            // If player, they must be momentum-notified
            GOOMPlayerMomentumSync sync = new GOOMPlayerMomentumSync((Player) entityCounterpart);
            sync.tryResolve();
        }

        entity.getItem().setCount(0);
        entity.discard();
        return true;
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {

        /*
         * Allows tinies to be put in armor slots T_T
         */
        return true;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {

        /*
         * By the time the player tries to interact with this in any way,
         * the item should seriously have a valid enclosed entity.
         *
         * Only on SERVER SIDE
         */
        if (pStack.getPopTime() < 1 && !pLevel.isClientSide) {
            ItemDualityCounterpart itemDuality = (ItemDualityCounterpart) (Object) pStack;
            Boolean pop = itemDuality.actuallysize$isInvalidityPopped();

            // If it has not been popped yet, pop it
            if (pop == null) {
                itemDuality.actuallysize$setInvalidityPopped(true);
                pStack.setPopTime(5);

            // If it has completed its pop grace period
            } else if (pop) {
                itemDuality.actuallysize$setInvalidityPopped(false);

                // Players are treated differently
                if (((ASIPSHeldEntityItem)pStack.getItem()).isPlayer()) {
                    if (pStack.getCount() > 1) { pStack.setCount(1); }  // Silly count cap

                    // The entity counterpart MUST be active, or invalid
                    Entity player = itemDuality.actuallysize$getEntityCounterpart();
                    if (player == null) { pStack.setCount(0); }
                    return;
                }

                // If not active, then it only exists as an item in inventory.
                // If active, it HAS to be valid so no further processing is needed
                Entity active = itemDuality.actuallysize$getEntityCounterpart();
                if (active == null) {

                    // Enclosed is invalid, item is invalid
                    Entity enclosed = itemDuality.actuallysize$getEnclosedEntity(pLevel);
                    if (enclosed == null) { pStack.setCount(0); }
                }
            }
        }
    }
}
