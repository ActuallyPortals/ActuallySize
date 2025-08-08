package actually.portals.ActuallySize.pickup.events;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;

/**
 * An entity fired when an entity is picked up due to ASI mod interactions
 *
 * @since 1.0.0
 * @author Actually Portals
 */
@Cancelable
public class ASIPSEntityPickupEntityEvent extends EntityEvent {

    /**
     * The entity that initiated the pickup action, who is doing the picking up
     *
     * @since 1.0.0
     */
    @NotNull Entity tiny;

    /**
     * @return The entity that initiated the pickup action, who is doing the picking up
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public Entity getBeeg() { return getEntity(); }

    /**
     * @return The entity that is being picked up
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public Entity getTiny() { return tiny; }

    /**
     * An event fired when the ASI plugin detects that
     * some entity wants to pick up something else.
     *
     * @param tiny The entity being picked up
     * @param beeg The entity doing the picking up
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASIPSEntityPickupEntityEvent(@NotNull Entity beeg, @NotNull Entity tiny) {
        super(beeg);
        this.tiny = tiny;
    }
}
