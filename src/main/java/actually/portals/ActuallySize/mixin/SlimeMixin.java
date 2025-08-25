package actually.portals.ActuallySize.mixin;

import actually.portals.ActuallySize.pickup.mixininterfaces.Edacious;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Slime.class)
public abstract class SlimeMixin extends Mob implements Enemy {

    protected SlimeMixin(EntityType<? extends Mob> pEntityType, Level pLevel) { super(pEntityType, pLevel); }

    @WrapOperation(method = "remove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    public boolean onSlimeSplit(Level instance, Entity newSlimeEntity, Operation<Boolean> original) {
        // Add normally, sure
        boolean ret = original.call(instance, newSlimeEntity);

        // But also... if eaten... then those also got eaten :spinskull:
        if (ret && newSlimeEntity instanceof LivingEntity) {
            LivingEntity newSlime = (LivingEntity) newSlimeEntity;
            Edacious thisEdacious = (Edacious) this;
            if (thisEdacious.actuallysize$wasConsumed()) {

                // They share the same fate
                Edacious newEdacious = (Edacious) newSlimeEntity;
                newEdacious.actuallysize$setWasConsumed(true);
                newSlime.setLastHurtByPlayer(this.lastHurtByPlayer);
                newSlimeEntity.kill();
            }
        }

        // Done
        return ret;
    }
}
