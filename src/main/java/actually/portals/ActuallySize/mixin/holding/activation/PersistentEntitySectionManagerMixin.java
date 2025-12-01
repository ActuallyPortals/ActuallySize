package actually.portals.ActuallySize.mixin.holding.activation;

import actually.portals.ActuallySize.pickup.actions.ASIPSDualityDeactivationAction;
import actually.portals.ActuallySize.pickup.mixininterfaces.EntityDualityCounterpart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.entity.Visibility;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(PersistentEntitySectionManager.class)
public abstract class PersistentEntitySectionManagerMixin<T extends EntityAccess> {

    @Shadow @Final EntitySectionStorage<T> sectionStorage;

    @Inject(method = "updateChunkStatus(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/entity/Visibility;)V", at = @At(value = "HEAD"))
    protected void onChunkUnload(ChunkPos pPos, Visibility pVisibility, CallbackInfo ci) {

        // Chunk unload only, really
        if (pVisibility != Visibility.HIDDEN) { return; }

        // Compare every entity
        this.sectionStorage.getExistingSectionsInChunk(pPos.toLong()).forEach((entitySection) -> {

            // Compare current visibility
            Visibility current = entitySection.getStatus();

            // If it was already hidden, sleep
            if (!current.isAccessible()) { return; }

            // Collect Entity Counterparts
            ArrayList<EntityDualityCounterpart> counterparts = new ArrayList<>();
            entitySection.getEntities().filter((entity) -> !entity.isAlwaysTicking()).forEach((entityCounterpart) -> {

                // We only really care about actual entities
                if (!(entityCounterpart instanceof Entity)) { return; }

                // And active dualities, at that
                EntityDualityCounterpart dualityEntity = (EntityDualityCounterpart) entityCounterpart;
                if (dualityEntity.actuallysize$isActive()) {
                    //actuallysize.Log("ASI OCU &e ENTITY DUALITY <PRE!> DE-SPAWNED?! " + ((Entity) entityCounterpart).getScoreboardName());
                    counterparts.add(dualityEntity);
                }
            });

            // Now miss me with that concurrent modification exception
            for (EntityDualityCounterpart entity : counterparts) {
                ASIPSDualityDeactivationAction action = new ASIPSDualityDeactivationAction(entity);
                action.tryResolve();
            }
        });
    }
}
