package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.pickup.mixininterfaces.Edacious;
import actually.portals.ActuallySize.world.mixininterfaces.AmountMatters;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin implements AmountMatters {

    @Shadow @Final @javax.annotation.Nullable private Entity directEntity;

    @Shadow public abstract Holder<DamageType> typeHolder();

    @Unique
    @Nullable Double actuallysize$matterAmount = null;

    @Override
    public void actuallysize$setAmount(@Nullable Double amount) { actuallysize$matterAmount = amount; }

    @Override
    @Nullable public Double actuallysize$getAmount() { return actuallysize$matterAmount; }

    @Inject(method = "getLocalizedDeathMessage", at = @At("HEAD"), cancellable = true)
    public void onSquish(LivingEntity tiny, CallbackInfoReturnable<Component> cir) {

        // It needs to be direct attack
        if (directEntity != null && tiny instanceof Edacious) {
            Entity beeg = directEntity;

            /*
             * When consumed (real)
             */
            Edacious eda = (Edacious) tiny;
            if (eda.actuallysize$wasConsumed()) {

                cir.setReturnValue(tiny.getDisplayName().copy().append(Component.translatable("death.attack.actuallysizeinteractions.nom")).append(beeg.getDisplayName()));
                cir.cancel();
                return;
            }

            // Okay now get me relative size. Beeg messages
            double relative = ASIUtilities.getRelativeScale(tiny, beeg);
            if (relative > 4) {
                if (typeHolder().is(DamageTypes.PLAYER_ATTACK) || typeHolder().is(DamageTypes.MOB_ATTACK)) {

                    cir.setReturnValue(tiny.getDisplayName().copy().append(Component.translatable("death.attack.actuallysizeinteractions.squish")).append(beeg.getDisplayName()));
                    cir.cancel();
                    return;
                }

            // Tinies defeating beegs LOL
            } else if (relative < 0.25) {
                if (typeHolder().is(DamageTypes.PLAYER_ATTACK) || typeHolder().is(DamageTypes.MOB_ATTACK)) {

                    cir.setReturnValue(tiny.getDisplayName().copy().append(Component.translatable("death.attack.actuallysizeinteractions.felled")).append(beeg.getDisplayName()));
                    cir.cancel();
                    return;
                }
            }
        }
    }
}
