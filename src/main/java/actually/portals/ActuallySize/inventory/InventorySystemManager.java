package actually.portals.ActuallySize.inventory;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.OptionalLong;

public class InventorySystemManager {
    /**
     * Registry of dimensions added by this mod
     *
     * @since 1.0.0
     */
    public static final ResourceKey<LevelStem> INVENTORY_DIMENSION_KEY = ResourceKey.create(Registries.LEVEL_STEM, ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "inventorydim"));

    /**
     * Registry of dimensions added by this mod
     *
     * @since 1.0.0
     */
    public static final ResourceKey<DimensionType> INVENTORY_DIMENSION_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "inventorydim_type"));

    public static void boostrapType(BootstapContext<DimensionType> context) {
        context.register(INVENTORY_DIMENSION_TYPE, new DimensionType(
                OptionalLong.of(12000),
                false,
                false,
                false,
                false,
                1,
                true,
                false,
                0,
                256,
                256,
                BlockTags.INFINIBURN_OVERWORLD,
                BuiltinDimensionTypes.OVERWORLD_EFFECTS,
                1,
                new DimensionType.MonsterSettings(false, false, ConstantInt.of(0), 0)
        ));
    }

    public static void boostrapStem(BootstapContext<LevelStem> context) {


        HolderGetter<Biome> biomeReg = context.lookup(Registries.BIOME);
        HolderGetter<DimensionType> dimensionReg = context.lookup(Registries.DIMENSION_TYPE);

        LevelStem stem = new LevelStem(dimensionReg.getOrThrow(INVENTORY_DIMENSION_TYPE), new InventoryDimensionGen(new FixedBiomeSource(biomeReg.getOrThrow(Biomes.THE_VOID))));

        context.register(INVENTORY_DIMENSION_KEY, stem);
    }

    /**
     * Registry of dimensions added by this mod
     *
     * @since 1.0.0
     */
    public static final ResourceKey<Level> INVENTORY_DIMENSION = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "inventorydim"));
}
