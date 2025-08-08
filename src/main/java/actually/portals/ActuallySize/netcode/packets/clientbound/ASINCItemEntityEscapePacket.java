package actually.portals.ActuallySize.netcode.packets.clientbound;

import actually.portals.ActuallySize.netcode.ASIClientsidePacketHandler;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityEscapeAction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * When an Item-Entity duality escapes, this
 * is sent to the clients to ensure they unlink
 * it in their side.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASINCItemEntityEscapePacket {

    /**
     * The entity ID of the Entity Counterpart of the Item-Entity duality
     *
     * @since 1.0.0
     */
    final int entityCounterpartID;

    /**
     * The Entity of the Item-Entity duality
     *
     * @since 1.0.0
     */
    @Nullable Entity entityCounterpart;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable public Entity getEntityCounterpart() { return entityCounterpart; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable public Entity getEntityCounterpart(@NotNull Level world) {

        // Done is done
        if (entityCounterpart != null) { return entityCounterpart; }

        // Find and return
        entityCounterpart = world.getEntity(entityCounterpartID);
        return entityCounterpart;
    }

    /**
     * @param entityCounterpart The entity counterpart of the Item-Entity
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINCItemEntityEscapePacket(@NotNull Entity entityCounterpart) {
        entityCounterpartID = entityCounterpart.getId();
        this.entityCounterpart = entityCounterpart;
    }

    /**
     * @param buff The bytes received from over the network
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINCItemEntityEscapePacket(@NotNull FriendlyByteBuf buff) {
        entityCounterpartID = buff.readVarInt();
    }

    /**
     * @param buff A buffer in which to write the bytes to send over the network
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void encode(@NotNull FriendlyByteBuf buff) {
        buff.writeVarInt(entityCounterpartID);
    }

    /**
     * Find the holder, entity, and item, and link them together
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void handle(@NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->

                    ASIClientsidePacketHandler.handleItemEntityAction(

                            new ASIPSDualityEscapeAction(this,
                                    ASIClientsidePacketHandler.getDualityActionWorld(contextSupplier)),

                            contextSupplier));
        });
        contextSupplier.get().setPacketHandled(true);
    }
}