package actually.portals.ActuallySize.world.blocks.furniture;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * Called to register ASI Beeg Furniture
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIBeegFurnitureRegistryEvent extends Event {

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @NotNull HashMap<ResourceLocation, ASIBeegFurnishing> getFurnishingsRegistry() {
        return furnishingsRegistry;
    }

    /**
     * The Beeg Furnishings that will be registered
     *
     * @since 1.0.0
     */
    @NotNull final HashMap<ResourceLocation, ASIBeegFurnishing> furnishingsRegistry = new HashMap<>();

    /**
     * @param item Item to link this furnishing to
     * @param furnishing The furnishing
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void put(@NotNull Item item, @NotNull ASIBeegFurnishing furnishing) {
        ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(item);
        furnishingsRegistry.put(itemKey, furnishing);
    }

    /**
     * An event that fires upon startup to register Beeg Furniture
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIBeegFurnitureRegistryEvent() {}
}
