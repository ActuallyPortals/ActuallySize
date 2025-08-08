package actually.portals.ActuallySize.pickup.mixininterfaces;

import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Items may have entities associated with them, even
 * when the entity is not spawned in the world. This
 * extension helps keeping track of that.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface ItemDualityCounterpart {

    /**
     * @return The entity stored in this ItemStack, if there is one,
     *         and strictly the entity stored in this ItemStack tag.
     *
     * @param world World to generate entity in, needed to parse entity.
     *              Will update this entity's world everytime to match
     *              the one provided to this method.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable Entity actuallysize$getEnclosedEntity(@NotNull Level world);

    /**
     * @return Looks into the tag and finds out the UUID of
     *         the entity within, if there is one. The UUID
     *         may return null even if there is a valid entity
     *         here, by the way.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable UUID actuallysize$getEnclosedEntityUUID();

    /**
     * @return If this item has an Entity saved in its NBT tag.
     *
     * @param world Needed when reading an entity from NBT.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    boolean actuallysize$hasEnclosedEntity(@NotNull Level world);

    /**
     * @return The Entity of this Item-Entity duality. Check that it actually exists
     *         in the world with {@link Entity#isAddedToWorld()}, as this may simply
     *         represent the Entity that will be spawned to the world in the future
     *         when this duality is activated, or was spawned to the world in the past.
     *         <p>
     *         Essentially identical to {@link #actuallysize$getEnclosedEntity(Level)}
     *         except it is guaranteed to have a different UUID. This method only returns null
     *         when this ItemStack has not attempted to generate an entity ever.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable Entity actuallysize$getEntityCounterpart();

    /**
     * @param world Regenerate the entity counterpart from the enclosed entity.
     *
     * @return Essentially identical to {@link #actuallysize$getEnclosedEntity(Level)}
     *         except it is guaranteed to have a different UUID than the one saved in the NBT tag.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable Entity actuallysize$readyEntityCounterpart(@NotNull Level world);

    /**
     * @param who The Entity in the world that this item is linked to, or itself if this is the entity
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$setEntityCounterpart(@Nullable Entity who);

    /**
     * @return The entity that holds this ItemStack in their hand (or inventory for the of players)
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable ItemEntityDualityHolder actuallysize$getItemEntityHolder();

    /**
     * @param who The entity that holds this ItemStack in their hand (or inventory for the of players)
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$setItemEntityHolder(@Nullable ItemEntityDualityHolder who);

    /**
     * @return In the holder's inventory, where is this item stack?
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable ItemStackLocation<? extends Entity> actuallysize$getItemStackLocation();

    /**
     * @param who In the holder's inventory, where is this item stack?
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$setItemStackLocation(@Nullable ItemStackLocation<? extends Entity> who);

    /**
     * @return If this ItemStack has its Entity counterpart in the world
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    boolean actuallysize$isDualityActive();
}
