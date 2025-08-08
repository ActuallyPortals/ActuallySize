package actually.portals.ActuallySize.netcode.packets.clientbound;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.netcode.ASIClientsidePacketHandler;
import actually.portals.ActuallySize.pickup.actions.ASIPSDualityActivationAction;
import gunging.ootilities.GungingOotilitiesMod.exploring.*;
import gunging.ootilities.GungingOotilitiesMod.exploring.entities.ISEEntityLocation;
import gunging.ootilities.GungingOotilitiesMod.exploring.entities.ISEEntityStatement;
import gunging.ootilities.GungingOotilitiesMod.exploring.entities.ISEEquipmentSlotted;
import gunging.ootilities.GungingOotilitiesMod.exploring.entities.ISEExplorerStatements;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * When an Item-Entity duality spawns its Entity, we
 * must tell the clients such entity is linked to
 * such item. This packet carries that information
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASINCItemEntityActivationPacket {

    /**
     * The location of the ItemStack that represents
     * this entity.
     *
     * @since 1.0.0
     */
    @Nullable ItemStackLocation<? extends Entity> stackLocation;

    /**
     * The entity ID of the holder of the ItemStack
     *
     * @since 1.0.0
     */
    final int holderID;

    /**
     * The entity ID of the Entity Counterpart of the Item-Entity duality
     *
     * @since 1.0.0
     */
    final int entityCounterpartID;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public int getHolderID() { return holderID; }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable public ItemStackLocation<? extends Entity> getStackLocation(@NotNull Level world) {

        // If already built, return it
        if (stackLocation != null) { return stackLocation; }

        // Otherwise, find holder
        Entity holderAsEntity = world.getEntity(holderID);
        if (holderAsEntity == null) { return null; }
        if (statement == null) { return null; }
        if (!statement.getElaboratorTarget().isInstance(holderAsEntity)) { return null; }

        ItemExplorerElaborator elaborator = statement.prepareElaborator(holderAsEntity);
        ItemStackExplorer explorer = statement.prepareExplorer();
        ItemStackLocation location = explorer.realize(elaborator);

        // Build Item Stack Location
        stackLocation = (ItemStackLocation<? extends Entity>) location;
        return stackLocation;
    }

    /**
     * The Entity of the Item-Entity duality
     *
     * @since 1.0.0
     */
    @Nullable Entity entityCounterpart;

    /**
     * The statement for this activation
     *
     * @since 1.0.0
     */
    @Nullable final ItemExplorerStatement statement;

    /**
     * @return The statement for this activation
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable public ItemExplorerStatement getStatement() { return statement; }

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
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable public ItemStackLocation<? extends Entity> getStackLocation() { return stackLocation; }

    /**
     * @param location The location of the Item counterpart of this Item-Entity
     * @param entityCounterpart The entity counterpart of the Item-Entity
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINCItemEntityActivationPacket(@NotNull ItemStackLocation<? extends Entity> location, @NotNull Entity entityCounterpart) {
        this.holderID = location.getHolder().getId();
        this.stackLocation = location;
        this.statement = location.getStatement();
        entityCounterpartID = entityCounterpart.getId();
        this.entityCounterpart = entityCounterpart;
    }

    /**
     * @param buff The bytes received from over the network
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public ASINCItemEntityActivationPacket(@NotNull FriendlyByteBuf buff) {
        holderID = buff.readVarInt();
        entityCounterpartID = buff.readVarInt();
        this.statement = ExplorerManager.decode(buff);
    }

    /**
     * @param buff A buffer in which to write the bytes to send over the network
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void encode(@NotNull FriendlyByteBuf buff) {
        buff.writeVarInt(holderID);
        buff.writeVarInt(entityCounterpartID);
        statement.encode(buff);
    }

    /**
     * Find the holder, entity, and item, and link them together
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void handle(@NotNull Supplier<NetworkEvent.Context> contextSupplier) {

        contextSupplier.get().enqueueWork(() -> {
            ASIClientsidePacketHandler.handleItemEntityAction(

                    new ASIPSDualityActivationAction(this,
                            ASIClientsidePacketHandler.getDualityActionWorld(contextSupplier)),

                    contextSupplier);

        });

        contextSupplier.get().setPacketHandled(true);
    }
}
