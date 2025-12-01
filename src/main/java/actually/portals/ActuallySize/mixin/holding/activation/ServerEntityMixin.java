package actually.portals.ActuallySize.mixin.holding.activation;

import net.minecraft.server.level.ServerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {

    /*
    @Shadow
    @Final
    private Entity entity;

    @Inject(method = "sendPairingData", at = @At("RETURN"))
    protected void onSendPairingDataReturn(ServerPlayer pPlayer, Consumer<Packet<ClientGamePacketListener>> pConsumer, CallbackInfo ci) {

        // If this entity is an Item-Entity duality entity, it must be synced to its holder when pairing data is sent.

        EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) entity;
        if (dualityEntity.actuallysize$isActive()) {
            actuallysize.Log("BROADCASTING ACTIVATED ITEM ENTITY " + entity.getScoreboardName());
            ItemDualityCounterpart dualityItem = (ItemDualityCounterpart) (Object) dualityEntity.actuallysize$getItemCounterpart();
            if (dualityItem == null || dualityItem.actuallysize$getItemStackLocation() == null) { return; }

            // Create packet to send over the network and send to those tracking this entity
            ASINSItemEntityActivationPacket packet = new ASINSItemEntityActivationPacket(dualityItem.actuallysize$getItemStackLocation(), entity);
            ASINetworkManager.broadcastEntityUpdate(entity, packet);
        }
    }
    //*/
}
