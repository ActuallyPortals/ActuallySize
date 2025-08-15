package actually.portals.ActuallySize.netcode.packets.serverbound;

import actually.portals.ActuallySize.netcode.ASINetworkManager;
import actually.portals.ActuallySize.netcode.packets.clientbound.ASINCHoldPointsSyncReply;
import actually.portals.ActuallySize.pickup.ASIPickupSystemManager;
import actually.portals.ActuallySize.pickup.actions.ASIPSHoldingSyncAction;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSRegisterableHoldPoint;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * Asks from the server to send various information on the held item system
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASINSHoldingSyncRequest {

    /**
     * If syncing initial connection stuff too
     *
     * @since 1.0.0
     */
    boolean withNetworkIndices;

    /**
     * If syncing nearby active item-entity dualities
     *
     * @since 1.0.0
     */
    boolean withActiveDualities;

    /**
     * If syncing nearby configurable-hold-point entities
     *
     * @since 1.0.0
     */
    boolean withPointConfigurations;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINSHoldingSyncRequest(boolean network, boolean config, boolean active) {
        withNetworkIndices = network;
        withPointConfigurations = config;
        withActiveDualities = active;
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINSHoldingSyncRequest(@NotNull FriendlyByteBuf buff) {
        this.withNetworkIndices = buff.readBoolean();
        this.withPointConfigurations = buff.readBoolean();
        this.withActiveDualities = buff.readBoolean();
    }

    /**
     * @param buff A buffer in which to write the bytes to send over the network
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void encode(@NotNull FriendlyByteBuf buff) {
        buff.writeBoolean(withNetworkIndices);
        buff.writeBoolean(withPointConfigurations);
        buff.writeBoolean(withActiveDualities);
    }

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

            // Attempt to resolve this sync request
            ASIPSHoldingSyncAction action = new ASIPSHoldingSyncAction(player);
            action.tryResolve();
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
