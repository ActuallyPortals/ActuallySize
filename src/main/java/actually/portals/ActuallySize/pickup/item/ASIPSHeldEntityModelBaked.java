package actually.portals.ActuallySize.pickup.item;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.textures.UnitTextureAtlasSprite;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * This class represents a model, with textures and vertices and all
 * that... except I am interested in none of it, it is empty. Then I only
 * override the {@link #isCustomRenderer()} to return <code>true</code>.
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
 *
 * @see net.minecraftforge.client.model.EmptyModel
 */
@OnlyIn(Dist.CLIENT)
public class ASIPSHeldEntityModelBaked extends SimpleBakedModel {

    /**
     * Borrowed (of course) from {@link net.minecraftforge.client.model.EmptyModel}
     *
     * @since 1.0.0
     */
    private static final Material MISSING_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation());

    /**
     * As per the guide in <a href="https://docs.neoforged.net/docs/1.20.4/resources/client/models/modelloaders/">the NeoForge docs</a>
     * the correct way to implement a Model Loader in minecraft 1.20.1 is the following.
     * <p>
     * Surely, I only mean to implement a custom renderer rather than an actual model baker,
     * so it feels like a huge overkill. It kinda is, but I am satisfied knowing I did it right.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSHeldEntityModelBaked() {
        super(List.of(), Map.of(), false, false, false, UnitTextureAtlasSprite.INSTANCE, ItemTransforms.NO_TRANSFORMS, ItemOverrides.EMPTY, RenderTypeGroup.EMPTY, RenderTypeGroup.EMPTY);
    }

    /**
     * @return The missing texture particle
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public @NotNull TextureAtlasSprite getParticleIcon() { return MISSING_TEXTURE.sprite(); }

    /**
     * With this,we can finally use our {@link ASIPSHeldEntityExtension} to load the
     * {@link ASIPSHeldEntityRenderer} into the system and display the entity encoded
     * in the held item rather than some other random resource pack sprite.
     *
     * @return true
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public boolean isCustomRenderer() { return true; }
}
