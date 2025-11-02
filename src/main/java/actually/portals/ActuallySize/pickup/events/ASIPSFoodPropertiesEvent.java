package actually.portals.ActuallySize.pickup.events;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallyServerConfig;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraftforge.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * When determining if a tiny is edible, use this event
 * to modify the food properties associated with this
 * entity.
 *
 * @author Actually Portals
 * @since 1.0.0
 */
public class ASIPSFoodPropertiesEvent extends EntityEvent {

    /**
     * The resultant food properties that will be
     * added to the Item Stack that represents this
     * entity.
     *
     * @since 1.0.0
     */
    @NotNull final FoodProperties.Builder builder = new FoodProperties.Builder();

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @NotNull public FoodProperties.Builder getBuilder() { return builder; }

    /**
     * The actual nutrition value that will be set for
     * these Food Properties at the end of the event pass.
     *
     * @since 1.0.0
     */
    int nutrition;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public int getNutrition() { return nutrition; }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public void setNutrition(int n) { nutrition = n; }

    /**
     * The actual saturation value that will be set for
     * these Food Properties at the end of the event pass.
     *
     * @since 1.0.0
     */
    float saturation;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public float getSaturation() { return saturation; }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public void setSaturation(float s) { saturation = s; }

    /**
     * The size of the entity being eaten, widely used in ASI
     *
     * @since 1.0.0
     */
     double size;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public double getSize() { return size; }

    /**
     * @param entity The entity which item stack representation is to be eaten
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    public ASIPSFoodPropertiesEvent(@NotNull LivingEntity entity) {
        super(entity);

        // Some starter nutritional values
        size = ASIUtilities.getEffectiveSize(entity);
        double meat = size * 1.5;
        //FOO//ActuallySizeInteractions.Log("ASI &1 FOO &r Food Properties Construct for &3 " + entity.getClass().getSimpleName() + " " + entity.getScoreboardName() + " &7 of size &e x" + size);

        /*
         * With this option enabled, mob-items are worth a lot
         * more food, in a way making giants prefer eating raw
         * animals isn't that neat?
         */
        if (ActuallyServerConfig.hungryBeegs) {
            if (size > 1) {
                meat = ((size * 1.3) + 2);
                meat = meat * meat;
            } else {
                meat = (size * 1.3) * 5; }
            if (entity instanceof Player) {
                meat *= 2;

                // Creative mode players are delicious
                if (((Player) entity).getAbilities().instabuild) { meat *= 1000; }
            }
        }

        // Apply base nutrition
        nutrition = OotilityNumbers.round(meat);
    }

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override  public LivingEntity getEntity() { return (LivingEntity) super.getEntity(); }
}
