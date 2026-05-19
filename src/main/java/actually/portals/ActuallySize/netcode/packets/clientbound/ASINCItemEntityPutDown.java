package actually.portals.ActuallySize.netcode.packets.clientbound;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.netcode.ASIClientsidePacketHandler;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import actually.portals.ActuallySize.pickup.mixininterfaces.GraceImpulsable;
import actually.portals.ActuallySize.pickup.mixininterfaces.GraceLanding;
import actually.portals.ActuallySize.pickup.mixininterfaces.GracePickupable;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Sent when an Item-Entity is put down safely on the ground
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASINCItemEntityPutDown {

    /**
     * The location where the item entity was put down
     *
     * @since 1.0.0
     */
    @NotNull Vec3 placeDownLocation;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public Vec3 getPlaceDownLocation() { return placeDownLocation; }

    /**
     * The number of ticks that this entity will remain in Grace Landing feather falling
     *
     * @since 1.0.0
     */
    int graceTicks;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public int getGraceTicks() { return graceTicks; }

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
    public ASINCItemEntityPutDown(@NotNull Entity who, @NotNull Vec3 placeDownLocation, int graceTicks) {
        this.id = who.getId();
        this.placeDownLocation = placeDownLocation;
        this.graceTicks = graceTicks;
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINCItemEntityPutDown(@NotNull FriendlyByteBuf buff) {
        this.id = buff.readVarInt();
        this.placeDownLocation = new Vec3(buff.readDouble(), buff.readDouble(), buff.readDouble());
        this.graceTicks = buff.readVarInt();
    }

    /**
     * @param buff A buffer in which to write the bytes to send over the network
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void encode(@NotNull FriendlyByteBuf buff) {
        buff.writeVarInt(id);
        buff.writeDouble(placeDownLocation.x);
        buff.writeDouble(placeDownLocation.y);
        buff.writeDouble(placeDownLocation.z);
        buff.writeVarInt(graceTicks);
    }

    /**
     * Applies the position and grace to the specified
     *
     * @param foundEntity Presumably entity found by the ID in this packet,
     *                    but it works with any and all entity really.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void apply(@NotNull Entity foundEntity) {

        // Set all positions
        foundEntity.setDeltaMovement(Vec3.ZERO);
        foundEntity.setPos(placeDownLocation);
        foundEntity.setOldPosAndRot();
        foundEntity.resetFallDistance();

        // Add ticks of grace landing
        GraceLanding grc = (GraceLanding) foundEntity;
        grc.actuallysize$addGraceLanding(graceTicks);

        if (foundEntity instanceof Player) {
            GracePickupable pick = (GracePickupable) foundEntity;
            pick.actuallysize$addGracePickup(5); }

        // Add ticks of grace impulse
        if (foundEntity instanceof GraceImpulsable) {
            GraceImpulsable imp = (GraceImpulsable) foundEntity;
            imp.actuallysize$addGraceImpulse(graceTicks + 10); }
    }

    /**
     * Find the holder, entity, and item, and link them together
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void handle(@NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ASIClientsidePacketHandler.handlePlaceDown(this, contextSupplier));
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
