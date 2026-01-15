package actually.portals.ActuallySize.world.mixininterfaces;

/**
 * Allows to synchronize a slot
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface ForceSlotSynchronization {

    /**
     * @param pSlotIndex The slot to force an update of
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$synchronizeSlotToRemote(int pSlotIndex);
}
