package actually.portals.ActuallySize.netcode.packets.clientbound;

import actually.portals.ActuallySize.netcode.ASIClientsidePacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * The flux packet is composed of a deactivation and an activation
 * packet in one. This is necessary because the holders may be
 * different as much as the slot where the entity will be.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASINCItemEntityFluxPacket  {

    /**
     * The packet related to the entity being deactivated
     *
     * @since 1.0.0
     */
    @NotNull final ASINCItemEntityDeactivationPacket from;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public ASINCItemEntityDeactivationPacket getFrom() { return from; }

    /**
     * The packet related to the entity being reactivated
     *
     * @since 1.0.0
     */
    @NotNull final ASINCItemEntityActivationPacket to;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public ASINCItemEntityActivationPacket getTo() { return to; }

    /**
     * @param from The packet related to the entity being deactivated
     * @param to The packet related to the entity being reactivated
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINCItemEntityFluxPacket(@NotNull ASINCItemEntityDeactivationPacket from, @NotNull ASINCItemEntityActivationPacket to) {
        this.from = from;
        this.to = to;
    }

    /**
     * @param buff The bytes received from over the network
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINCItemEntityFluxPacket(@NotNull FriendlyByteBuf buff) {
        from = new ASINCItemEntityDeactivationPacket(buff);
        to = new ASINCItemEntityActivationPacket(buff);
    }

    /**
     * @param buff A buffer in which to write the bytes to send over the network
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void encode(@NotNull FriendlyByteBuf buff) {
        from.encode(buff);
        to.encode(buff);
    }

    /**
     * Find the holder, entity, and item, and link them together
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void handle(@NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ASIClientsidePacketHandler.handleItemEntityFlux(this, contextSupplier));
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
