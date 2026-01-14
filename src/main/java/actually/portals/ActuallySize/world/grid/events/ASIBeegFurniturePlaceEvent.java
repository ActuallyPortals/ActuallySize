package actually.portals.ActuallySize.world.grid.events;

import actually.portals.ActuallySize.world.blocks.furniture.ASIBeegFurnishing;
import actually.portals.ActuallySize.world.grid.ASIBeegBlock;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackExplorer;
import gunging.ootilities.GungingOotilitiesMod.exploring.players.ISPExplorerStatements;
import gunging.ootilities.GungingOotilitiesMod.exploring.players.ISPPlayerElaborator;
import gunging.ootilities.GungingOotilitiesMod.exploring.players.ISPPlayerExplorer;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * An ASI-Specific Event fired when placing a Beeg Furniture
 *
 * @since 1.0.0
 * @author Actually Portals
 */
@Cancelable
public class ASIBeegFurniturePlaceEvent extends Event implements ASIBeegBlockEvent {

    /**
     * The furnishing being placed
     *
     * @since 1.0.0
     */
    @NotNull final ASIBeegFurnishing furnishing;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @NotNull ASIBeegFurnishing getFurnishing() { return furnishing; }

    /**
     * The structure being placed
     *
     * @since 1.0.0
     */
    @NotNull StructureTemplate structure;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @NotNull StructureTemplate getStructure() { return structure; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public void setStructure(@NotNull StructureTemplate structure) { this.structure = structure; }

    /**
     * The rotation of the structure
     *
     * @since 1.0.0
     */
    @NotNull Rotation structureRotation;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @NotNull Rotation getStructureRotation() { return structureRotation; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public void setStructureRotation(@NotNull Rotation structureRotation) { this.structureRotation = structureRotation; }

    /**
     * The offset of the structure
     *
     * @since 1.0.0
     */
    @NotNull Vec3i structureOffset;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @NotNull Vec3i getStructureOffset() { return structureOffset; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public void setStructureOffset(@NotNull Vec3i structureOffset) { this.structureOffset = structureOffset; }

    /**
     * How much more it costs to place this furniture
     * than the normal-sized version
     *
     * @since 1.0.0
     */
    int itemCostMultiplier;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public int getItemCostMultiplier() { return itemCostMultiplier; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public void setItemCostMultiplier(int itemCostMultiplier) { this.itemCostMultiplier = itemCostMultiplier; }

    /**
     * The beeg blocks that will be cleared in
     * order to place this structure
     *
     * @since 1.0.0
     */
    @NotNull final HashMap<ASIBeegBlock, ASIBeegBreakEvent> clearingQueue = new HashMap<>();

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @NotNull HashMap<ASIBeegBlock, ASIBeegBreakEvent> getClearingQueue() { return clearingQueue; }

    /**
     * The Beeg Grid block that this will be placed to
     *
     * @since 1.0.0
     */
    @NotNull final ASIBeegBlock beegBlock;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override public @NotNull ASIBeegBlock getBeegBlock() { return beegBlock; }

    /**
     * The player placing down this furniture
     *
     * @since 1.0.0
     */
    @NotNull final ServerPlayer player;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @NotNull ServerPlayer getPlayer() { return player; }

    /**
     * The world where this furniture is placed
     *
     * @since 1.0.0
     */
    @NotNull final ServerLevel world;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @NotNull ServerLevel getWorld() { return world; }

    /**
     * @param player        The beeg placing down this furniture
     * @param beegBlock     The beeg block where this furniture is being placed
     * @param furnishing    The furnishing being placed
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIBeegFurniturePlaceEvent(
            @NotNull ServerLevel world,
            @NotNull ServerPlayer player,
            @NotNull ASIBeegBlock beegBlock,
            @NotNull ASIBeegFurnishing furnishing,
            @NotNull StructureTemplate structure,
            @NotNull Rotation structureRotation,
            @NotNull Vec3i structureOffset) {
        this.world = world;
        this.player = player;
        this.beegBlock = beegBlock;
        this.furnishing = furnishing;
        this.structure = structure;
        this.structureRotation = structureRotation;
        this.structureOffset = structureOffset;
        itemCostMultiplier = beegBlock.getEffectiveScale() * 4;
    }

    /**
     * The log block that will be used, in place for {@link actually.portals.ActuallySize.world.ASIWorldSystemManager#BEEG_LOG_BLOCK}
     *
     * @since 1.0.0
     */
    @Nullable BlockState choiceLog = null;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @Nullable BlockState getChoiceLog() { return choiceLog; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public void setChoiceLog(@Nullable BlockState choiceLog) { this.choiceLog = choiceLog; }

    /**
     * The wood block that will be used, in place for {@link actually.portals.ActuallySize.world.ASIWorldSystemManager#BEEG_WOOD_BLOCK}
     *
     * @since 1.0.0
     */
    @Nullable BlockState choiceWood = null;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @Nullable BlockState getChoiceWood() { return choiceWood; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public void setChoiceWood(@Nullable BlockState choiceWood) { this.choiceWood = choiceWood; }

    /**
     * The wool block that will be used, in place for {@link actually.portals.ActuallySize.world.ASIWorldSystemManager#BEEG_WOOL_BLOCK}
     *
     * @since 1.0.0
     */
    @Nullable BlockState choiceWool = null;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @Nullable BlockState getChoiceWool() { return choiceWool; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public void setChoiceWool(@Nullable BlockState choiceWool) { this.choiceWool = choiceWool; }

    /**
     * The planks block that will be used, in place for {@link actually.portals.ActuallySize.world.ASIWorldSystemManager#BEEG_PLANKS_BLOCK}
     *
     * @since 1.0.0
     */
    @Nullable BlockState choicePlanks = null;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @Nullable BlockState getChoicePlanks() { return choicePlanks; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public void setChoicePlanks(@Nullable BlockState choicePlanks) { this.choicePlanks = choicePlanks; }

    /**
     * Identifies, using the player of this event, the choice of
     * materials that will be replaced into the structure
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void generateChoiceSettings() {

        /*
         * Create settings by looking into this players' inventory
         */
        for (ItemStackExplorer<ISPPlayerElaborator, Player> location : ((new ISPPlayerExplorer(ISPExplorerStatements.MAIN)).elaborate(new ISPPlayerElaborator(getPlayer())))) {

            // Identify the item
            ItemStack itemStack = location.getStatement().readItemStack(getPlayer());
            if (itemStack == null) { continue; }

            // Only block item supported
            Item item = itemStack.getItem();
            if (!(item instanceof BlockItem)) { continue; }

            Block block = ((BlockItem) item).getBlock();
            Holder.Reference<Block> reg = block.builtInRegistryHolder();
            if (choiceWool == null && reg.is(BlockTags.WOOL)) { choiceWool = block.defaultBlockState(); }
            else if (choiceLog == null && reg.is(BlockTags.LOGS)) { choiceLog = block.defaultBlockState(); }
            else if (choicePlanks == null && reg.is(BlockTags.PLANKS)) { choicePlanks = block.defaultBlockState(); }

            // There is no tag for wood
            else if (choiceWood == null) {

                // The best we have is checking the name lol
                String key = reg.key().location().getPath();
                if (key.contains("wood")) { choiceWood = block.defaultBlockState(); }
            }
        }
    }
}