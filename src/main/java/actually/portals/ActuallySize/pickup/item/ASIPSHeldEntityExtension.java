package actually.portals.ActuallySize.pickup.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

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
