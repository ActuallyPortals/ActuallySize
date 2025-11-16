package actually.portals.ActuallySize.netcode.packets.serverbound;


import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityEscapeAction;
import actually.portals.ActuallySize.pickup.holding.ASIPSHoldPoint;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.ItemDualityCounterpart;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * A packet sent when the player struggles to escape a hold
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASINSStrugglePacket {

    /**
     * The clientside tick this packet was sent
     * on, for a better struggle computation.
     *
     * @since 1.0.0
     */
    long tick;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public long getTick() { return tick; }

    /**
     * @param tick The clientside tick this packet was sent on, for a better struggle computation.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINSStrugglePacket(long tick) { this.tick = tick; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINSStrugglePacket(@NotNull FriendlyByteBuf buff) { this.tick = buff.readVarLong(); }

    /**
     * @param buff A buffer in which to write the bytes to send over the network
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void encode(@NotNull FriendlyByteBuf buff) { buff.writeVarLong(tick); }

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

            // Struggling only matters when held
            EntityDualityCounterpart entityDuality = (EntityDualityCounterpart) player;
            if (!entityDuality.actuallysize$isHeld()) { return; }

            // Must be held in a real hold point
            ASIPSHoldPoint hold = entityDuality.actuallysize$getHoldPoint();
            if (hold == null || hold.isVirtualHoldPoint()) { return; }

            // Register struggle and calculate totals
            ArrayList<Long> totals = registerStruggle(ASINSPreferredSize.GetEffectiveUUID(player), getTick());
            //STG//ActuallySizeInteractions.Log("Received STRUGGLE &e " + getTick() + " &7 for a total of &b " + totals.size());
            if (hold.canBeEscapedByStruggling(entityDuality.actuallysize$getItemEntityHolder(), entityDuality, totals)) {
                //STG//ActuallySizeInteractions.Log("&a >>> ESCAPED <<< ");
                entityDuality.actuallysize$escapeDuality();

            } else {
                //STG//ActuallySizeInteractions.Log("&c >>> DENIED <<< ");
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }

    //region As Manager
    @NotNull public static ArrayList<Long> registerStruggle(@NotNull UUID uuid, long forTick) {

        // Copy over prior struggles to new array
        ArrayList<Long> interim = struggleHistory.get(uuid);
        ArrayList<Long> ret = new ArrayList<>();
        if (interim != null) {

            /*
             * Struggle calculations run on the ideal 20 tick counter,
             * which means the maximum struggling you can reach is 20
             * by struggling 20 consecutive ticks at some point within
             * the 60 tick buffer.
             */
            for (Long struggle : interim) {

                // Skip repeats :eyes_sus: damned hackers
                if (forTick == struggle) { continue; }
                if (forTick - struggle < 61) { ret.add(struggle); }
            }
        }

        // Add latest struggle
        ret.add(forTick);

        // Save changes
        struggleHistory.put(uuid, ret);
        return ret;
    }

    /**
     * Keeps track of the struggles received by specific UUIDs
     *
     * @since 1.0.0
     */
    @NotNull public static final HashMap<UUID, ArrayList<Long>> struggleHistory = new HashMap<>();
    //endregion
}
