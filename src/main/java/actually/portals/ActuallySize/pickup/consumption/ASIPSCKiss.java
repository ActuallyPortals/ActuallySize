package actually.portals.ActuallySize.pickup.consumption;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Suggested action where the tiny is only kissed
 * and granted regeneration because kisses heal
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIPSCKiss implements ASIPSPlayerConsumption {

    /**
     * The effects of this kiss
     *
     * @since 1.0.0
     */
    @NotNull final ArrayList<MobEffectInstance> kissEffects = new ArrayList<>();

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @NotNull ArrayList<MobEffectInstance> getKissEffects() { return kissEffects; }

    /**
     * Creates a kiss consumer that applies effects to the
     * kissed player when the kiss ends. These may be
     * modified during the event.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSCKiss() {
        kissEffects.add(new MobEffectInstance(MobEffects.REGENERATION, 400, 1));
        kissEffects.add(new MobEffectInstance(MobEffects.HEAL, 1, 1));
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public void snack(@NotNull ServerPlayer beeg, @NotNull ServerPlayer tiny) {
        for (MobEffectInstance kis : kissEffects) { tiny.addEffect(kis); }
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public boolean eats() { return false; }
}
