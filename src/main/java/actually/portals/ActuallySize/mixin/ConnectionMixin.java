package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public abstract class ConnectionMixin {


    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V", at = @At("HEAD"))
    protected void onSendCall(Packet<?> pPacket, PacketSendListener pSendListener, CallbackInfo ci) {
        if (true) { return; }

        // Filter out packets that we are not interested in
        switch (pPacket.getClass().getName()) {
            case "net.minecraft.network.protocol.game.ClientboundMoveEntityPacket$Pos":
            case "net.minecraft.network.protocol.game.ClientboundSoundPacket":
            case "net.minecraft.network.protocol.game.ClientboundSetTimePacket":
            case "net.minecraft.network.protocol.game.ServerboundMovePlayerPacket$Pos":
            case "net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket":
            case "net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket":
            case "net.minecraft.network.protocol.game.ClientboundRotateHeadPacket":
            case "net.minecraft.network.protocol.game.ClientboundSystemChatPacket":
            case "net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket":
            case "net.minecraft.network.protocol.game.ServerboundMovePlayerPacket$Rot":
            case "net.minecraft.network.protocol.game.ClientboundMoveEntityPacket$PosRot":
            case "net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket":
            case "net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket":
            case "net.minecraft.network.protocol.game.ClientboundMoveEntityPacket$Rot":
            case "net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket":
            case "net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket":
            case "net.minecraft.network.protocol.game.ServerboundMovePlayerPacket$PosRot":
            case "net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket":
            case "net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket":
            case "net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket":
            case "net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket":
            case "net.minecraft.network.protocol.game.ClientboundPlayerChatPacket":
            case "net.minecraft.network.protocol.game.ClientboundSetHealthPacket":
            case "net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket":
            case "net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket":
            case "net.minecraft.network.protocol.game.ClientboundKeepAlivePacket":
            case "net.minecraft.network.protocol.game.ServerboundKeepAlivePacket":
            case "net.minecraft.network.protocol.game.ServerboundContainerClosePacket":
            case "net.minecraft.network.protocol.game.ClientboundSetPassengersPacket":
            case "net.minecraft.network.protocol.game.ServerboundSwingPacket":
            case "net.minecraft.network.protocol.game.ClientboundLightUpdatePacket":
            case "net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket":
            case "net.minecraft.network.protocol.game.ClientboundSetExperiencePacket":
                return;

            case "net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket":     // Sometimes happens

            case "net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket":     // Client when changing selected hotbar slot, clientside
            case "net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket":       // When hand is updated in other clients :FLUSH:??? serverside

            case "net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket":     // Client when picking up item

            case "net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket":   // When picking up or placing down cow, serverside
            case "net.minecraft.network.protocol.game.ServerboundUseItemOnPacket":          // When placing down cow, clientside
            case "net.minecraft.network.protocol.game.ServerboundInteractPacket":           // When picking up cow, clientside

            case "net.minecraft.network.protocol.game.ServerboundContainerClickPacket":     // When moving stuff around in CLIENT survival inventory
                // Server-side detection of inventory items being moved around?

            case "net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket": // When moving stuff around in CLIENT creative inventory

            case "net.minecraft.network.protocol.game.ServerboundPlayerActionPacket":       // Clientside when client drops an item

                ActuallySizeInteractions.Log("ASI CMH OSC&6 Accepted &e " + pPacket.getClass().getName());
                break;

            case "net.minecraft.network.protocol.game.ClientboundBundlePacket":             // Serverside when client drops an item

                ClientboundBundlePacket bundle = (ClientboundBundlePacket) pPacket;

                for(Packet<ClientGamePacketListener> pat : bundle.subPackets()) {
                    ActuallySizeInteractions.Log("ASI CMH OSC&2 Bundled &a " + pat.getClass().getName());
                }
                break;

            default:
                ActuallySizeInteractions.Log("ASI CMH OSC&3 Sending &b " + pPacket.getClass().getName());
                break;
        }
    }
}
