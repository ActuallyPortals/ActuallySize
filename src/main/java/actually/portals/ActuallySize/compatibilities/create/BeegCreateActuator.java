package actually.portals.ActuallySize.compatibilities.create;

import actually.portals.ActuallySize.world.grid.ASIWorldBlock;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

/**
 * A mixin for Create Kinetic Components that
 * may be interacted with in a special manner
 * by giants
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface BeegCreateActuator {

    /**
     * @return The result of this interaction
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull InteractionResult actuallysize$whenBeegUsed(@NotNull ASIWorldBlock where, @NotNull Player who, @NotNull InteractionHand hand, @NotNull BlockHitResult hit);
}
