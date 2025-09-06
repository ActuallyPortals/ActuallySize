package actually.portals.ActuallySize.pickup.mixininterfaces;

import actually.portals.ActuallySize.pickup.holding.model.ASIPSModelPartInfo;
import org.jetbrains.annotations.Nullable;

/**
 * The capability of a held entity to know the model part
 * it is held in, of its holder, if applicable for the
 * hold point it is held by.
 *
 * @author Actually Portals
 * @since 1.0.0
 */
public interface ModelPartHoldable {

    /**
     * @return The model part associated with this entity's hold point
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @Nullable ASIPSModelPartInfo actuallysize$getHeldModelPart();

    /**
     * In the clientside, this is the clientside tick. Despite the model
     * part being updated multiple times per tick, this only changes once
     * every few seconds and behaves as a timer to send the packet to the
     * server.
     * <br><br>
     * In the serverside, this updates every time a packet is received from
     * any of the clients. It really does store the latest server tick this
     * happened.
     * <br><br>
     * In both situations, if this tick happened too long ago, the model
     * part information is reset and the default coordinates of the model
     * part become used.
     *
     * @return The last tick in which this model part info was updated*
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    long actuallysize$getModelPartTime();

    /**
     * @see #actuallysize$getModelPartTime()
     *
     * @param t The last tick in which this model part info was updated*
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    void actuallysize$setModelPartTime(long t);
}
