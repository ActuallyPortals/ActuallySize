package actually.portals.ActuallySize.netcode.packets.clientbound;

import actually.portals.ActuallySize.netcode.ASIClientsidePacketHandler;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSRegisterableHoldPoint;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A packet with the capability of syncing all hold points over
 * the network, or at the very least their enum indices.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASINCHoldPointsSyncReply {

    /**
     * The list that contains the statements
     *
     * @since 1.0.0
     */
    @NotNull HashMap<String, Integer> synced;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @NotNull HashMap<String, Integer> getSynced() { return synced; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @NotNull String getNamespace() { return namespace; }

    /**
     * The namespace of these statements
     *
     * @since 1.0.0
     */
    @NotNull String namespace;

    /**
     * @param namespace The namespace of these statements, theoretically it
     *                  could be read from the first statement since those are
     *                  guaranteed to be the same namespace, BUT, you know, if
     *                  I put it as a separate argument it will make this even
     *                  more explicit. All these statements belong to the same
     *                  namespace!
     *
     * @param sync The list of statements to sync over the network
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINCHoldPointsSyncReply(@NotNull String namespace, @NotNull ArrayList<ASIPSRegisterableHoldPoint> sync) {
        synced = new HashMap<>();
        for (ASIPSRegisterableHoldPoint st : sync) { synced.put(st.getNamespacedKey().getPath(), st.getOrdinal()); }
        this.namespace = namespace;
    }

    /**
     * @param buf The list of statements received from the network
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINCHoldPointsSyncReply(@NotNull FriendlyByteBuf buf) {
        synced = new HashMap<>();
        namespace = buf.readUtf();
        int count = buf.readVarInt();
        for (int i = 0; i < count; i++) {

            // Decode
            String path = buf.readUtf();
            int network = buf.readVarInt();

            // Put
            synced.put(path, network);
        }
    }

    /**
     * @param buff A buffer in which to write the bytes to send over the network
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void encode(@NotNull FriendlyByteBuf buff) {

        // Append namespace
        buff.writeUtf(namespace);

        // Append list size
        buff.writeVarInt(synced.size());

        // Append each statement
        for (Map.Entry<String, Integer> st : synced.entrySet()) {

            // Encode only the path
            buff.writeUtf(st.getKey());

            // Encode network index
            buff.writeVarInt(st.getValue());
        }
    }

    /**
     * Find the holder, entity, and item, and link them together
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void handle(@NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    ASIClientsidePacketHandler.handleStatementSync(this, contextSupplier));
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
