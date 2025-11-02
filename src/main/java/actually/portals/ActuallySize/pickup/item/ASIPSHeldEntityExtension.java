package actually.portals.ActuallySize.pickup.item;

import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import actually.portals.ActuallySize.pickup.holding.pose.ASIPSArmPoserHold;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemDualityCounterpart;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

/**
 * The class that draws the Entity saved in the NBT of an item, where the
 * item should be when held.
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
 * @author Actually Portals
 * @since 1.0.0
 */
@OnlyIn(Dist.CLIENT)
public class ASIPSHeldEntityExtension implements IClientItemExtensions {

    /**
     * @return The hand pose associated with the chosen hold point
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public @Nullable HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {

        // Assert non-null
        if (entityLiving == null) { return IClientItemExtensions.super.getArmPose(entityLiving, hand, itemStack); }
        if (hand == null) { return IClientItemExtensions.super.getArmPose(entityLiving, hand, itemStack); }
        if (itemStack == null) { return IClientItemExtensions.super.getArmPose(entityLiving, hand, itemStack); }

        // We must find the hold point
        ItemDualityCounterpart itemDuality = (ItemDualityCounterpart) (Object) itemStack;
        if (itemDuality == null) { return IClientItemExtensions.super.getArmPose(entityLiving, hand, itemStack); }

        EntityDualityCounterpart entityDuality = (EntityDualityCounterpart) itemDuality.actuallysize$getEntityCounterpart();
        if (entityDuality == null) { return IClientItemExtensions.super.getArmPose(entityLiving, hand, itemStack); }

        ASIPSHoldPoint holdPoint = entityDuality.actuallysize$getHoldPoint();
        if (holdPoint == null) { return IClientItemExtensions.super.getArmPose(entityLiving, hand, itemStack); }

        // Okay now, arm pose or nothing. If there is no pose, it returns empty
        if (holdPoint instanceof ASIPSArmPoserHold) { return (HumanoidModel.ArmPose) ((ASIPSArmPoserHold) holdPoint).getHoldPose(entityLiving, hand, itemStack); }
        return HumanoidModel.ArmPose.EMPTY;
    }

    /**
     * @return The ASI Held Item renderer!
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {

        /*
         * The custom renderer will draw the item differently than
         * would the standard item renderer. This allows me to
         * draw some pretty neat stuff in inventory slots.
         */
        return ASIPSModelRegistryListener.ASI_RENDERER; }
}
