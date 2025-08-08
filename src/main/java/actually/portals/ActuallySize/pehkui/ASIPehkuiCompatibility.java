package actually.portals.ActuallySize.pehkui;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleType;
import virtuoel.pehkui.api.ScaleTypes;

/**
 * Accesses the API of Pehkui
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIPehkuiCompatibility {

    /**
     * @param mob The entity of which to check the scale
     *
     * @return The scale of this entity
     *
     * @see virtuoel.pehkui.api.ScaleTypes
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static float GetEntityScale(@NotNull Entity mob) { return GetEntityScale(mob, ScaleTypes.BASE); }

    /**
     * @param mob The entity of which to check the scale
     * @param scaleType The type of scale to check
     *
     * @return The scale of this entity
     *
     * @see virtuoel.pehkui.api.ScaleTypes
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static float GetEntityScale(@NotNull Entity mob, @NotNull ScaleType scaleType) {
        ScaleData scaleData = scaleType.getScaleData(mob);
        return scaleData.getScale();
    }
}
