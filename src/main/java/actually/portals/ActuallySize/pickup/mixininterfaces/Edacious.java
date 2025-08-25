package actually.portals.ActuallySize.pickup.mixininterfaces;

import net.minecraft.world.food.FoodProperties;
import org.jetbrains.annotations.Nullable;

/**
 * Anyway well basically it is funny to consume edible
 * drops of something that you consume... 『 anyway~ 』
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface Edacious {

    /**
     * Food properties extracted from the contained
     * entity from their loot drops the moment they
     * were eaten
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$setEdaciousProperties(@Nullable FoodProperties props);

    /**
     * Food properties extracted from the contained
     * entity from their loot drops the moment they
     * were eaten
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable FoodProperties actuallysize$getEdaciousProperties();

    /**
     * @param eda If this was consumed
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$setWasConsumed(boolean eda);

    /**
     * @return If this was consumed
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    boolean actuallysize$wasConsumed();
}
