package actually.portals.ActuallySize.world.blocks.furniture;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.world.grid.ASIBeegBlock;
import actually.portals.ActuallySize.world.grid.ASIWorldBlock;
import actually.portals.ActuallySize.world.grid.events.ASIBeegBreakEvent;
import actually.portals.ActuallySize.world.grid.events.ASIBeegFurniturePlaceEvent;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackExplorer;
import gunging.ootilities.GungingOotilitiesMod.exploring.players.*;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a structure with the capacity to scale.
 * In other words, it is a series of structures, and
 * you get the appropriate one based on your scale
 *
 * @author Actually Portals
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class ASIBeegFurnishing {

    /**
     * The key by which Structures must begin to be
     * considered in the ASI Beeg Furniture system
     *
     * @since 1.0.0
     */
    @NotNull public static final String BEEG_FURNISHING_SIGIL = "asi_";

    /**
     * The block being replaced by this structure
     *
     * @since 1.0.0
     */
    @NotNull final Block base;

    /**
     * @param base The block being replaced by this structure
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIBeegFurnishing(@NotNull Block base) {
        this.base = base;
    }

    /**
     * The various structures that this beeg furnishing
     * supports and at which scales they come into play
     *
     * @since 1.0.0
     */
    @NotNull final HashMap<Integer, StructureTemplate> scaledLibrary = new HashMap<>();

    /**
     * @param scale The scale of this structure
     * @param structure The structure to include
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public void addStructure(int scale, @NotNull StructureTemplate structure) { scaledLibrary.put(scale, structure); }

    /**
     * Clears the structures of this BEEG FURNITURE
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public void clearStructures() { scaledLibrary.clear(); }

    /**
     * The minimum scale you must be to trigger this beeg furniture, defaults to 2
     *
     * @since 1.0.0
     */
    int minimumScale = 2;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public int getMinimumScale() { return minimumScale; }

    /**
     * @return This same ASI Beeg Furnishing object
     *
     * @param minimumScale The minimum scale you must be to trigger this beeg furniture
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public ASIBeegFurnishing withMinimumScale(int minimumScale) { this.minimumScale = minimumScale; return this; }

    /**
     * If crouching allows to place a scaled-down
     * version of this, which is the default
     * behaviour of Beeg Building system
     *
     * @since 1.0.0
     */
    boolean allowHalving = false;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public boolean isAllowHalving() { return allowHalving; }

    /**
     * @return This same ASI Beeg Furnishing object
     *
     * @param halving If crouching allows to place a scaled-down version of this
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public ASIBeegFurnishing withAllowHalving(boolean halving) { this.allowHalving = halving; return this; }

    /**
     * Will place the biggest furniture
     * that has a scale equal or smaller
     * than the placing scale.
     * <br><br>
     * The alternative is to put the smallest
     * furniture that has a scale equal or
     * larger than the placing scale
     *
     * @since 1.0.0
     */
    boolean biggestThatFits = true;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public boolean isBiggestThatFits() { return biggestThatFits; }

    /**
     * @return This same ASI Beeg Furnishing object
     *
     * @param fit Will place the biggest furniture that
     *            has a scale equal or smaller than the placing scale.
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public ASIBeegFurnishing withBiggestThatFits(boolean fit) { this.biggestThatFits = fit; return this; }

    /**
     * If the structure rotates according to how the player
     * is facing. Alternatively, it rotates according to
     * the clicked face.
     *
     * @since 1.0.0
     */
    boolean usesPlayerFacing = true;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public boolean isUsesPlayerFacing() { return usesPlayerFacing; }

    /**
     * @return This same ASI Beeg Furnishing object
     *
     * @param playerFacing If the structure rotates according to how the player
     *                     is facing. Alternatively, it rotates according to
     *                     the clicked face.
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public ASIBeegFurnishing withUsesPlayerFacing(boolean playerFacing) { this.usesPlayerFacing = playerFacing; return this; }

    /**
     * Flips the rotation of the structure in respect to facing direction
     *
     * @since 1.0.0
     */
    boolean flipFacing = false;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public boolean isFlipFacing() { return flipFacing; }

    /**
     * @return This same ASI Beeg Furnishing object
     *
     * @param flip Flips the rotation of the structure in respect to facing direction
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public ASIBeegFurnishing withFlipFacing(boolean flip) { this.flipFacing = flip; return this; }

    /**
     * If the structure requires the area to be cleared out.
     * Alternatively, it will simply be placed in here and
     * destroy whatever already was here.
     *
     * @since 1.0.0
     */
    boolean requiresEmptyArea = true;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public boolean isRequiresEmptyArea() { return requiresEmptyArea; }

    /**
     * @return This same ASI Beeg Furnishing object
     *
     * @param empty If the structure requires the area to be cleared out.
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public ASIBeegFurnishing withRequiresEmptyArea(boolean empty) { this.requiresEmptyArea = empty; return this; }

    /**
     * If placing the furniture will forcibly clear
     * the entire Beeg Block it got placed into
     *
     * @since 1.0.0
     */
    boolean clearsArea = false;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public boolean isClearsArea() { return clearsArea; }

    /**
     * @return This same ASI Beeg Furnishing object
     *
     * @param clear If placing the furniture will forcibly clear
     *              the entire Beeg Block it got placed into
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public ASIBeegFurnishing withClearsArea(boolean clear) { this.clearsArea = clear; return this; }

    /**
     * Fills the structures array from the loaded structures in this manager.
     *
     * @param loadedStructures The structures available to be used
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public abstract void gatherFurnishingStructures(@NotNull StructureTemplateManager loadedStructures);

    /**
     * Will find the biggest structure that fits your scale.
     * Essentially, the last structure which scale is lower
     * than you, or equal if it exists.
     * <br><br>
     * "You need to be this big to place down beeg furniture"
     *
     * @param scale The scale that you are at
     *
     * @param rev Instead, find the smallest structure that fits you.
     *            It will return the first structure which scale is
     *            bigger than you, or equal if it exists.
     *            <br><br>
     *            "A size larger still fits those smaller"
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @Nullable public StructureTemplate getForScale(double scale, boolean rev) {
        if (scale < minimumScale) { return null; }

        // Prefer exact match no matter what
        StructureTemplate exact = scaledLibrary.get(OotilityNumbers.round(scale));
        if (exact != null) { return exact; }

        // Optimized structural search
        int res = rev ? Integer.MAX_VALUE : -1;
        for (Integer i : scaledLibrary.keySet()) {
            if (rev) {
                if (i < scale) { continue; }
                if (i < res) { res = i; }
            } else {
                if (i > scale) { continue; }
                if (i > res) { res = i; }
            }
        }

        // That's the one we are looking for
        return scaledLibrary.get(res);
    }

    /**
     * @param pitch The pitch of the player when placing down this furniture, in DEGREES
     * @param yaw The yaw of the player when placing down this furniture, in DEGREES
     *
     * @return The facing direction of this player as far as this
     *         furniture is concerned, considering that some
     *         furnishings disregard pítch while it may be relevant
     *         for others.
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull Direction getFacingDirection(double pitch, double yaw) {

        // Constrain to 0° through 359°
        double simYaw = yaw;
        while (simYaw < 0) { simYaw += 360; }
        while (simYaw >= 360) { simYaw -= 360; }

        // Center about yaw = 0
        if (simYaw > 180) { simYaw -= 360; }

        // Divide in quadrants and round
        int quadYaw = OotilityNumbers.round(simYaw / 90D);

        // Check these coordinate quadrants
        if (quadYaw == -1) { return Direction.EAST; }
        if (quadYaw == 0) { return Direction.SOUTH; }
        if (quadYaw == 1) { return Direction.WEST; }
        return Direction.NORTH;
    }

    /**
     * @return The rotation, assuming {@link Direction#SOUTH} is 0°
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public Rotation fromDirectionToRotation(@NotNull Direction dir) {

        // Just rotate assuming SOUTH is no rotation
        if (flipFacing) {

            return switch (dir) {
                case WEST -> Rotation.COUNTERCLOCKWISE_90;
                case EAST -> Rotation.CLOCKWISE_90;
                case NORTH -> Rotation.NONE;
                default -> Rotation.CLOCKWISE_180;
            };

        } else {
            return switch (dir) {
                case WEST -> Rotation.CLOCKWISE_90;
                case EAST -> Rotation.COUNTERCLOCKWISE_90;
                case NORTH -> Rotation.CLOCKWISE_180;
                default -> Rotation.NONE;
            };
        }
    }

    /**
     * @param player The player doing the placing down
     * @param at The world block the player indicated
     * @param beegBlock The beeg block that is determined to be used
     * @param faceClicked The face of the block that was clicked
     *
     * @return The structure that will be placed
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @Nullable public StructureTemplate getStructure(@NotNull ServerPlayer player, @NotNull ASIBeegBlock beegBlock, @NotNull ASIWorldBlock at, @NotNull Direction faceClicked) {
        return getForScale(beegBlock.getEffectiveScale(), !isBiggestThatFits());
    }

    /**
     * @param player The player doing the placing down
     * @param at The world block the player indicated
     * @param beegBlock The beeg block that is determined to be used
     * @param faceClicked The face of the block that was clicked
     * @param structure The structure that will be placed
     * @param structureRotation The rotation of the Structure Template
     *
     * @return The world-coords offset of this structure in the Beeg Block
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public Vec3i getStructureOffset(@NotNull ServerPlayer player, @NotNull ASIBeegBlock beegBlock, @NotNull ASIWorldBlock at, @NotNull Direction faceClicked, @NotNull StructureTemplate structure, @NotNull Rotation structureRotation) {
        return Vec3i.ZERO;
    }

    /**
     * @param player The player doing the placing
     * @param at Where this is being placed
     * @param faceClicked The face that was clicked
     *
     * @return An event for the placing of this furniture,
     *         ran through the Forge Mod Bus already.
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @Nullable public ASIBeegFurniturePlaceEvent preparePlace(@NotNull ServerPlayer player, @NotNull ASIWorldBlock at, @NotNull Direction faceClicked) {
        if (!(at.getWorld() instanceof ServerLevel)) { return null; }
        ServerLevel world = (ServerLevel) at.getWorld();

        // Identify the beeg block
        ASIBeegBlock beegBlock = ASIBeegBlock.containing(ASIUtilities.getEntityScale(player), at.getPos().getCenter()).withHalved(isAllowHalving() && player.isShiftKeyDown());
        if (beegBlock.isHalved()) {beegBlock = beegBlock.getHalf(at.getPos().getCenter());}
        Direction playerFacing = getFacingDirection(player.getXRot(), player.getYRot());
        if (isUsesPlayerFacing()) { playerFacing = faceClicked; }

        // Fill structures on first use
        if (scaledLibrary.isEmpty()) {gatherFurnishingStructures(world.getStructureManager());}

        // Find structure to place
        StructureTemplate struct = getStructure(player, beegBlock, at, faceClicked);
        if (struct == null) { return null; }
        Rotation structureOrientation = fromDirectionToRotation(playerFacing);

        /*
         * Calculate the offset from the beeg block's ZERO
         * to where the structure will be placed
         */
        Vec3i structureOffset = getStructureOffset(player, beegBlock, at, faceClicked, struct, structureOrientation);

        /*
         * Check that all the blocks that will be affected can be affected
         */
        Vec3i origin = new Vec3i(OotilityNumbers.round(beegBlock.minX()), OotilityNumbers.round(beegBlock.minY()), OotilityNumbers.round(beegBlock.minZ()));
        Vec3i structureSize = struct.getSize(fromDirectionToRotation(playerFacing));
        ArrayList<ASIBeegBlock> affected = beegBlock.beegCuboidContaining(structureOffset.offset(origin), structureOffset.offset(structureSize).offset(origin.offset(-1, -1, -1)));
        ASIBeegFurniturePlaceEvent ret = new ASIBeegFurniturePlaceEvent(world, player, beegBlock, this, struct, structureOrientation, structureOffset);
        for (ASIBeegBlock block : affected) {

            // A single non-empty block can cancel this whole operation
            if (isRequiresEmptyArea() && !block.isEmpty(world, true, null, 0)) { return null; }

            // A single cancelled block break can prevent the entire operation
            if (isClearsArea()) {
                ASIBeegBreakEvent prepped = block.prepareBeegBreak(null, player, world);
                if (prepped.isCanceled()) { return null; }
                ret.getClearingQueue().put(block, prepped);
            }
        }

        // Post event
        ret.generateChoiceSettings();
        MinecraftForge.EVENT_BUS.post(ret);
        return ret;
    }

    /**
     * Places down this furniture at this block, taking
     * into account the direction the player is facing
     * as well as the face they clicked.
     *
     * @param player Optional player, to check special conditions like if they are crouching
     * @param at The position to place this furniture at
     * @param faceClicked The face that was clicked to place down this block
     * @return If this furniture could be placed down
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public boolean tryPlace(@NotNull ServerPlayer player, @NotNull ASIWorldBlock at, @NotNull Direction faceClicked) {
        if (!(at.getWorld() instanceof ServerLevel)) { return false; }
        ServerLevel world = (ServerLevel) at.getWorld();

        // Prepare to place
        ASIBeegFurniturePlaceEvent prep = preparePlace(player, at, faceClicked);
        if (prep == null) { return false; }
        if (prep.isCanceled()) { return false; }

        // Actually place
        place(prep);
        return true;
    }

    /**
     * Places down this furniture at this block, taking
     * into account the direction the player is facing
     * as well as the face they clicked.
     *
     * @param prep The data for the placing of this structure
     *
     * @return If this furniture could be placed down
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public boolean place(@NotNull ASIBeegFurniturePlaceEvent prep) {

        // Clear area as queued
        if (isClearsArea()) {
            for (Map.Entry<ASIBeegBlock, ASIBeegBreakEvent> clear : prep.getClearingQueue().entrySet()) {
                clear.getKey().executeBeegBreak(clear.getValue());
            }
        }

        // Finalize settings
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(prep.getStructureRotation());
        StructureProcessor proc = ASIFurnitureChoiceProcessor.getInstance()
                .withChoiceOfLog(prep.getChoiceLog())
                .withChoiceOfPlanks(prep.getChoicePlanks())
                .withChoiceOfWood(prep.getChoiceWood())
                .withChoiceOfWool(prep.getChoiceWool());
        settings.addProcessor(proc);

        /*
         * Now is time to actually place down the structure
         */
        Vec3i structureOffset = prep.getStructureOffset();
        ASIBeegBlock beegBlock = prep.getBeegBlock();
                BlockPos cookedPos = BlockPos.containing(
                structureOffset.getX() + beegBlock.minX(),
                structureOffset.getY() + beegBlock.minY(),
                structureOffset.getZ() + beegBlock.minZ());
        prep.getStructure().placeInWorld(prep.getWorld(), cookedPos, cookedPos, settings, StructureBlockEntity.createRandom(0), Block.UPDATE_CLIENTS);
        return true;
    }

    /**
     * @param sOff Side offset in unrotated structure coordinates (+X)
     * @param vOff Vertical offset in unrotated structure coordinates (+Y)
     * @param fOff Forward offset in unrotated structure coordinates (+Z)
     *
     * @param structureRotation Rotation that will be applied to the structure
     *
     * @param structureSize Size of the structure in unrotated structure coordinates
     *
     * @return The correct offset to apply to this structure
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull Vec3i relativeToAbsoluteOffsets(int sOff, int vOff, int fOff, @NotNull Rotation structureRotation, @NotNull Vec3i structureSize) {

        // Fix rotation
        switch (structureRotation) {
            case CLOCKWISE_180:
                sOff += structureSize.getX() - 1;
                fOff += structureSize.getZ() - 1;
                break;

            case CLOCKWISE_90:
                fOff += structureSize.getZ() - 1;
                break;

            case COUNTERCLOCKWISE_90:
                sOff += structureSize.getX() - 1;
                break;

            case NONE:
            default:
                break;
        }

        // Apply
        int x, z;
        switch (structureRotation) {
            case CLOCKWISE_90:
            case COUNTERCLOCKWISE_90:
                x = fOff;
                z = sOff;
                break;

            case NONE:
            case CLOCKWISE_180:
            default:
                x = sOff;
                z = fOff;
                break;
        }

        // That's it
        return new Vec3i(x, vOff, z);
    }
}
