package actually.portals.ActuallySize.pickup.mixininterfaces;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

/**
 * Allows access of Food Effect Consumers in Food Properties
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface FoodEffectsGet {

    /**
     * @return The food effect consumers
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull List<Pair<Supplier<MobEffectInstance>, Float>> actuallysize$getFoodEffects();
}
