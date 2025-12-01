package actually.portals.ActuallySize.mixin.holding.activation;

import actually.portals.ActuallySize.pickup.actions.ASIPSHoldingSyncAction;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Inject(method = "placeNewPlayer", at = @At("RETURN"))
    public void onPlayerLogin(Connection pNetManager, ServerPlayer pPlayer, CallbackInfo ci) {

        // Resolve without asking
        ASIPSHoldingSyncAction syncing = new ASIPSHoldingSyncAction(pPlayer);
        syncing.withNetworkIndices();
        syncing.withConfigurables();
        syncing.resolve();;
    }
}
