package actually.portals.ActuallySize.netcode;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.netcode.packets.clientbound.*;
import actually.portals.ActuallySize.netcode.packets.serverbound.ASINSHoldPointsConfigurationSync;
import actually.portals.ActuallySize.netcode.packets.serverbound.ASINSHoldPointsSyncRequest;
import actually.portals.ActuallySize.netcode.packets.serverbound.ASINSPreferredSize;
import actually.portals.ActuallySize.netcode.packets.serverbound.ASINSEntityDualitySyncRequest;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The class tasked to handle Netcode for ASI
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASINetworkManager {

    /**
     * The main serverbound connection channel
     *
     * @since 1.0.0
     */
    private static final SimpleChannel MAIN_CHANNEL =
            NetworkRegistry.ChannelBuilder.named(
                            ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, "main"))
                    .serverAcceptedVersions((version) -> true )
                    .clientAcceptedVersions((version) -> true)
                    .networkProtocolVersion(() -> "1").simpleChannel();

    /**
     * Registers all network packets to their appropriate channel, intended
     * to be called ONCE during mod loading and only then.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void register() {
        int i = 0;

        MAIN_CHANNEL.messageBuilder(ASINSPreferredSize.class, i++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ASINSPreferredSize::encode)
                .decoder(ASINSPreferredSize::new)
                .consumerMainThread(ASINSPreferredSize::handle).add();

        MAIN_CHANNEL.messageBuilder(ASINSEntityDualitySyncRequest.class, i++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ASINSEntityDualitySyncRequest::encode)
                .decoder(ASINSEntityDualitySyncRequest::new)
                .consumerMainThread(ASINSEntityDualitySyncRequest::handle).add();

        MAIN_CHANNEL.messageBuilder(ASINSHoldPointsSyncRequest.class, i++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ASINSHoldPointsSyncRequest::encode)
                .decoder(ASINSHoldPointsSyncRequest::new)
                .consumerMainThread(ASINSHoldPointsSyncRequest::handle).add();

        MAIN_CHANNEL.messageBuilder(ASINSHoldPointsConfigurationSync.class, i++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ASINSHoldPointsConfigurationSync::encode)
                .decoder(ASINSHoldPointsConfigurationSync::new)
                .consumerMainThread(ASINSHoldPointsConfigurationSync::handle).add();

        MAIN_CHANNEL.messageBuilder(ASINCItemEntityActivationPacket.class, i++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ASINCItemEntityActivationPacket::encode)
                .decoder(ASINCItemEntityActivationPacket::new)
                .consumerMainThread(ASINCItemEntityActivationPacket::handle).add();

        MAIN_CHANNEL.messageBuilder(ASINCItemEntityDeactivationPacket.class, i++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ASINCItemEntityDeactivationPacket::encode)
                .decoder(ASINCItemEntityDeactivationPacket::new)
                .consumerMainThread(ASINCItemEntityDeactivationPacket::handle).add();

        MAIN_CHANNEL.messageBuilder(ASINCItemEntityEscapePacket.class, i++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ASINCItemEntityEscapePacket::encode)
                .decoder(ASINCItemEntityEscapePacket::new)
                .consumerMainThread(ASINCItemEntityEscapePacket::handle).add();

        MAIN_CHANNEL.messageBuilder(ASINCItemEntityFluxPacket.class, i++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ASINCItemEntityFluxPacket::encode)
                .decoder(ASINCItemEntityFluxPacket::new)
                .consumerMainThread(ASINCItemEntityFluxPacket::handle).add();

        MAIN_CHANNEL.messageBuilder(ASINCHoldPointsSyncReply.class, i++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ASINCHoldPointsSyncReply::encode)
                .decoder(ASINCHoldPointsSyncReply::new)
                .consumerMainThread(ASINCHoldPointsSyncReply::handle).add();

        MAIN_CHANNEL.messageBuilder(ASINCHoldPointsConfigurationBroadcast.class, i++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ASINCHoldPointsConfigurationBroadcast::encode)
                .decoder(ASINCHoldPointsConfigurationBroadcast::new)
                .consumerMainThread(ASINCHoldPointsConfigurationBroadcast::handle).add();
    }

    /**
     * @param msg The packet to send from the local client to the server.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void playerToServer(@Nullable Object msg) {
        MAIN_CHANNEL.send(PacketDistributor.SERVER.noArg(), msg);
    }

    /**
     * @param msg The packet to send from the local client to everyone online.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void playerToEveryone(@Nullable Object msg) {
        MAIN_CHANNEL.send(PacketDistributor.ALL.noArg(), msg);
    }

    /**
     * @param msg The packet to send from a local client to another specific remote client.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void playerToPlayer(@NotNull ServerPlayer player, @Nullable Object msg) {
        MAIN_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    /**
     * Broadcasts from the server to all clients tracking this entity
     *
     * @param who Entity in question
     * @param msg Packet to send
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void broadcastEntityUpdate(@NotNull Entity who, @Nullable Object msg) {
        MAIN_CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> who), msg);
    }

    /**
     * Broadcasts from the server to the specified player
     *
     * @param who Player in question
     * @param msg Packet to send
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void serverToPlayer(@NotNull ServerPlayer who, @Nullable Object msg) {
        MAIN_CHANNEL.send(PacketDistributor.PLAYER.with(() -> who), msg);
    }
}
