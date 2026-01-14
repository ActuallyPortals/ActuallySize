package actually.portals.ActuallySize.world.blocks.furniture;

import actually.portals.ActuallySize.world.grid.ASIBeegBlock;
import actually.portals.ActuallySize.world.grid.ASIWorldBlock;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Optional;

/**
 * Represents a furniture similar to a torch, that
 * can be placed standing or at a wall.
 *
 * @author Actually Portals
 * @since 1.0.0
 */
public class ASIBeegTorch extends ASIBeegFurnishing {

    /**
     * @param base The block being replaced by this structure
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIBeegTorch(@NotNull StandingAndWallBlockItem base, @NotNull String standingKey, @NotNull String wallKey) {
        super(base.getBlock());
        standing_key = ASIBeegFurnishing.BEEG_FURNISHING_SIGIL + standingKey;
        wall_key = ASIBeegFurnishing.BEEG_FURNISHING_SIGIL + wallKey;
    }

    /**
     * The name of the structure that will be
     * used for this torch BEEG FURNITURE when
     * placed on the ground
     * <br><br>
     * For example: "torch" in <br>
     * <code>actuallysize:asi_torch_x8</code>
     *
     * @since 1.0.0
     */
    @NotNull final String standing_key;

    /**
     * The name of the structure that will be
     * used for this torch BEEG FURNITURE
     * when placed on a wall
     * <br><br>
     * For example: "wall_torch" in <br>
     * <code>actuallysize:asi_wall_torch_x8</code>
     *
     * @since 1.0.0
     */
    @NotNull final String wall_key;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public void gatherFurnishingStructures(@NotNull StructureTemplateManager loadedStructures) {
        for (ResourceLocation namespacedKey : loadedStructures.listTemplates().toList()) {

            // Only asi_ path participates in the BEEG FURNITURE system
            String key = namespacedKey.getPath();
            if (!key.startsWith(ASIBeegFurnishing.BEEG_FURNISHING_SIGIL)) { continue; }

            // Identify standing or wall torch structure
            boolean isWall = key.startsWith(wall_key);
            boolean isStanding = !isWall && key.startsWith(standing_key);
            if (!isStanding && !isWall) { continue; }

            // Find scale
            int scaleIndex = key.lastIndexOf("_x");
            if (scaleIndex < 1) { continue; }
            Double scaleSigil = OotilityNumbers.DoubleParse(key.substring(scaleIndex + 2));
            if (scaleSigil == null || scaleSigil <= 1) { continue; }

            // The scale is appropriate, this structure qualifies
            int scale = OotilityNumbers.round(scaleSigil);
            StructureTemplate structure = null;

            // Identify structure
            try {
                Optional<StructureTemplate> found = loadedStructures.get(namespacedKey);
                if (found.isPresent()) { structure = found.get(); }
            } catch (ResourceLocationException ignored) { continue; }
            if (structure == null) { continue; }

            // Finally, include
            if (isWall) { addWallStructure(scale, structure); }
            if (isStanding) { addStructure(scale, structure); }
        }
    }

    /**
     * The structures used for wall placement of these torches
     *
     * @since 1.0.0
     */
    @NotNull final HashMap<Integer, StructureTemplate> scaledWallLibrary = new HashMap<>();

    /**
     * @param scale The scale of this structure
     * @param structure The structure to include
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public void addWallStructure(int scale, @NotNull StructureTemplate structure) { scaledWallLibrary.put(scale, structure); }

    /**
     * Will find the biggest wall torch that fits your scale.
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
    @Nullable
    public StructureTemplate getWallForScale(double scale, boolean rev) {
        if (scale < minimumScale) { return null; }

        // Prefer exact match no matter what
        StructureTemplate exact = scaledWallLibrary.get(OotilityNumbers.round(scale));
        if (exact != null) { return exact; }

        // Optimized structural search
        int res = rev ? Integer.MAX_VALUE : -1;
        for (Integer i : scaledWallLibrary.keySet()) {
            if (rev) {
                if (i < scale) { continue; }
                if (i < res) { res = i; }
            } else {
                if (i > scale) { continue; }
                if (i > res) { res = i; }
            }
        }

        // That's the one we are looking for
        return scaledWallLibrary.get(res);
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public @Nullable StructureTemplate getStructure(@NotNull ServerPlayer player, @NotNull ASIBeegBlock beegBlock, @NotNull ASIWorldBlock at, @NotNull Direction faceClicked) {

        // Redirect to wall torch variant
        if (faceClicked.getAxis().isHorizontal()) {
            return getWallForScale(beegBlock.getEffectiveScale(), !isBiggestThatFits()); }

        // Use the normal structure index
        return super.getStructure(player, beegBlock, at, faceClicked);
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public @NotNull Vec3i getStructureOffset(@NotNull ServerPlayer player, @NotNull ASIBeegBlock beegBlock, @NotNull ASIWorldBlock at, @NotNull Direction faceClicked, @NotNull StructureTemplate structure, @NotNull Rotation structureRotation) {

        // Crouching means to place torches where you are clicking
        if (player.isShiftKeyDown()) {

            if (faceClicked.getAxis().isHorizontal()) {
                return getFreeOffsetAsWallTorch(player, beegBlock, at, faceClicked, structure, structureRotation);
            } else {
                return getFreeOffsetAsStandingTorch(player, beegBlock, at, faceClicked, structure, structureRotation);
            }

        // Otherwise, centered in the grid
        } else {

            if (faceClicked.getAxis().isHorizontal()) {
                return getCenteredOffsetAsWallTorch(player, beegBlock, at, faceClicked, structure, structureRotation);
            } else {
                return getCenteredOffsetAsStandingTorch(player, beegBlock, at, faceClicked, structure, structureRotation);
            }
        }
    }

    /**
     * @param player The player doing the placing down
     * @param at The world block the player indicated
     * @param beegBlock The beeg block that is determined to be used
     * @param faceClicked The face of the block that was clicked
     * @param structure The structure that will be placed
     * @param structureRotation The rotation of the Structure Template
     *
     * @return The world-coords offset of this wall torch, not grid-centered but free placement
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull Vec3i getFreeOffsetAsWallTorch(@NotNull ServerPlayer player, @NotNull ASIBeegBlock beegBlock, @NotNull ASIWorldBlock at, @NotNull Direction faceClicked, @NotNull StructureTemplate structure, @NotNull Rotation structureRotation) {

        // Centered and standing? That means to put it in the center of the grid.
        Vec3i structureSize = structure.getSize(structureRotation);
        int fOff = (faceClicked.getAxis() == Direction.Axis.X ? structureSize.getX() : structureSize.getZ()) - 1;
        int l = (faceClicked.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 0 : (beegBlock.getEffectiveScale() - fOff));
        int y = OotilityNumbers.ceil(0.5D * (beegBlock.getEffectiveScale() - structureSize.getY()));

        int x, z;
        switch (structureRotation) {
            case CLOCKWISE_90:
            case COUNTERCLOCKWISE_90:
                x = l;
                z = at.getPos().getZ() - beegBlock.minZ();
                break;

            case NONE:
            case CLOCKWISE_180:
            default:
                x = at.getPos().getX() - beegBlock.minX();
                z = l;
        }

        // That's it
        return new Vec3i(x, y, z);
    }

    /**
     * @param player The player doing the placing down
     * @param at The world block the player indicated
     * @param beegBlock The beeg block that is determined to be used
     * @param faceClicked The face of the block that was clicked
     * @param structure The structure that will be placed
     * @param structureRotation The rotation of the Structure Template
     *
     * @return The world-coords offset of this wall torch, centered on the grid
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull Vec3i getCenteredOffsetAsWallTorch(@NotNull ServerPlayer player, @NotNull ASIBeegBlock beegBlock, @NotNull ASIWorldBlock at, @NotNull Direction faceClicked, @NotNull StructureTemplate structure, @NotNull Rotation structureRotation) {

        // Centered and standing? That means to put it in the center of the grid.
        Vec3i structureSize = structure.getSize(structureRotation);
        int sOff = (faceClicked.getAxis() == Direction.Axis.X ? structureSize.getZ() : structureSize.getX());
        if ((faceClicked == Direction.EAST || faceClicked == Direction.NORTH) && (sOff % 2 == 0)) { sOff--; }
        int s = OotilityNumbers.ceil(0.5D * (beegBlock.getEffectiveScale() - sOff));
        int fOff = (faceClicked.getAxis() == Direction.Axis.X ? structureSize.getZ() : structureSize.getX()) - 1;
        int l = (faceClicked.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 0 : (beegBlock.getEffectiveScale() - fOff));
        int y = OotilityNumbers.ceil(0.5D * (beegBlock.getEffectiveScale() - structureSize.getY()));

        int x, z;
        switch (structureRotation) {
            case CLOCKWISE_90:
            case COUNTERCLOCKWISE_90:
                x = l;
                z = s;
                break;

            case NONE:
            case CLOCKWISE_180:
            default:
                x = s;
                z = l;
        }

        // That's it
        return new Vec3i(x, y, z);
    }

    /**
     * @param player The player doing the placing down
     * @param at The world block the player indicated
     * @param beegBlock The beeg block that is determined to be used
     * @param faceClicked The face of the block that was clicked
     * @param structure The structure that will be placed
     * @param structureRotation The rotation of the Structure Template
     *
     * @return The world-coords offset of this standing torch, not grid-centered but free placement
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull Vec3i getFreeOffsetAsStandingTorch(@NotNull ServerPlayer player, @NotNull ASIBeegBlock beegBlock, @NotNull ASIWorldBlock at, @NotNull Direction faceClicked, @NotNull StructureTemplate structure, @NotNull Rotation structureRotation) {

        // Free, standing torch, offset it by the clicked block
        int x = at.getPos().getX() - beegBlock.minX();
        int y = at.getPos().getY() - beegBlock.minY();
        int z = at.getPos().getZ() - beegBlock.minZ();

        // That's it
        return new Vec3i(x, y, z);
    }

    /**
     * @param player The player doing the placing down
     * @param at The world block the player indicated
     * @param beegBlock The beeg block that is determined to be used
     * @param faceClicked The face of the block that was clicked
     * @param structure The structure that will be placed
     * @param structureRotation The rotation of the Structure Template
     *
     * @return The world-coords offset of this standing torch, centered on the grid
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull Vec3i getCenteredOffsetAsStandingTorch(@NotNull ServerPlayer player, @NotNull ASIBeegBlock beegBlock, @NotNull ASIWorldBlock at, @NotNull Direction faceClicked, @NotNull StructureTemplate structure, @NotNull Rotation structureRotation) {

        // Centered and standing? That means to put it in the center of the grid.
        int x = OotilityNumbers.ceil(beegBlock.getEffectiveScale() * 0.5D);
        int y = 0;
        int z = x;

        // That's it
        return new Vec3i(x, y, z);
    }
}
