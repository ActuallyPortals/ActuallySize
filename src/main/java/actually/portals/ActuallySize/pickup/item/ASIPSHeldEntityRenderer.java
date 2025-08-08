package actually.portals.ActuallySize.pickup.item;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemEntityDualityHolder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

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
     * @param stack The ItemStack being rendered
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
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext pDisplayContext, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        //RBI//ActuallySizeInteractions.Log("ASI PS HEI&b Renderer By Item");

        // Need to have a count
        if (stack.getCount() < 1) {
            //RBI//ActuallySizeInteractions.Log("ASI PS HEI&c Uncounted");
            return; }

        // Interpret as duality
        ItemDualityCounterpart dualityItem = (ItemDualityCounterpart) (Object) stack;
        Entity entityCounterpart = dualityItem.actuallysize$getEntityCounterpart();
        if (entityCounterpart == null) {

            if (!(stack.getItem() instanceof ASIPSHeldEntityItem)) {
                //RBI//ActuallySizeInteractions.Log("ASI PS HEI&c Not held entity item");
                return;
            }

            if (((ASIPSHeldEntityItem) stack.getItem()).isPlayer()) {
                //RBI//ActuallySizeInteractions.Log("ASI PS HEI&c PLAYER variant of held entity item");
                return;
            }

            // No duality? We must rebuild it. We will need a world reference
            Player player = Minecraft.getInstance().player;
            if (player == null) {
                //RBI//ActuallySizeInteractions.Log("ASI PS HEI&c No local");
                return; }

            // Rebuild entity
            entityCounterpart = dualityItem.actuallysize$getEnclosedEntity(player.level());
            if (entityCounterpart == null) {
                //RBI//ActuallySizeInteractions.Log("ASI PS HEI&c Non reproducible");
                return; }
        }

        // Render this entity
        //RBI//ActuallySizeInteractions.Log("ASI PS HEI&a Rendered");
        ItemEntityDualityHolder dualityHolder = dualityItem.actuallysize$getItemEntityHolder();
        Entity holder = dualityHolder instanceof Entity ? (Entity) dualityHolder : null;
        pPoseStack.pushPose();

        // Apparently holding yourself makes it recursive! LMAO
        if (holder != null) { if (entityCounterpart.getUUID().equals(holder.getUUID())) { return; } }

        float scale;
        switch (pDisplayContext) {
            case GUI:
            case FIXED:
                scale = (float) getNormalizationScale(entityCounterpart);
                break;

            case GROUND:
                scale = ((float) getNormalizationScale(entityCounterpart)) * 0.4F;
                break;

            case FIRST_PERSON_LEFT_HAND:
            case FIRST_PERSON_RIGHT_HAND:
            case HEAD:
                scale = (float) Math.sqrt(getNormalizationScale(entityCounterpart));
                scale *= 0.4F;
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

                /*/ O I I A I O I I I A I
                if (!dualityItem.actuallysize$isDualityActive()) {
                    pPoseStack.rotateAround(new Quaternionf(0, 1, 0, 1), (float) Math.cos(System.currentTimeMillis() * 0.01D), (float) Math.sin(System.currentTimeMillis() * 0.01D), 0);
                }
                //*/
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
                    transform = new Vec3(0.5D, 0.48D, 0.5D);
                }
                break;


            case NONE:
            default:
                transform = new Vec3(0, 0, 0);
        }

        pPoseStack.translate(transform.x, transform.y, transform.z);
        pPoseStack.scale(scale, scale, scale);

        /*

        // Reference latest
        int rev = 3;
        ArrayList<PoseStack.Pose> latest = new ArrayList<>();
        for (int i = 0; i < rev; i++) {

            // Remember
            latest.add(pPoseStack.last());

            // Pop
            pPoseStack.popPose();
        }

        Minecraft.getInstance().getEntityRenderDispatcher().render(rebuilt, 0, 0, 0, 0, 1, pPoseStack, pBuffer, pPackedLight);


        for (int iii = 0; iii < rev ; iii++) {

            // Push
            pPoseStack.pushPose();

            // Find
            PoseStack.Pose ii = latest.get(rev - iii - 1);

            // Set
            pPoseStack.last().pose().set(ii.pose());
            pPoseStack.last().normal().set(ii.normal());
        }

        Minecraft.getInstance().getEntityRenderDispatcher().render(rebuilt, 0, 0, 0, 0, 1, pPoseStack, pBuffer, pPackedLight);

        // */

        // Draw again to be sure
        Minecraft.getInstance().getEntityRenderDispatcher().render(entityCounterpart, 0, 0, 0, 0, 1, pPoseStack, pBuffer, pPackedLight);

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
