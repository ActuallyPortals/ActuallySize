package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.pickup.mixininterfaces.TimeDurationModifiable;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MobEffectInstance.class)
public abstract class MobEffectInstanceMixin implements Comparable<MobEffectInstance>, net.minecraftforge.common.extensions.IForgeMobEffectInstance, TimeDurationModifiable {

    @Shadow private int duration;

    @Override
    public void actuallysize$setDuration(int duration) { this.duration = duration; }

    @Override
    public int actuallysize$getDuration() { return duration; }
}
