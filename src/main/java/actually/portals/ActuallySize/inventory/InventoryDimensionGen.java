package actually.portals.ActuallySize.inventory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Emptiness, no chunk generation, emptiness for real
 *
 * @since 1.0.0
 */
public class InventoryDimensionGen extends ChunkGenerator {

    /**
     * CODEC
     *
     * @since 1.0.0
     */
    public static final Codec<InventoryDimensionGen> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter((generator) -> generator.biomeSource))
                    .apply(instance, instance.stable(InventoryDimensionGen::new)));

    public InventoryDimensionGen(BiomeSource pBiomeSource) { super(pBiomeSource); }

    @Override
    protected @NotNull Codec<? extends ChunkGenerator> codec() { return CODEC; }

    @Override
    public void applyCarvers(@NotNull WorldGenRegion pLevel, long pSeed, @NotNull RandomState pRandom, @NotNull BiomeManager pBiomeManager, @NotNull StructureManager pStructureManager, @NotNull ChunkAccess pChunk, GenerationStep.@NotNull Carving pStep) { }

    @Override
    public void buildSurface(@NotNull WorldGenRegion pLevel, @NotNull StructureManager pStructureManager, @NotNull RandomState pRandom, @NotNull ChunkAccess pChunk) { }

    @Override
    public void spawnOriginalMobs(@NotNull WorldGenRegion pLevel) { }

    @Override
    public int getGenDepth() { return 0; }

    @Override
    public @NotNull CompletableFuture<ChunkAccess> fillFromNoise(@NotNull Executor pExecutor, @NotNull Blender pBlender, @NotNull RandomState pRandom, @NotNull StructureManager pStructureManager, @NotNull ChunkAccess pChunk) { return CompletableFuture.completedFuture(pChunk); }

    @Override
    public int getSeaLevel() { return 0; }

    @Override
    public int getMinY() { return 0; }

    @Override
    public int getBaseHeight(int pX, int pZ, Heightmap.@NotNull Types pType, @NotNull LevelHeightAccessor pLevel, @NotNull RandomState pRandom) { return 0; }

    @Override
    public @NotNull NoiseColumn getBaseColumn(int pX, int pZ, @NotNull LevelHeightAccessor pHeight, @NotNull RandomState pRandom) { return new NoiseColumn(0, new BlockState[0]); }

    @Override
    public void addDebugScreenInfo(@NotNull List<String> pInfo, @NotNull RandomState pRandom, @NotNull BlockPos pPos) { }
}
