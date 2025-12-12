package actually.portals.ActuallySize.world.grid.events;

import actually.portals.ActuallySize.world.grid.ASIBeegBlock;
import actually.portals.ActuallySize.world.grid.ASIWorldBlock;
import actually.portals.ActuallySize.world.grid.fluids.ASIWorldFluid;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * An event that drains a lot of fluid blocks in a
 * Beeg Block and, sometimes, its vicinity
 *
 * @author Actually Portals
 * @since 1.0.0
 */
@Cancelable
public class ASIBeegDrainEvent extends ASIBeegBucketEvent {

    /**
     * @param beegBlock          The beeg block that is being drained
     * @param toDrain            Blocks that will be drained, excluding original drain
     * @param originalDrain      The block that was first drained
     * @param player             Player doing the beeg draining
     * @param fluid              The fluid being drained, EMPTY if all fluids
     * @param emptyBucketItem    The bucket before going into this
     * @param filledBucketItem   The bucket after going into this
     * @param totalFluidUnits    The total number of fluid units expected to be drained
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIBeegDrainEvent(@NotNull ASIBeegBlock beegBlock, @NotNull ArrayList<ASIWorldFluid> toDrain, @Nullable ASIWorldBlock originalDrain, @NotNull ServerPlayer player, @NotNull Fluid fluid, @NotNull ItemStack emptyBucketItem, @NotNull ItemStack filledBucketItem, int totalFluidUnits) {
        super(beegBlock, toDrain, originalDrain, player, fluid, emptyBucketItem, filledBucketItem, totalFluidUnits);
    }
}