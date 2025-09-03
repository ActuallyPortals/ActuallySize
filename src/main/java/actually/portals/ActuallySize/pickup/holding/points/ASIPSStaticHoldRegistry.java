package actually.portals.ActuallySize.pickup.holding.points;

import gunging.ootilities.GungingOotilitiesMod.exploring.ItemExplorerStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A prime form of the Hold Point Registry meant to be treated as
 * the plugin's static singleton, which actually holds a reference
 * to all hold points that are used in the mod
 * Equipment Slots or any other object really.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIPSStaticHoldRegistry extends ASIPSHoldPointRegistry {

    /**
     * The number of points registered so far, acts as a unique index for this session
     *
     * @since 1.0.0
     */
    private int pointIndex = 0;

    /**
     * Include a brand new registerable hold point to be accessible through {@link #getHoldPoint(Object)}
     *
     * @param index Enum or string or anything to index this hold point by
     * @param point The hold point that you are registering
     *
     * @author Actually Portals
     * @since 1.0.0
     */
    @Override
    public void registerHoldPoint(@Nullable ItemExplorerStatement<?,?> index, @NotNull ASIPSRegisterableHoldPoint point) {

        // Increase point index
        pointIndex = pointIndex + 1;
        point.setOrdinal(pointIndex);

        // Register in dictionaries
        super.registerHoldPoint(index, point);

        // Find that namespace's hold and include it
        HashMap<String, ASIPSRegisterableHoldPoint> inNamespace = byNamespaces.computeIfAbsent(point.getNamespacedKey().getNamespace(), k -> new HashMap<>());
        inNamespace.put(point.getNamespacedKey().getPath(), point);
        byOrdinal.put(point.getOrdinal(), point);
    }

    /**
     * The registered Hold Points but sorted by namespaces
     *
     * @since 1.0.0
     */
    @NotNull HashMap<String, HashMap<String, ASIPSRegisterableHoldPoint>> byNamespaces = new HashMap<>();

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public HashMap<String, HashMap<String, ASIPSRegisterableHoldPoint>> getByNamespaces() { return byNamespaces; }

    /**
     * The registered Hold Points but sorted by ordinal
     *
     * @since 1.0.0
     */
    @NotNull HashMap<Integer, ASIPSRegisterableHoldPoint> byOrdinal = new HashMap<>();

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public HashMap<Integer, ASIPSRegisterableHoldPoint> getByOrdinal() { return byOrdinal; }

    /**
     * @return The list of namespaces of all statements
     *
     * @since 1.0.0
     * @author Gunging
     */
    @NotNull public ArrayList<String> listStatementNamespaces() {
        return new ArrayList<>(byNamespaces.keySet());
    }

    /**
     * @return The list of all statements associated with this namespace
     *
     * @since 1.0.0
     * @author Gunging
     */
    @NotNull public ArrayList<ASIPSRegisterableHoldPoint> listHoldPoints(@NotNull String namespace) {
        HashMap<String, ASIPSRegisterableHoldPoint> statements = byNamespaces.get(namespace);
        if (statements == null) { return new ArrayList<>(); }
        return new ArrayList<>(statements.values());
    }

    @Override
    public void unregisterAllHoldPoints() {
        super.unregisterAllHoldPoints();
        byNamespaces.clear();
        byOrdinal.clear();
    }
}
