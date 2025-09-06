package actually.portals.ActuallySize.pickup.item;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.pickup.mixininterfaces.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

/**
 * A class that renders the entity encoded within a {@link ASIPSHeldEntityItem}
 * into the inventory slot, rather than a simple PNG image or something boring
 * like that.
 * <p>
 * Minecraft allows us the delicious capability of resource packs.
 * To accomplish this it needs a powerful (and convoluted) system to
 * load models and textures. I wished to display the entity you pick
 * up rather than a texture from a resourcepack, this means overriding
 * the rendering of the item. These are the steps I had to take:
 * <p>
 * (1) Create a Model Loader - to load model bakers into the engine {@link ASIPSHeldEntityModelLoader}
 * <p>
 * (2) Create a Model Baker - to bake the models we will use {@link ASIPSHeldEntityModelBaker}
 * <p>
 * (3) Create a Baked Model - to override the rendering of the model {@link ASIPSHeldEntityModelBaked}
 * <p>
 * (4) Extend {@link net.minecraft.world.item.Item} class - to manage your desired data {@link ASIPSHeldEntityItem}
 * <p>
 * (5) Create a Client Extension - to specify the renderer to use in this item {@link ASIPSHeldEntityExtension}
 * <p>
 * (6) Create a Renderer - to actually display the data {@link ASIPSHeldEntityRenderer}
 * <p>
 * [0] Register the Model Loader when {@link net.minecraftforge.client.event.ModelEvent} is fired.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
@OnlyIn(Dist.CLIENT)
public class ASIPSHeldEntityRenderer extends BlockEntityWithoutLevelRenderer {

    /**
     * @param pBlockEntityRenderDispatcher The renderer dispatcher
     * @param pEntityModelSet The model set
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSHeldEntityRenderer(@NotNull BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, @NotNull EntityModelSet pEntityModelSet) {
        super(pBlockEntityRenderDispatcher, pEntityModelSet);
    }

    /**
     * Actually draws the item in the inventory GUI, except I do not draw an item at all
     * and just redirect to drawing the entity using the models of the entity whatever.
     *
     * @param itemCounterpart The ItemStack being rendered
     * @param pDisplayContext The context by which this item is rendered
     * @param pPoseStack The pose stack to draw at
     * @param pBuffer A buffer I guess
     * @param pPackedLight The light level
     * @param pPackedOverlay Good question
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public void renderByItem(@NotNull ItemStack itemCounterpart, @NotNull ItemDisplayContext pDisplayContext, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        //RBI//ActuallySizeInteractions.Log("ASI PS HEI&b Renderer By Item");

        // Need to have a count
        if (itemCounterpart.getCount() < 1) {
            //RBI//ActuallySizeInteractions.Log("ASI PS HEI&c Uncounted");
            return; }

        // Interpret as duality
        ItemDualityCounterpart itemDuality = (ItemDualityCounterpart) (Object) itemCounterpart;
        Entity entityCounterpart = itemDuality.actuallysize$getEntityCounterpart();
        if (entityCounterpart == null) {

            if (!(itemCounterpart.getItem() instanceof ASIPSHeldEntityItem)) {
                //RBI//ActuallySizeInteractions.Log("ASI PS HEI&c Not held entity item");
                return;
            }

            if (((ASIPSHeldEntityItem) itemCounterpart.getItem()).isPlayer()) {
                //RBI//ActuallySizeInteractions.Log("ASI PS HEI&c PLAYER variant of held entity item");
                return;
            }

            // No duality? We must rebuild it. We will need a world reference
            Player player = Minecraft.getInstance().player;
            if (player == null) {
                //RBI//ActuallySizeInteractions.Log("ASI PS HEI&c No local");
                return; }

            // Rebuild entity
            entityCounterpart = itemDuality.actuallysize$getEnclosedEntity(player.level());
            if (entityCounterpart == null) {
                //RBI//ActuallySizeInteractions.Log("ASI PS HEI&c Non reproducible");
                return; }
        }

        /*
         * When this is being rendered in the world, we don't render held dualities.
         */
        switch (pDisplayContext) {
            case HEAD:
            case THIRD_PERSON_LEFT_HAND:
            case THIRD_PERSON_RIGHT_HAND:

                // We don't display item when the entity is held
                if (((EntityDualityCounterpart) entityCounterpart).actuallysize$isHeld()) {
                    return; }

                break;

            default: break; }

        // Render this entity
        //RBI//ActuallySizeInteractions.Log("ASI PS HEI&a Rendered");
        ItemEntityDualityHolder dualityHolder = itemDuality.actuallysize$getItemEntityHolder();
        if (dualityHolder == null) { dualityHolder = ((VASIPoseStack) pPoseStack).actuallysize$getPoseParent(); }
        Entity holder = dualityHolder instanceof Entity ? (Entity) dualityHolder : null;

        // Apparently holding yourself makes it recursive! LMAO
        if (holder != null) { if (entityCounterpart.getUUID().equals(holder.getUUID())) { return; } }

        double scale;
        switch (pDisplayContext) {
            case GUI:
            case FIXED:
                scale = getNormalizationScale(entityCounterpart);
                break;

            case GROUND:
                scale = getNormalizationScale(entityCounterpart) * 0.4D;
                break;

            case FIRST_PERSON_LEFT_HAND:
            case FIRST_PERSON_RIGHT_HAND:
            case HEAD:
                scale = Math.sqrt(getNormalizationScale(entityCounterpart));
                if (holder == null) {
                    scale *= 0.4D;
                } else {
                    scale *= 0.7D * ASIUtilities.beegBalanceEnhance(ASIUtilities.getRelativeScale(holder, false, entityCounterpart, false), 2, 0.25); }
                break;

            case THIRD_PERSON_LEFT_HAND:
            case THIRD_PERSON_RIGHT_HAND:

                //ActuallySizeInteractions.Log("ASI &3 RND 1 &r Rendering item [" + stack.getDisplayName().getString() + "], Built entity [" + rebuilt.getScoreboardName() + "]");
                //ActuallySizeInteractions.Log("ASI &3 RND 2 &r Is Duality Item Active?: " + dualityItem.actuallysize$isActive() + (dualityItem.actuallysize$getEntityCounterpart() != null ? ", " + dualityItem.actuallysize$getEntityCounterpart().getScoreboardName() + " @x" + ASIUtilities.getEntityScale(dualityItem.actuallysize$getEntityCounterpart()): ""));
                //ActuallySizeInteractions.Log("ASI &3 RND 3 &r Holder?: " + (dualityHolder != null ? dualityHolder.getClass() : "<null>") + (dualityHolder instanceof Entity ? ", " + ((Entity) dualityHolder).getScoreboardName() + " @x" + ASIUtilities.getEntityScale((Entity) dualityHolder) : ""));

                if (holder != null) {
                    //ASIUtilities.particles(holder.level(), holder.position());
                    scale = (float) getRestitutionScale(holder, entityCounterpart);
                } else {
                    scale = (float) Math.sqrt(getNormalizationScale(entityCounterpart));
                    scale *= 0.25F;
                }

                break;

            case NONE:
            default:
                scale = (float) Math.sqrt(getNormalizationScale(entityCounterpart));
                scale *= 0.25F;
                break;

        }

        Vec3 transform;
        switch (pDisplayContext) {
            case GUI:
            case FIXED:
                transform = new Vec3(0.5D, 0, 0);
                break;

            case GROUND:
            case FIRST_PERSON_LEFT_HAND:
            case FIRST_PERSON_RIGHT_HAND:
            case HEAD:
                transform = new Vec3(0.5D, 0.5D, 0.5D);
                break;

            case THIRD_PERSON_LEFT_HAND:
            case THIRD_PERSON_RIGHT_HAND:
                if (holder != null) {
                    transform = new Vec3(0.5D, 0.5D - getSinkingScalar(holder, entityCounterpart), 0.5D);
                } else {
                    transform = new Vec3(0.5D, 0.48D, 0.5D); }
                break;

            case NONE:
            default:
                transform = new Vec3(0, 0, 0);
        }

        double spinDegrees = entityCounterpart instanceof LivingEntity ? ((LivingEntity) entityCounterpart).yBodyRotO : 0;
        switch (pDisplayContext) {
            case GUI:
                spinDegrees += 45;
                break;

            case THIRD_PERSON_RIGHT_HAND:
            case FIRST_PERSON_RIGHT_HAND:
                spinDegrees -= 25;
                break;

            case THIRD_PERSON_LEFT_HAND:
            case FIRST_PERSON_LEFT_HAND:
                spinDegrees += 25;
                break;

            case HEAD:
            case GROUND:
            case FIXED:
            case NONE:
            default: break;
        }

        pPoseStack.pushPose();
        pPoseStack.translate(transform.x, transform.y, transform.z);
        pPoseStack.scale((float) scale, (float) scale, (float) scale);
        pPoseStack.mulPose(Axis.YP.rotationDegrees((float) spinDegrees));

        // Draw again to be sure
        EntityRenderDispatcher renderer = Minecraft.getInstance().getEntityRenderDispatcher();
        boolean shadows = ((GraceImpulsable) renderer).actuallysize$isInGraceImpulse();
        boolean hitbox = renderer.shouldRenderHitBoxes();
        renderer.setRenderShadow(false);
        renderer.setRenderHitBoxes(false);
        renderer.render(entityCounterpart, 0, 0, 0, 0, 1, pPoseStack, pBuffer, pPackedLight);
        renderer.setRenderShadow(shadows);
        renderer.setRenderHitBoxes(hitbox);

        // Escape
        pPoseStack.popPose();
    }

    /**
     * When holding an entity, the render scale "1" is your
     * scale. This means that if you are holding a player,
     * they will draw as your size when held in your hand.
     * <p>
     * Then, we must scale them down to their original size.
     * If you are 4x bigger, we must shrink them by 0.25x,
     * basically [Their size] / [Your size]
     *
     * @param beeg The entity holding the other entity
     * @param tiny The entity being held
     *
     * @return The scale to return the held entity back to their original size
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public static double getRestitutionScale(@NotNull Entity beeg, @NotNull Entity tiny) {
        //return ASIUtilities.getEntityScale(tiny) / ASIUtilities.getEntityScale(beeg);
        return 1D / ASIUtilities.getEntityScale(beeg, false);
    }

    /**
     * You could call this the "Standard" bounding box size
     * that would fit in an inventory slot, a little arbitrary
     * to what normalization scale looked good.
     *
     * @since 1.0.0
     */
    public static double RENDER_NORMALIZATION_SCALAR = 0.4;

    /**
     * Some entities are much bigger than others. When holding
     * an Ender Dragon vs a Silverfish, we want to scale them
     * differently so that they fit in the inventory slot.
     * <p>
     * This will return a big number to scale up tiny entities,
     * and a small number to scale down large entities. It might
     * not fit perfectly, but it is pretty straight-forward, it
     * uses their bounding box to normalize the size.
     *
     * @param tiny The entity being held
     *
     * @return A scale to normalize this entity's size
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public static double getNormalizationScale(@NotNull Entity tiny) {
        return RENDER_NORMALIZATION_SCALAR / ASIUtilities.getEffectiveSize(tiny);
    }

    /**
     * When tinies are very small, we have no choice but to hold them
     * with the tips of our fingers. However, when they are a little
     * bigger, it is better to hold them with the entire hand, then the
     * bigger the entity the more it sinks into the hand basically.
     *
     * @param tiny The entity being held
     *
     * @return A translation to sink this entity into the player hand
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public static double getSinkingScalar(@NotNull Entity beeg, @NotNull Entity tiny) {

        double ratio = ASIUtilities.getRelativeScale(beeg, false, tiny, true);
        if (ratio >= 1.00) { return 0.5; }
        if (ratio >= 0.75) { return 0.4; }
        if (ratio >= 0.50) { return 0.3; }
        if (ratio >= 0.25) { return 0.2; }
        if (ratio >= 0.15) { return 0.15; }
        if (ratio >= 0.05) { return 0.1; }
        return 0.05D;
    }
}
