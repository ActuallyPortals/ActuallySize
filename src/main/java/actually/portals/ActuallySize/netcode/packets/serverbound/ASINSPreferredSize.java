package actually.portals.ActuallySize.netcode.packets.serverbound;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallyServerConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * If this player prefers to be beeg
     *
     * @since 1.0.0
     */
    private final boolean preferredBeeg;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public boolean isPreferredBeeg() { return preferredBeeg; }

    /**
     * If this player prefers to be smol
     *
     * @since 1.0.0
     */
    private final boolean preferredSmol;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public boolean isPreferredSmol() { return preferredSmol; }

    /**
     * @param preferredSize The scale the local player prefers to be
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINSPreferredSize(double preferredSize, boolean beeg, boolean smol) {
        this.preferredSize = preferredSize;
        this.preferredBeeg = beeg;
        this.preferredSmol = smol;
    }

    /**
     * @param buff A buffer with a single double value, representing
     *             the scale the local player prefers to be.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINSPreferredSize(@NotNull FriendlyByteBuf buff) {
        this(buff.readDouble(), buff.readBoolean(), buff.readBoolean());
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
        buff.writeBoolean(preferredBeeg);
        buff.writeBoolean(preferredSmol);
    }

    /**
     * This method is usually called when respawning a player, and also
     * when they first log in to the server in this session. This means
     * that your preferences are only re-applied upon death and upon
     * server reboot. You may send updated preferences in between, and
     * they will be recorded, but they won't apply until you die.
     *
     * @param someone The person to apply these preferences to
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void applyTo(@NotNull ServerPlayer someone) {

        // Interpret settings
        double serverBeeg = ActuallyServerConfig.beegSize;
        boolean serverBeegEnabled = serverBeeg != 1;

        double serverTiny = ActuallyServerConfig.tinySize;
        boolean serverTinyEnabled = serverTiny != 1;

        boolean serverFree = ActuallyServerConfig.enableFreeSize;
        boolean preferredFree = getPreferredSize() != 1;

        // Highest priority to free choice
        if (serverFree && preferredFree) {

            // Clamp between an okay minimum and maximum
            double adjusted = getPreferredSize();
            if (adjusted > 50) { adjusted = 50; }
            if (adjusted < 0.02) { adjusted = 0.02; }

            // Use preferred size
            ASIUtilities.setEntityScale(someone, adjusted);

        // Alternatively, do we want to play beeg?
        } else if (serverBeegEnabled && isPreferredBeeg()) {

            // Use beeg size
            ASIUtilities.setEntityScale(someone, serverBeeg);
        } else if (serverTinyEnabled && isPreferredSmol()) {

            // Use tiny size
            ASIUtilities.setEntityScale(someone, serverTiny);

        // Normalize size, no preferences
        } else { ASIUtilities.setEntityScale(someone, 1); }
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
        contextSupplier.get().enqueueWork(() -> {

            // Find the player who is sending this packet
            ServerPlayer player = contextSupplier.get().getSender();
            if (player == null) { return; }

            // First time? Apply these
            if (GetPreferredSize(player) == null) { applyTo(player); }

            // Apply those settings
            SetPreferredSize(player, this);
        });
        contextSupplier.get().setPacketHandled(true);
    }

    //region As Manager
    /**
     * @param who The player to set their preferred size.
     * @param scale The scale they are said to prefer
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void SetPreferredSize(@NotNull ServerPlayer who, @NotNull ASINSPreferredSize scale) { SetPreferredSize(GetEffectiveUUID(who), scale); }

    /**
     * @param who The player who you seek their preferred size.
     *
     * @return The known preferred size of this player.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable public static ASINSPreferredSize GetPreferredSize(@NotNull ServerPlayer who) { return GetPreferredSize(GetEffectiveUUID(who)); }

    /**
     * @param who The player whose UUID you seek
     *
     * @return The UUID that is used, for this player, by the Preferred Size system.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public static UUID GetEffectiveUUID(@NotNull ServerPlayer who) {

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
    public static void SetPreferredSize(@NotNull UUID who, @NotNull ASINSPreferredSize scale) {
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
    @Nullable public static ASINSPreferredSize GetPreferredSize(@NotNull UUID who) {
        return preferredSizes.get(who);
    }

    /**
     * Not so much as a network packet, but as the system that
     * keeps track of the Preferred Sizes of player, the collection
     * to store everyone's preferred size
     *
     * @since 1.0.0
     */
    @NotNull public static final HashMap<UUID, ASINSPreferredSize> preferredSizes = new HashMap<>();
    //endregion
}
