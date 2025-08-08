package actually.portals.ActuallySize.netcode.packets.auxiliary;

import gunging.ootilities.GungingOotilitiesMod.exploring.ItemExplorerStatement;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * Simple class to pack the Network Statement into one self-contained object bit to encode and decode
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASINAExplorerStatementBit {

    /**
     * The "options" that go along with this statement
     *
     * @since 1.0.0
     */
    @NotNull final String options;

    /**
     * The Network Index of this Statement
     *
     * @since 1.0.0
     */
    final int networkIndex;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public int getNetworkIndex() { return networkIndex; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public @NotNull String getOptions() { return options; }

    /**
     * @param statement The Explorer Statement being encoded
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINAExplorerStatementBit(@NotNull ItemExplorerStatement<?,?> statement) {
        this(statement.getNetworkIndex(), statement.getOptions());
    }

    /**
     * @param networkIndex The Network Index of this Statement
     * @param options The "options" that go along with this statement
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINAExplorerStatementBit(int networkIndex, @NotNull String options) {
        this.networkIndex = networkIndex;
        this.options = options;
    }

    /**
     * @param buff Bytes synced across the network
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINAExplorerStatementBit(@NotNull FriendlyByteBuf buff) {
        this.networkIndex = buff.readVarInt();
        this.options = buff.readUtf();
    }

    /**
     * Allows to be used as Hash Map key
     *
     * @param obj The other possible Network Statement Option
     *
     * @return If it is equal to this one
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ASINAExplorerStatementBit)) { return false; }
        ASINAExplorerStatementBit ot = (ASINAExplorerStatementBit) obj;
        return networkIndex == ot.networkIndex && options.equals(ot.options);
    }

    /**
     * @param buff A buffer in which to write the bytes to send over the network
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void encode(@NotNull FriendlyByteBuf buff) {

        // Encode the Statement Network Index
        buff.writeVarInt(networkIndex);

        // Encode the Statement Options
        buff.writeUtf(options);
    }
}
