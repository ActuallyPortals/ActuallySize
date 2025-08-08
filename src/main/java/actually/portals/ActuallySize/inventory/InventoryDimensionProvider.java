package actually.portals.ActuallySize.inventory;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Provides Data pack information to dimension generation
 *
 * @since 1.0.0
 */
public class InventoryDimensionProvider extends DatapackBuiltinEntriesProvider {

    /**
     * Data on dimension type and stem
     *
     * @since 1.0.0
     */
    public static final RegistrySetBuilder BUILDER =
            new RegistrySetBuilder()
                    .add(Registries.DIMENSION_TYPE, InventorySystemManager::boostrapType)
                    .add(Registries.LEVEL_STEM, InventorySystemManager::boostrapStem);


    public InventoryDimensionProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(ActuallySizeInteractions.MODID));
    }
}
