package actually.portals.ActuallySize.pickup.item;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * This class will register the Model Loader when the correct event fires.
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
@Mod.EventBusSubscriber(modid = ActuallySizeInteractions.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ASIPSModelRegistryListener {

    /**
     * The Block Entity Renderers are designed to be singletons
     *
     * @since 1.0.0
     */
    public static ASIPSHeldEntityRenderer ASI_RENDERER;

    /**
     * @see ASIPSHeldEntityModelLoader
     *
     * @param event The event of model loaders being registered
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @SubscribeEvent
    public static void OnModelRegistry(final ModelEvent.RegisterGeometryLoaders event) {

        // Register the renderer the first time
        if (ASI_RENDERER == null) {
            ASI_RENDERER = new ASIPSHeldEntityRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        }

        // Register the Model Loaders
        event.register(ASIPSHeldEntityModelLoader.HELD_ENTITY_LOADER_ID.getPath(), ASIPSHeldEntityModelLoader.HELD_ENTITY_LOADER);
    }
}
