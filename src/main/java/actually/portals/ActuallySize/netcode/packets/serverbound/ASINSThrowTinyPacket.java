package actually.portals.ActuallySize.netcode.packets.serverbound;

import actually.portals.ActuallySize.pickup.actions.ASIPSDualityThrowAction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * A packet sent to the server telling them that
 * the local player pressed the input that means
 * "I want to throw this tiny"
 * <br><br>
 * This packet is simple, and carries a very specific
 * meaning, as the only slot that may throw tinies as
 * of currently is the main hand = the item currently
 * selected in the hotbar.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASINSThrowTinyPacket {

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINSThrowTinyPacket() {}

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINSThrowTinyPacket(@NotNull FriendlyByteBuf buf) {}

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public void encode(@NotNull FriendlyByteBuf buf) {}

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

            // Create action and resolve it
            ASIPSDualityThrowAction action = new ASIPSDualityThrowAction(player);
            action.tryResolve();
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
