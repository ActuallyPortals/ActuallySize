package actually.portals.ActuallySize.mixin.holding.food;

import actually.portals.ActuallySize.pickup.mixininterfaces.Combinable;
import actually.portals.ActuallySize.pickup.mixininterfaces.FoodEffectsGet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.function.Supplier;

@Mixin(FoodProperties.class)
public abstract class FoodPropertiesMixin implements Combinable<FoodProperties>, FoodEffectsGet {

    @Shadow public abstract boolean isFastFood();

    @Shadow public abstract int getNutrition();

    @Shadow public abstract boolean canAlwaysEat();

    @Shadow public abstract boolean isMeat();

    @Shadow public abstract float getSaturationModifier();

    @Shadow @Final private List<Pair<Supplier<MobEffectInstance>, Float>> effects;

    @Override
    public @NotNull List<Pair<Supplier<MobEffectInstance>, Float>> actuallysize$getFoodEffects() { return effects; }

    @Override
    public @NotNull FoodProperties actuallysize$combineWith(@NotNull FoodProperties other) {
        FoodProperties.Builder builder = new FoodProperties.Builder();

        // Adopt the booleans
        if (other.canAlwaysEat() || this.canAlwaysEat()) { builder.alwaysEat(); }
        if (other.isMeat() || this.isMeat()) { builder.meat(); }
        if (other.isFastFood() && this.isFastFood()) { builder.fast(); }

        // Combine nutrition
        builder.nutrition(this.getNutrition() + other.getNutrition());
        builder.saturationMod(this.getSaturationModifier() + other.getSaturationModifier());

        // Combine effects
        for (Pair<Supplier<MobEffectInstance>, Float> eff : this.actuallysize$getFoodEffects()) { builder.effect(eff.getFirst(), eff.getSecond()); }
        for (Pair<Supplier<MobEffectInstance>, Float> eff : ((FoodEffectsGet) other).actuallysize$getFoodEffects()) { builder.effect(eff.getFirst(), eff.getSecond()); }

        // Done
        return builder.build();
    }
}
