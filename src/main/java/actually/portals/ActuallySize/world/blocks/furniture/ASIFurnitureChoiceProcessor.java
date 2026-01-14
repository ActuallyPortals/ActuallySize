package actually.portals.ActuallySize.world.blocks.furniture;

import actually.portals.ActuallySize.world.ASIWorldSystemManager;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A structure processor that can replace ASI Choice Blocks
 * for the actual block the player chooses to use.
 *
 * @author Actually Portals
 * @since 1.0.0
 */
public class ASIFurnitureChoiceProcessor extends StructureProcessor {

    /**
     * Singleton instance of this processor
     *
     * @since 1.0.0
     */
    static final ASIFurnitureChoiceProcessor INSTANCE = new ASIFurnitureChoiceProcessor();

    /**
     * Honestly this should not have been a singleton but that is
     * how minecraft is handling Structure Processors so here we go.
     * This clears the block choices to be the default values.
     *
     * @return The singleton of furniture choice
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public static ASIFurnitureChoiceProcessor getInstance() {
        return INSTANCE
                .withChoiceOfWool(Blocks.RED_WOOL.defaultBlockState())
                .withChoiceOfLog(Blocks.OAK_LOG.defaultBlockState())
                .withChoiceOfWood(Blocks.OAK_WOOD.defaultBlockState())
                .withChoiceOfPlanks(Blocks.OAK_PLANKS.defaultBlockState());
    }

    /**
     * The log block that will be used
     *
     * @since 1.0.0
     */
    @SuppressWarnings("DataFlowIssue")
    @NotNull BlockState choiceOfLog = null;

    /**
     * This method is cancelled when receiving
     * a null value, it only accepts not null
     *
     * @return This same ASI Furniture Choice Processor
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public ASIFurnitureChoiceProcessor withChoiceOfLog(@Nullable BlockState choice) { if (choice == null) { return this; } choiceOfLog = choice; return this; }

    /**
     * The log block that will be used
     *
     * @since 1.0.0
     */
    @SuppressWarnings("DataFlowIssue")
    @NotNull BlockState choiceOfWood = null;

    /**
     * This method is cancelled when receiving
     * a null value, it only accepts not null
     *
     * @return This same ASI Furniture Choice Processor
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public ASIFurnitureChoiceProcessor withChoiceOfWood(@Nullable BlockState choice) { if (choice == null) { return this; }  choiceOfWood = choice; return this; }

    /**
     * The log block that will be used
     *
     * @since 1.0.0
     */
    @SuppressWarnings("DataFlowIssue")
    @NotNull BlockState choiceOfPlanks = null;

    /**
     * This method is cancelled when receiving
     * a null value, it only accepts not null
     *
     * @return This same ASI Furniture Choice Processor
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public ASIFurnitureChoiceProcessor withChoiceOfPlanks(@Nullable BlockState choice) { if (choice == null) { return this; }  choiceOfPlanks = choice; return this; }

    /**
     * The log block that will be used
     *
     * @since 1.0.0
     */
    @SuppressWarnings("DataFlowIssue")
    @NotNull BlockState choiceOfWool = null;

    /**
     * This method is cancelled when receiving
     * a null value, it only accepts not null
     *
     * @return This same ASI Furniture Choice Processor
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public ASIFurnitureChoiceProcessor withChoiceOfWool(@Nullable BlockState choice) { if (choice == null) { return this; }  choiceOfWool = choice; return this; }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override protected @NotNull StructureProcessorType<?> getType() { return StructureProcessorType.BLACKSTONE_REPLACE; }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public @Nullable StructureTemplate.StructureBlockInfo process(
            @NotNull LevelReader world,
            @NotNull BlockPos offset,
            @NotNull BlockPos pos,
            @NotNull StructureTemplate.StructureBlockInfo original,
            @NotNull StructureTemplate.StructureBlockInfo result,
            @NotNull StructurePlaceSettings settings,
            @Nullable StructureTemplate structure) {

        // We only care for block choices
        Block block = result.state().getBlock();
        if (block == ASIWorldSystemManager.BEEG_LOG_BLOCK.getAsBlock().get()) {
            return new StructureTemplate.StructureBlockInfo(result.pos(), choiceOfLog, result.nbt()); }

        if (block == ASIWorldSystemManager.BEEG_WOOD_BLOCK.getAsBlock().get()) {
            return new StructureTemplate.StructureBlockInfo(result.pos(), choiceOfWood, result.nbt()); }

        if (block == ASIWorldSystemManager.BEEG_WOOL_BLOCK.getAsBlock().get()) {
            return new StructureTemplate.StructureBlockInfo(result.pos(), choiceOfWool, result.nbt()); }

        if (block == ASIWorldSystemManager.BEEG_PLANKS_BLOCK.getAsBlock().get()) {
            return new StructureTemplate.StructureBlockInfo(result.pos(), choiceOfPlanks, result.nbt()); }

        // No processing needed
        return super.process(world, offset, pos, original, result, settings, structure);
    }
}
