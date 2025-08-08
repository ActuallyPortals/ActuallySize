package actually.portals.ActuallySize.pickup.item;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.pickup.ASIPickupSystemManager;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityEscapeAction;
import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoints;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
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

    /**
     * @param pProperties The properties of this item
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIPSHeldEntityItem(@NotNull Properties pProperties) { super(pProperties); }

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

        // Player items are invalid if they have no duality
        if (isPlayer()) { return Component.translatable("tooltip.actuallysizeinteractions.invalid"); }

        // Not sure when this would happen
        if (!pStack.hasTag()) { return Component.translatable("tooltip.actuallysizeinteractions.invalid"); }

        // Also a strange situation
        CompoundTag compoundTag = pStack.getTag();
        if (compoundTag == null) { return Component.translatable("tooltip.actuallysizeinteractions.invalid"); }

        // Decode tag
        String entityName = compoundTag.getString(TAG_ENTITY_NAME);
        return Component.literal(entityName);
    }

    @Nullable Entity counterpartOrRebuild(@NotNull Level world, @Nullable ItemStack source, boolean forceNewEntity, boolean escapeIfSuccess) {
        if (source == null) { return null; }

        /*
         * When in creative mode, you want to force new entities because
         * you can spawn as many as you like. This will not do in survival.
         *
         * Anyway, when not forcing, if an entity exists in the world it
         * takes priority over generating one from the item.
         */
        if (!forceNewEntity) {

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
        if (!source.hasTag()) { return null; }
        CompoundTag compoundTag = source.getTag();
        if (compoundTag == null) { return null; }
        /*HDA*/ActuallySizeInteractions.Log("ASI &6 REC &r Generating from ASIPSHeldEntityItem.counterpartOrRebuild(Level, ItemStack, boolean, boolean)");

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
     * @param pContext The context by which the item is being used
     * @return The result of this interaction
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext pContext) {

        // Need to have a count
        if (pContext.getItemInHand().getCount() < 1) { return InteractionResult.PASS; }

        // Only works server side
        if (pContext.getLevel().isClientSide) { return InteractionResult.PASS; }

        // No idea how this could be
        Player player = pContext.getPlayer();
        if (player == null) {
            //ActuallySizeInteractions.Log("PS USE-ON Null player");
            return InteractionResult.PASS; }

        // Also strange situation
        ItemStack item = pContext.getItemInHand();
        if (item.getPopTime() > 0) {
            //ActuallySizeInteractions.Log("PS USE-ON In cooldown");
            return InteractionResult.PASS; }

        // Obtain the entity associated with this entity
        Level level = pContext.getLevel();
        Entity rebuilt = counterpartOrRebuild(level, item, player.isCreative(), true);
        if (rebuilt == null) {
            //ActuallySizeInteractions.Log("PS USE-ON Rebuild Failure");
            return InteractionResult.PASS; }

        /*
         * Actually place down the entity yay
         *
         * But we don't want it to suffocate, so place it a comfortable
         * distance away from the solid block so that they are good.
         */
        double margin = ASIUtilities.getEffectiveSize(rebuilt) * 0.2, mx = 0, my = 0, mz = 0, width = 0;
        switch (pContext.getClickedFace()) {

            case EAST:
            case WEST:
            case NORTH:
            case SOUTH:

                /*
                 * The origin in sideways directions is at the center, which
                 * means half of the width is to be used for in the margin
                 */
                width = (rebuilt.getBbWidth() * 0.5) + margin;
                mx = width * pContext.getClickedFace().getNormal().getX();
                mz = width * pContext.getClickedFace().getNormal().getZ();
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
        Vec3 entitySpawnPosition = pContext.getClickLocation().add(mx, my, mz);
        rebuilt.setPos(entitySpawnPosition);
        rebuilt.fallDistance = 0;

        // Deploy
        if (!rebuilt.isAddedToWorld()) { level.addFreshEntity(rebuilt); }

        // Item count decrease
        if (!player.isCreative()) { item.shrink(1); }

        return InteractionResult.CONSUME;
    }

    /**
     * @param pStack The actual ItemStack instance
     * @return The animation that is used when using
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return pStack.getItem().isEdible() ? UseAnim.DRINK : UseAnim.BLOCK;
    }

    /**
     * Attempts to throw this held entity forward
     *
     * @param stack The actual existing ItemStack
     * @param entity The entity swinging the item.
     *
     * @return If this entity was thrown forward (and thus swinging is not an attack)
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {

        // Need to have a count
        if (stack.getCount() < 1) {return false;}

        // Only runs on server
        if (entity.level().isClientSide) {return false;}

        // Poptime Cooldown
        if (stack.getPopTime() > 0) {
            //ActuallySizeInteractions.Log("PS SWING In cooldown");
            return false;
        }

        if (!(entity instanceof Player)) {return false;}
        Player player = (Player) entity;

        // Obtain the entity associated with this entity
        Level level = player.level();
        Entity rebuilt = counterpartOrRebuild(level, stack, player.isCreative(), true);
        if (rebuilt == null) {
            //ActuallySizeInteractions.Log("PS SWING Rebuild Failure");
            return false;
        }

        // The entity will be thrown from its current hold point if it exits
        ItemDualityCounterpart itemDuality = (ItemDualityCounterpart) (Object) stack;
        EntityDualityCounterpart entityDuality = (EntityDualityCounterpart) itemDuality.actuallysize$getEntityCounterpart();
        if (entityDuality == null || !entityDuality.actuallysize$isHeld()) {

            /*
             *  For whatever reason this item-entity is not active, even if
             *  thrown from the main hand. In that case, pretend this tiny
             *  was thrown from the main hand.
             */

            ASIPSHoldPoint simulation = ASIPSHoldPoints.MAINHAND;
            simulation.throwHeldEntity((ItemEntityDualityHolder) entity, (EntityDualityCounterpart) rebuilt);

            // If the entity is currently held, use their hold point
        } else {

            // The hold point exists trust me
            entityDuality.actuallysize$getHoldPoint().throwHeldEntity((ItemEntityDualityHolder) entity, (EntityDualityCounterpart) rebuilt);
        }

        // Deploy (added to world before slot adjusts position)
        if (!rebuilt.isAddedToWorld()) { level.addFreshEntity(rebuilt); }

        // Item count decrease
        if (!player.isCreative()) { stack.shrink(1); }

        // Complete processing
        return true;
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
}
