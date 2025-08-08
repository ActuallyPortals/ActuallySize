package actually.portals.ActuallySize.netcode.packets.serverbound;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.netcode.ASINetworkManager;
import actually.portals.ActuallySize.netcode.packets.auxiliary.ASINAExplorerStatementBit;
import actually.portals.ActuallySize.netcode.packets.clientbound.ASINCHoldPointsConfigurationBroadcast;
import actually.portals.ActuallySize.pickup.ASIPickupSystemManager;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSHoldPointRegistry;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSRegisterableHoldPoint;
import actually.portals.ActuallySize.pickup.mixininterfaces.HoldPointConfigurable;
import gunging.ootilities.GungingOotilitiesMod.exploring.ExplorerManager;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemExplorerStatement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Sent from a client to a server to communicate the
 * Hold Points they configured in their local client
 * <br><br>
 * Usually you'd have to sync a bunch of strings that
 * encode for ResourceLocations, but I've gone through
 * the trouble of syncing their runtime-assigned indices
 * so now I can just use those gg
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASINSHoldPointsConfigurationSync {

    /**
     * This is actually a HashMap of ItemExplorerStatement Network Indices to Registerable Hold Point Ordinal
     *
     * @since 1.0.0
     */
    @NotNull HashMap<ASINAExplorerStatementBit, Integer> synced;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @NotNull HashMap<ASINAExplorerStatementBit, Integer> getSynced() { return synced; }

    /**
     * @param sync The list of statement-hold to sync over the network
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINSHoldPointsConfigurationSync(@NotNull HashMap<ItemExplorerStatement<?,?>, ASIPSRegisterableHoldPoint> sync) {
        synced = new HashMap<>();

        // Map statement network index to hold point ordinal
        for (Map.Entry<ItemExplorerStatement<?,?>, ASIPSRegisterableHoldPoint> st : sync.entrySet()) {
            synced.put(new ASINAExplorerStatementBit(st.getKey()), st.getValue().getOrdinal()); }
    }

    /**
     * @param bits The list of statement-hold to sync over the network
     * @param yay Throwaway boolean to differentiate HashMap constructors after Type Erasure
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINSHoldPointsConfigurationSync(@NotNull HashMap<ASINAExplorerStatementBit, Integer> bits, boolean yay) {
        synced = bits;
    }

    /**
     * @param buf The list of statements received from the network
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINSHoldPointsConfigurationSync(@NotNull FriendlyByteBuf buf) {
        synced = new HashMap<>();
        int count = buf.readVarInt();
        for (int i = 0; i < count; i++) {

            // Decode
            ASINAExplorerStatementBit bit = new ASINAExplorerStatementBit(buf);
            int ordinal = buf.readVarInt();

            // Put
            synced.put(bit, ordinal);
        }
    }

    @NotNull public ASIPSHoldPointRegistry produceRegistry() {

        // Build Configuration
        ASIPSHoldPointRegistry reg = new ASIPSHoldPointRegistry();

        // Parse packet
        for (Map.Entry<ASINAExplorerStatementBit, Integer> syn : this.getSynced().entrySet()) {

            // Find Explorer Statement
            ItemExplorerStatement<?,?> statement = ExplorerManager.getByNetwork(syn.getKey().getNetworkIndex());
            if (statement == null) { continue; }
            statement = statement.withOptions(syn.getKey().getOptions());
            if (statement == null) { continue; }

            // Find Hold Point
            ASIPSRegisterableHoldPoint holdPoint = ASIPickupSystemManager.HOLD_POINT_REGISTRY.getByOrdinal().get(syn.getValue());
            if (holdPoint == null) { continue; }

            // Adjust with options
            reg.registerHoldPoint(statement, holdPoint);
        }

        return reg;
    }

    /**
     * @param buff A buffer in which to write the bytes to send over the network
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void encode(@NotNull FriendlyByteBuf buff) {

        // Append list size
        buff.writeVarInt(synced.size());

        // Append each statement
        for (Map.Entry<ASINAExplorerStatementBit, Integer> st : synced.entrySet()) {

            // Encode the Statement Network Index
            st.getKey().encode(buff);

            // Encode the Hold Point Ordinal
            buff.writeVarInt(st.getValue());
        }
    }

    /**
     * Find the holder, entity, and item, and link them together
     *
     * @since 1.0.0
     * @author Gunging
     */
    public void handle(@NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {

            // Identify the sender
            ServerPlayer player = contextSupplier.get().getSender();
            if (player == null) { return; }

            // Build Configuration
            ASIPSHoldPointRegistry reg = this.produceRegistry();

            // Create packet to sync
            ASINCHoldPointsConfigurationBroadcast bc = new ASINCHoldPointsConfigurationBroadcast(this.getSynced(), player, false);
            for (ServerPlayer name : player.level().getServer().getPlayerList().getPlayers()) {
                if (name.getId() == player.getId()) { continue; }

                // Send packet with new slots configuration
                ASINetworkManager.serverToPlayer(name, bc);
            }

            /*
             * Register onto player
             *
             * This happens after syncing to the other players because this method
             * will also fire DualityFluxEvents to move currently-held entity dualities
             * from their old hold points to their new ones. This in turn prefers the
             * clients to all know the new Hold Points.
             */
            ((HoldPointConfigurable) player).actuallysize$setLocalHoldPoints(reg);

            ActuallySizeInteractions.Log("ASI &dPS REG&7 Accepted for SERVER: ");
            reg.log();
        });
        contextSupplier.get().setPacketHandled(true);
    }
}