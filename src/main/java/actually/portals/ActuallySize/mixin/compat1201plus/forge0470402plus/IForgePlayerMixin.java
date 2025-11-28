package actually.portals.ActuallySize.mixin.compat1201plus.forge0470402plus;

import actually.portals.ActuallySize.ASIUtilities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.extensions.IForgePlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = IForgePlayer.class, remap = false)
public interface IForgePlayerMixin {

    @Shadow boolean isCloseEnough(Entity entity, double dist);

    /**
     * @author Actually Portals
     *
     * @reason So that reach scales with your player size
     */
    @Overwrite
    default boolean canReachRaw(Entity entity, double padding) {
        Player self = (Player) this;

        double range = self.getAttributeValue(ForgeMod.ENTITY_REACH.get()) + padding;
        double size = ASIUtilities.getEffectiveSize(self);
        if (size < 0.65) { size = 0.65; }

        return isCloseEnough(entity, range * size);
    }

    /**
     * @author Actually Portals
     *
     * @reason So that reach scales with your player size
     */
    @Overwrite
    default boolean canReachRaw(BlockPos pos, double padding) {
        Player self = (Player) this;

        double size = ASIUtilities.getEffectiveSize(self);
        double reach = self.getAttributeValue(ForgeMod.BLOCK_REACH.get()) + padding;
        double dist = self.getEyePosition().distanceToSqr(Vec3.atCenterOf(pos));
        if (size < 0.65) { size = 0.65; }

        return dist <= reach * reach * size * size;
    }
}
