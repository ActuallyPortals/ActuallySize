package actually.portals.ActuallySize.compatibilities.pehkui;

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

    /**
     * @param mob The entity to resize
     * @param scale The value to change this to
     *
     * @see virtuoel.pehkui.api.ScaleTypes
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void SetEntityScaleInstant(@NotNull Entity mob, double scale) {
        SetEntityScaleInstant(mob, ScaleTypes.BASE, scale);
    }

    /**
     * @param mob The entity to resize
     * @param scaleType The type of scale to change
     * @param scale The value to change this to
     *
     * @see virtuoel.pehkui.api.ScaleTypes
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void SetEntityScaleInstant(@NotNull Entity mob, @NotNull ScaleType scaleType, double scale) {
        ScaleData scaleData = scaleType.getScaleData(mob);
        scaleData.setScale((float) scale);
    }
}
