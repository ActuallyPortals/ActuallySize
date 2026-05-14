package actually.portals.ActuallySize.mixin.world;

import actually.portals.ActuallySize.ASIUtilities;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {

    @WrapOperation(method = "play", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/sounds/Sound;getAttenuationDistance()I"))
    private int WithGiantHearing(Sound instance, Operation<Integer> original) {

        /*
         * Sound attenuation at 16 blocks is too little for giants.
         */
        int usual = original.call(instance);

        // Giants have better hearing
        Player localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null) {
            double scale = ASIUtilities.getEntityScale(localPlayer);
            if (scale > 2) { usual = OotilityNumbers.ceil(scale * usual); } }

        // Attenuate properly
        return usual;
    }
}
