package actually.portals.ActuallySize.netcode.packets.serverbound;

import actually.portals.ActuallySize.netcode.ASINetworkManager;
import actually.portals.ActuallySize.netcode.packets.clientbound.ASINCHoldPointsSyncReply;
import actually.portals.ActuallySize.pickup.ASIPickupSystemManager;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSRegisterableHoldPoint;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * Asks from the server to send their statement network IDs
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASINSHoldPointsSyncRequest {

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINSHoldPointsSyncRequest() {}

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINSHoldPointsSyncRequest(@NotNull FriendlyByteBuf buff) { }

    /**
     * @param buff A buffer in which to write the bytes to send over the network
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void encode(@NotNull FriendlyByteBuf buff) { }

    /**
     * Find the holder, entity, and item, and link them together
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void handle(@NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {

            // Identify the sender
            ServerPlayer player = contextSupplier.get().getSender();
            if (player == null) { return; }

            // Iterate all namespaces
            for (String namespace : ASIPickupSystemManager.HOLD_POINT_REGISTRY.listStatementNamespaces()) {

                // To Sync
                ArrayList<ASIPSRegisterableHoldPoint> toSync = ASIPickupSystemManager.HOLD_POINT_REGISTRY.listHoldPoints(namespace);
                if (toSync.isEmpty()) { continue; }

                // Send information
                ASINCHoldPointsSyncReply packet = new ASINCHoldPointsSyncReply(namespace, toSync);
                ASINetworkManager.serverToPlayer(player, packet);
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
