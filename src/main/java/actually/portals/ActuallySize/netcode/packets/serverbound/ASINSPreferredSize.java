package actually.portals.ActuallySize.netcode.packets.serverbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * An example packet where the clients tell the server what
 * size they prefer to respawn or log-in as. I believe a similar
 * result can be achieved with configuration files directly, but
 * I wanted a quick and easy server-bound Network Packet example
 * to test and work with.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASINSPreferredSize {

    /**
     * The scale the local player prefers to be
     *
     * @since 1.0.0
     */
    private final double preferredSize;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public double getPreferredSize() { return preferredSize; }

    /**
     * @param preferredSize The scale the local player prefers to be
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINSPreferredSize(double preferredSize) {
        this.preferredSize = preferredSize;
    }

    /**
     * @param buff A buffer with a single double value, representing
     *             the scale the local player prefers to be.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINSPreferredSize(@NotNull FriendlyByteBuf buff) {
        this(buff.readDouble());
    }

    /**
     * @param buff A buffer in which to write a single double value,
     *             the scale the local player prefers to be.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void encode(@NotNull FriendlyByteBuf buff) {
        buff.writeDouble(preferredSize);
    }

    /**
     * The server will take a note of the current preferred scale
     * of this player, updating the static table.
     *
     * @param contextSupplier The context by which this packet is handled
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void handle(@NotNull Supplier<NetworkEvent.Context> contextSupplier) {

        // Find the player who is sending this packet
        ServerPlayer player = contextSupplier.get().getSender();
        if (player == null) { contextSupplier.get().setPacketHandled(false); return; }

        // Done
        SetPreferredSize(player, this.preferredSize);
    }

    /**
     * @param who The player to set their preferred size.
     * @param scale The scale they are said to prefer
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void SetPreferredSize(@NotNull ServerPlayer who, double scale) { SetPreferredSize(GetEffectiveUUID(who), scale); }

    /**
     * @param who The player who you seek their preferred size.
     *
     * @return The known preferred size of this player.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static double GetPreferredSize(@NotNull ServerPlayer who) { return GetPreferredSize(GetEffectiveUUID(who)); }

    /**
     * @param who The player whose UUID you seek
     *
     * @return The UUID that is used, for this player, by the Preferred Size system.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static UUID GetEffectiveUUID(@NotNull ServerPlayer who) {

        // If there are valid sessions, those are good enough for this purpose
        RemoteChatSession session = who.getChatSession();
        if (session != null) { return session.sessionId(); }

        // If not, we must get creative
        return who.getUUID();
    }

    /**
     * @param who The player to set their preferred size.
     * @param scale The scale they are said to prefer
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void SetPreferredSize(@NotNull UUID who, double scale) {
        preferredSizes.put(who, scale);
    }

    /**
     * @param who The UUID of who you seek their preferred size.
     *
     * @return The known preferred size of this player.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static double GetPreferredSize(@NotNull UUID who) {

        // Find in collection
        Double found = preferredSizes.get(who);

        // Missing?
        if (found == null) { return 1; }

        // Too small?
        if (found <= 0) { return 0.04; }

        // Good enough
        return found;
    }

    /**
     * Not so much as a network packet, but as the system that
     * keeps track of the Preferred Sizes of player, the collection
     * to store everyone's preferred size
     *
     * @since 1.0.0
     */
    @NotNull public static final HashMap<UUID, Double> preferredSizes = new HashMap<>();
}
