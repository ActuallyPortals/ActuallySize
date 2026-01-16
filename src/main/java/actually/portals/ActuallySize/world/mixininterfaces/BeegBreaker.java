package actually.portals.ActuallySize.world.mixininterfaces;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * When something is in the middle of breaking beeg,
 * this flag becomes set so as for some systems to
 * behave differently
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface BeegBreaker {

    /**
     * @return If this is currently beeg breaking
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    boolean actuallysize$isBeegBreaking();

    /**
     * @param is If this is currently beeg breaking
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$setBeegBreaking(boolean is);

    /**
     * @param drop A drop from a block broken by Beeg Breaking
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$addBeegBreakingDrop(@NotNull ItemStack drop);

    /**
     * @return The list of items broken during last Beeg Breaking pass
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull ArrayList<ItemStack> actuallysize$getBeegBreakingDrops();

    /**
     * @param beeg The player doing the Beeg Breaking
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$setBeegBreaker(@Nullable ServerPlayer beeg);

    /**
     * @return The list of items broken during last Beeg Breaking pass
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable ServerPlayer actuallysize$getBeegBreaker();
}
