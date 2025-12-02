package actually.portals.ActuallySize.world.aigoals;

import actually.portals.ActuallySize.world.mixininterfaces.Jumpstartable;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * A copy of the {@link net.minecraft.world.entity.ai.goal.AvoidEntityGoal} that
 * focuses less on entity type and more on the... size... of the target
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class FearBeegsGoal extends Goal implements Jumpstartable {

    /**
     * The mob this goal belongs to
     *
     * @since 1.0.0
     */
    @NotNull final PathfinderMob mob;

    /**
     * The speed modifier applied while fleeing
     *
     * @since 1.0.0
     */
    final double speedModifier;

    /**
     * The maximum distance this mob tries to path to
     *
     * @since 1.0.0
     */
    protected final float maxDistance;

    /**
     * The path this mob decided to take
     *
     * @since 1.0.0
     */
    @Nullable Path path;

    /**
     * The navigation of this mob
     *
     * @since 1.0.0
     */
    final PathNavigation navigation;

    /**
     * @param mob The mob this goal belongs to
     * @param speedModifier The speed modifier applied while fleeing
     * @param maxDistance The maximum distance this mob tries to path to
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public FearBeegsGoal(@NotNull PathfinderMob mob, double speedModifier, float maxDistance) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.maxDistance = maxDistance;
        this.navigation = mob.getNavigation();
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public boolean canUse() {

        // Need to have a target
        LivingEntity toAvoid = mob.getTarget();
        if (toAvoid == null) { return false; }

        // Try to generate path
        return genPath();
    }

    /**
     * @return If a flee path was generated successfully
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public boolean genPath() {

        // Need to have a target
        LivingEntity toAvoid = mob.getTarget();
        if (toAvoid == null) { return false; }

        // Find position to flee to
        Vec3 vec3 = DefaultRandomPos.getPosAway(
                this.mob,
                OotilityNumbers.ceil(maxDistance),
                OotilityNumbers.ceil(maxDistance * 0.3),
                toAvoid.position());
        if (vec3 == null) { return false; }

        // Will not go closer than I already am
        if (toAvoid.distanceToSqr(vec3.x, vec3.y, vec3.z) < toAvoid.distanceToSqr(this.mob)) { return false; }

        // Attempt navigation
        this.path = navigation.createPath(vec3.x, vec3.y, vec3.z, 0);
        return path != null;
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public boolean canContinueToUse() { return !this.navigation.isDone(); }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public void start() { if (path == null) { return; } this.navigation.moveTo(this.path, this.speedModifier); }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public void stop() { path = null; navigation.stop(); }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @Override
    public void actuallysize$jumpstart() {

        // Try to generate path
        for (int i = 0; i < 20; i++) { if (genPath()) { return; } }
    }

    @Override
    public void tick() {
        if (navigation.isDone()) { stop(); }
        if (path == null) { actuallysize$jumpstart(); start(); }
    }
}
