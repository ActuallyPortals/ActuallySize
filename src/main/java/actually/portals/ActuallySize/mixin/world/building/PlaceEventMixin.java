package actually.portals.ActuallySize.mixin.world.building;

import actually.portals.ActuallySize.world.mixininterfaces.Directed;
import net.minecraft.core.Direction;
import net.minecraftforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockEvent.EntityPlaceEvent.class)
public class PlaceEventMixin implements Directed {

    @Unique
    @Nullable
    private static Direction actuallysize$direction;

    @Override
    public @Nullable Direction actuallysize$getDirection() { return actuallysize$direction; }

    @Override
    public void actuallysize$setDirection(@Nullable Direction direction) { actuallysize$direction = direction; }
}
