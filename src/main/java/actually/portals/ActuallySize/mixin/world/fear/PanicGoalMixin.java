package actually.portals.ActuallySize.mixin.world.fear;

import actually.portals.ActuallySize.world.mixininterfaces.Jumpstartable;
import actually.portals.ActuallySize.world.mixininterfaces.Rerollable;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PanicGoal.class)
public abstract class PanicGoalMixin extends Goal implements Rerollable, Jumpstartable {

    @Shadow protected abstract boolean findRandomPosition();

    @Override
    public void actuallysize$reroll() {  findRandomPosition();  start();  }

    @Override
    public void actuallysize$jumpstart() { actuallysize$reroll(); }
}
