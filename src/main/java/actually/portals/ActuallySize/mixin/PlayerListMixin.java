package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.netcode.ASINetworkManager;
import actually.portals.ActuallySize.netcode.packets.clientbound.ASINCHoldPointsConfigurationBroadcast;
import actually.portals.ActuallySize.netcode.packets.clientbound.ASINCHoldPointsSyncReply;
import actually.portals.ActuallySize.pickup.ASIPickupSystemManager;
import actually.portals.ActuallySize.pickup.holding.points.ASIPSRegisterableHoldPoint;
import actually.portals.ActuallySize.pickup.mixininterfaces.HoldPointConfigurable;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Inject(method = "placeNewPlayer", at = @At("RETURN"))
    public void onPlayerLogin(Connection pNetManager, ServerPlayer pPlayer, CallbackInfo ci) {

        // Iterate all namespaces
        for (String namespace : ASIPickupSystemManager.HOLD_POINT_REGISTRY.listStatementNamespaces()) {

            // To Sync
            ArrayList<ASIPSRegisterableHoldPoint> toSync = ASIPickupSystemManager.HOLD_POINT_REGISTRY.listHoldPoints(namespace);
            if (toSync.isEmpty()) { continue; }

            // Send information
            ASINCHoldPointsSyncReply packet = new ASINCHoldPointsSyncReply(namespace, toSync);
            ASINetworkManager.serverToPlayer(pPlayer, packet);
        }
        if (pPlayer.getServer() == null) { return; }

        // Send information on other players
        for (ServerPlayer p : pPlayer.getServer().getPlayerList().getPlayers()) {
            if (p.getId() != pPlayer.getId()) {

                // Send information
                ASINCHoldPointsConfigurationBroadcast broadcast = new ASINCHoldPointsConfigurationBroadcast(((HoldPointConfigurable) p).actuallysize$getLocalHoldPoints().getRegisteredPoints(), p);
                ASINetworkManager.serverToPlayer(pPlayer, broadcast);
            }
        }
    }
}
