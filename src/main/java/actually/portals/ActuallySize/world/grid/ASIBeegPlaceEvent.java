package actually.portals.ActuallySize.world.grid;

import actually.portals.ActuallySize.ASIUtilities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * An ASI-Specific Event fired when placing a block in the beeg grid
 *
 * @since 1.0.0
 * @author Actually Portals
 */
@Cancelable
public class ASIBeegPlaceEvent extends BlockEvent.EntityMultiPlaceEvent {

    /**
     * The Beeg Grid block that this will be placed to
     *
     * @since 1.0.0
     */
    @NotNull final ASIBeegBlock beegBlock;

    /**
     * @param snapshots The blocks that will be placed
     * @param placedAgainst The block the original block was placed against
     * @param entity The entity doing the block-placing
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIBeegPlaceEvent(@NotNull List<BlockSnapshot> snapshots,
                             @NotNull BlockState placedAgainst,
                             @NotNull Entity entity,
                             @NotNull ASIBeegBlock beegBlock) {
        super(snapshots, placedAgainst, entity);
        this.beegBlock = beegBlock;
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @NotNull ASIBeegBlock getBeegBlock() { return beegBlock; }
}
