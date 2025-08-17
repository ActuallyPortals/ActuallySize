package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.world.mixininterfaces.AmountMatters;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin implements AmountMatters {

    @Unique
    @Nullable Double actuallysize$matterAmount = null;

    @Override
    public void actuallysize$setAmount(@Nullable Double amount) { actuallysize$matterAmount = amount; }

    @Override
    @Nullable public Double actuallysize$getAmount() { return actuallysize$matterAmount; }
}
