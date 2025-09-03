package actually.portals.ActuallySize.pickup.mixininterfaces;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Deque;

/**
 * Allows to look into the Pose Deque in Pose Stack class
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public interface VASIPoseStack {

    /**
     * @return The pose stack deque
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull Deque<PoseStack.Pose> actuallysize$getDeque();

    /**
     * @return The mirror deque ASI uses to apply the same operations
     *         applied in model space to world space coordinates (reaL)
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull Deque<PoseStack.Pose> actuallysize$getMirrorDeque();

    /**
     * Resets the mirror deque
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$resetMirrorDeque();

    /**
     * Enables the mirror deque
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$enableMirrorDeque();

    /**
     * Ceases mirroring
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$stopMirroring();

    /**
     * Ceases mirroring
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    boolean actuallysize$isMirroring();

    /**
     * The latest pose of the mirror deque
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    PoseStack.Pose actuallysize$mirrorLast();

    /**
     * @return If this pose stack is associated with a "parent" entity, that entity.
     *         Most often from rendering said entity one way or another.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable ItemEntityDualityHolder actuallysize$getPoseParent();

    /**
     * @param parent An entity this pose stack is associated with
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$setPoseParent(@Nullable ItemEntityDualityHolder parent);

    /**
     * @return If there is a parent, often enough there are children that
     *         care about that parent or are part of the same calculations.
     *         It is handy to identify them once, when the parent is identified.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable ArrayList<EntityDualityCounterpart> actuallysize$getPoseChildren();

    /**
     * @param children Entities that care about this pose stack rendering (real)
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$setPoseChildren(@Nullable ArrayList<EntityDualityCounterpart> children);

    /**
     * @return The renderer currently being used to render the parent
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable EntityRenderer<? extends Entity> actuallysize$getRenderer();

    /**
     * @param rend The renderer currently being used to render the parent
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    void actuallysize$setRenderer(@Nullable EntityRenderer<? extends Entity> rend);
}
