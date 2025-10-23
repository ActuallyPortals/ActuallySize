package actually.portals.ActuallySize.netcode.packets.serverbound;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.netcode.ASINetworkManager;
import actually.portals.ActuallySize.netcode.packets.clientbound.ASINCItemEntityActivationPacket;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemDualityCounterpart;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Asks from the server if this is entity is am Entity-counterpart or not.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASINSEntityDualitySyncRequest {

    /**
     * The entity ID of the entity being requested
     *
     * @since 1.0.0
     */
    final int entityID;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public int getEntityID() {
        return entityID;
    }

    /**
     * @param entity The entity to request
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINSEntityDualitySyncRequest(@NotNull Entity entity) { this.entityID = entity.getId(); }

    /**
     * @param buff The bytes received from over the network
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINSEntityDualitySyncRequest(@NotNull FriendlyByteBuf buff) {
        entityID = buff.readVarInt();
    }

    /**
     * @param buff A buffer in which to write the bytes to send over the network
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void encode(@NotNull FriendlyByteBuf buff) {
        buff.writeVarInt(entityID);
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

            // Identify the entity
            Level world = player.level();
            Entity target = world.getEntity(getEntityID());
            if (target == null) { return; }

            // Is it a duality counterpart?
            EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) target;
            if (!dualityEntity.actuallysize$isActive()) { return; }
            ItemDualityCounterpart dualityItem = (ItemDualityCounterpart) (Object) dualityEntity.actuallysize$getItemCounterpart();
            if (dualityItem == null) { return; }

            // Send information
            /*HDA*/ActuallySizeInteractions.LogHDA(getClass(), "EDS", "&f Handling sync activation packet for {0}", dualityItem.actuallysize$getItemStackLocation());
            ASINCItemEntityActivationPacket packet = new ASINCItemEntityActivationPacket(dualityItem.actuallysize$getItemStackLocation(), target, dualityEntity.actuallysize$getHoldPoint());
            ASINetworkManager.serverToPlayer(player, packet);
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
