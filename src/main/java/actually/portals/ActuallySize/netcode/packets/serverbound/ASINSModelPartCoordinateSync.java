package actually.portals.ActuallySize.netcode.packets.serverbound;

import actually.portals.ActuallySize.pickup.holding.model.ASIPSModelPartInfo;
import actually.portals.ActuallySize.pickup.mixininterfaces.ModelPartHoldable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Sent from clients rendering an item-entity duality
 * so the server has an approximate position of where
 * the entity should be (real).
 * <br><br>
 * It would be a security concern to trust the clients
 * fully, tho. These coordinates are just suggestions
 * then.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASINSModelPartCoordinateSync {

    /**
     * The origin of the model part
     *
     * @since 1.0.0
     */
    @NotNull Vec3 origin;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull Vec3 getOrigin() { return origin; }

    /**
     * Rotation around the X axis
     *
     * @since 1.0.0
     */
    double pitch;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public double getPitch() { return pitch; }

    /**
     * The rotation around the Y axis
     *
     * @since 1.0.0
     */
    double yaw;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public double getYaw() { return yaw; }

    /**
     * The rotation around the Y axis
     *
     * @since 1.0.0
     */
    double roll;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public double getRoll() { return roll; }

    /**
     * ID of the Entity concerned with this model part sync
     *
     * @since 1.0.0
     */
    int id;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public int getID() { return id; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINSModelPartCoordinateSync(@NotNull Entity who, @NotNull Vec3 origin, double pitch, double yaw, double roll) {
        this.id = who.getId();
        this.origin = origin;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINSModelPartCoordinateSync(@NotNull FriendlyByteBuf buff) {
        this.id = buff.readVarInt();
        this.origin = new Vec3(buff.readDouble(), buff.readDouble(), buff.readDouble());
        this.pitch = buff.readDouble();
        this.yaw = buff.readDouble();
        this.roll = buff.readDouble();
    }

    /**
     * @param buff A buffer in which to write the bytes to send over the network
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void encode(@NotNull FriendlyByteBuf buff) {
        buff.writeVarInt(id);
        buff.writeDouble(origin.x);
        buff.writeDouble(origin.y);
        buff.writeDouble(origin.z);
        buff.writeDouble(pitch);
        buff.writeDouble(yaw);
        buff.writeDouble(roll);
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

            Entity found = player.level().getEntity(id);
            if (found == null) { return; }

            // Update model info with these specs
            ASIPSModelPartInfo info = ((ModelPartHoldable) found).actuallysize$getHeldModelPart();
            if (info == null) { return; }
            info.updateModelPart(origin, pitch, yaw, roll);
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
