package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.pickup.holding.points.ASIPSHoldPointRegistry;
import actually.portals.ActuallySize.pickup.mixininterfaces.HoldPointConfigurable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements HoldPointConfigurable {

    protected PlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) { super(pEntityType, pLevel); }

    @Unique @NotNull ASIPSHoldPointRegistry actuallysize$localRegistry = new ASIPSHoldPointRegistry();

    @Override
    public @NotNull ASIPSHoldPointRegistry actuallysize$getLocalHoldPoints() {
        return actuallysize$localRegistry;
    }

    @Override
    public void actuallysize$setLocalHoldPoints(@NotNull ASIPSHoldPointRegistry reg) {

        // Update registry while keeping a reference to the old
        ASIPSHoldPointRegistry old = actuallysize$localRegistry;
        actuallysize$localRegistry = reg;

        // When these change in the server, they must be synced to clients and flux
        if (!level().isClientSide) {

            //todo Flux from old hold point to new hold point
        }
    }
}
