package actually.portals.ActuallySize.pickup.item;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallyServerConfig;
import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.pickup.ASIPickupSystemManager;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityEscapeAction;
import actually.portals.ActuallySize.pickup.events.ASIPSFoodPropertiesEvent;
import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoints;
import actually.portals.ActuallySize.pickup.mixininterfaces.*;
import gunging.ootilities.GungingOotilitiesMod.instants.GOOMPlayerMomentumSync;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import gunging.ootilities.GungingOotilitiesMod.scheduling.SchedulingManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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
        if (useContext.getLevel().isClientSide) { return InteractionResult.PASS; }

        // Must be out of grab cooldown
        ItemStack itemCounterpart = useContext.getItemInHand();
        if (itemCounterpart.getPopTime() > 0) { return InteractionResult.PASS; }

        // Only makes sense when used by a player
        Player holderPlayer = useContext.getPlayer();
        if (holderPlayer == null) { return InteractionResult.PASS; }

        // Needs to be crouching to place, otherwise eaten as foodie
        if (!holderPlayer.isCrouching()) { return InteractionResult.PASS; }

        // Fetch the entity to be placed down
        ItemDualityCounterpart itemDuality = (ItemDualityCounterpart) (Object) itemCounterpart;
        EntityDualityCounterpart entityDuality = (EntityDualityCounterpart) itemDuality.actuallysize$getEntityCounterpart();
        if (entityDuality != null) {

            // Force new if creative (unless players, players cannot be duped)
            if (holderPlayer.getAbilities().instabuild && !isPlayer()) {
                Level level = holderPlayer.level();
                Entity rebuilt = counterpartOrRebuild(level, itemCounterpart, true, true);
                if (rebuilt != null) { entityDuality = (EntityDualityCounterpart) rebuilt; } }

            Entity entityCounterpart = (Entity) entityDuality;

            // Set position, and nullify momentum, and escape
            entityCounterpart.setDeltaMovement(Vec3.ZERO);
            entityCounterpart.setPos(entityPlaceOn(entityCounterpart, useContext.getClickedFace(), useContext.getClickLocation()));
            entityCounterpart.fallDistance = 0;
            entityDuality.actuallysize$escapeDuality();
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
                //ActuallySizeInteractions.Log("PS USE Rebuild Failure");
                return InteractionResult.PASS; }

            // Set position, and nullify momentum
            rebuilt.setDeltaMovement(Vec3.ZERO);
            rebuilt.setPos(entityPlaceOn(rebuilt, useContext.getClickedFace(), useContext.getClickLocation()));
            rebuilt.fallDistance = 0;

            // Deploy (added to world before slot adjusts position)
            if (!rebuilt.isAddedToWorld()) { level.addFreshEntity(rebuilt); }
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
     * Attempts to throw this held entity forward
     *
     * @param itemCounterpart The actual existing ItemStack
     * @param holderEntity The entity swinging the item.
     *
     * @return If this entity was thrown forward (and thus swinging is not an attack)
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public boolean onEntitySwing(ItemStack itemCounterpart, LivingEntity holderEntity) {

        // Need to have a count
        if (itemCounterpart.getCount() < 1) {return false;}

        // Only runs on server
        if (holderEntity.level().isClientSide) {return false;}

        // Pop-time Cooldown
        if (itemCounterpart.getPopTime() > 0) { return false; }

        // Only makes sense when used by a player
        if (!(holderEntity instanceof Player)) { return false; }
        Player holderPlayer = (Player) holderEntity;
        if (holderPlayer.isCrouching()) { return false; }

        // Throw active entity duality
        ItemDualityCounterpart itemDuality = (ItemDualityCounterpart) (Object) itemCounterpart;
        EntityDualityCounterpart entityDuality = (EntityDualityCounterpart) itemDuality.actuallysize$getEntityCounterpart();
        if (entityDuality != null) {

            // Find its hold point
            ASIPSHoldPoint holdPoint = entityDuality.actuallysize$getHoldPoint();
            if (holdPoint == null) { holdPoint = ASIPSHoldPoints.MAINHAND; }

            // Force new if creative (unless players, players cannot be duped)
            if (holderPlayer.getAbilities().instabuild && !isPlayer()) {
                Level level = holderPlayer.level();
                Entity rebuilt = counterpartOrRebuild(level, itemCounterpart, true, true);
                if (rebuilt != null) { entityDuality = (EntityDualityCounterpart) rebuilt; } }

            // Throw from the hold point and escape
            holdPoint.throwHeldEntity((ItemEntityDualityHolder) holderEntity, entityDuality);
            entityDuality.actuallysize$escapeDuality();
            if (entityDuality instanceof Player) {

                // Add ticks of grace impulse
                GraceImpulsable imp = (GraceImpulsable) entityDuality;
                imp.actuallysize$addGraceImpulse(40);

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
                //ActuallySizeInteractions.Log("PS SWING Rebuild Failure");
                return false; }

            /*
             *  For whatever reason this item-entity is not active, even if
             *  thrown from the main hand. In that case, pretend this tiny
             *  was thrown from the main hand.
             */

            ASIPSHoldPoint simulation = ASIPSHoldPoints.MAINHAND;
            simulation.throwHeldEntity((ItemEntityDualityHolder) holderEntity, (EntityDualityCounterpart) rebuilt);

            // Deploy (added to world before slot adjusts position)
            if (!rebuilt.isAddedToWorld()) { level.addFreshEntity(rebuilt); }
        }

        // Item count decrease
        if (!holderPlayer.getAbilities().instabuild) { itemCounterpart.shrink(1); }

        // Complete processing
        return true;
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
}
