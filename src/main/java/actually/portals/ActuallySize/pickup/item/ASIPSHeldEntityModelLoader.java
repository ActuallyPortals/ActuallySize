package actually.portals.ActuallySize.pickup.item;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

/**
 * This class will load the {@link  ASIPSHeldEntityModelBaker} model baker.
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
public class ASIPSHeldEntityModelLoader implements IGeometryLoader<ASIPSHeldEntityModelBaker> {

    /**
     * The Model Loaders are designed to be singletons
     *
     * @since 1.0.0
     */
    public static final ASIPSHeldEntityModelLoader HELD_ENTITY_LOADER = new ASIPSHeldEntityModelLoader();

    /**
     * The namespaced key to reference this loader via item JSON files:
     *
     * <pre>{@code
     * {
     *   "loader": "actuallysize:held_entity_loader",
     *   "parent": "minecraft:item/generated",
     *   "textures": {
     *     "layer0": "minecraft:item/diamond"
     *   }
     * }}</pre>
     *
     * @since 1.0.0
     */
    public static final ResourceLocation HELD_ENTITY_LOADER_ID = ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "held_entity_loader");

    /**
     * As per the guide in <a href="https://docs.neoforged.net/docs/1.20.4/resources/client/models/modelloaders/">the NeoForge docs</a>
     * the correct way to implement a Model Loader in minecraft 1.20.1 is the following.
     * <p></p>
     * Surely, I am only meaning to implement a custom renderer rather than an actual model baker,
     * so it feels like a huge overkill. It kinda is, but I am satisfied knowing I did it right.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    private ASIPSHeldEntityModelLoader() {}

    /**
     * @param jsonObject A json object that is ignored
     * @param context A context that is ignored
     * @return An empty model baker
     * @throws JsonParseException Never, since I read an empty model
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public ASIPSHeldEntityModelBaker read(JsonObject jsonObject, JsonDeserializationContext context) throws JsonParseException {
        return new ASIPSHeldEntityModelBaker();
    }
}

