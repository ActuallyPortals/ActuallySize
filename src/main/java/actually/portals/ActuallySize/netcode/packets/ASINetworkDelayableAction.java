package actually.portals.ActuallySize.netcode.packets;

/**
 * Some network action packets will arrive before some other things they depend
 * on, that's why we must schedule them and run them a little later.
 *
 * @author Actually Portals
 * @since 1.0.0
 */
public interface ASINetworkDelayableAction {

    /**
     * @return The number of attempts this action has been tried to be executed
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    int getAttempts();

    /**
     * Increases the number of attempts by one
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    void logAttempt();
}
