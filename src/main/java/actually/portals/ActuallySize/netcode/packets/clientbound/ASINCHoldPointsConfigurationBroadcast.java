package actually.portals.ActuallySize.netcode.packets.clientbound;

import actually.portals.ActuallySize.netcode.ASIClientsidePacketHandler;
import actually.portals.ActuallySize.netcode.packets.auxiliary.ASINAExplorerStatementBit;
import actually.portals.ActuallySize.netcode.packets.serverbound.ASINSHoldPointsConfigurationSync;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSRegisterableHoldPoint;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemExplorerStatement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Supplier;

/**
 * A hold points configuration from server to other players
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASINCHoldPointsConfigurationBroadcast extends ASINSHoldPointsConfigurationSync {

    /**
     * The ID of the player being synced
     *
     * @since 1.0.0
     */
    final int playerIndex;

    /**
     * The index of the player
     *
     * @since 1.0.0
     */
    public int getPlayerIndex() { return playerIndex; }

    /**
     * @param sync The list of statement-hold to sync over the network
     * @param from The player being sycned in this broadcast
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINCHoldPointsConfigurationBroadcast(@NotNull HashMap<ItemExplorerStatement<?, ?>, ASIPSRegisterableHoldPoint> sync, @NotNull ServerPlayer from) {
        super(sync);
        playerIndex = from.getId();
    }

    /**
     * @param bits The list of statement-hold to sync over the network
     * @param yay Throwaway boolean to differentiate HashMap constructors after Type Erasure,
     *            this constructor is used when rebuilding the network packet so you probably
     *            do not want to use this one.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINCHoldPointsConfigurationBroadcast(@NotNull HashMap<ASINAExplorerStatementBit, Integer> bits, @NotNull ServerPlayer from, boolean yay) {
        super(bits, yay);
        playerIndex = from.getId();
    }

    /**
     * @param buf The list of statements received from the network
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINCHoldPointsConfigurationBroadcast(@NotNull FriendlyByteBuf buf) {
        super(buf);
        playerIndex = buf.readVarInt();
    }

    @Override
    public void encode(@NotNull FriendlyByteBuf buff) {
        super.encode(buff);
        buff.writeVarInt(playerIndex);
    }

    /**
     * Find the holder, entity, and item, and link them together
     *
     * @since 1.0.0
     * @author Gunging
     */
    public void handle(@NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    ASIClientsidePacketHandler.handleRemotePlayerHoldConfiguration(this, contextSupplier));
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
