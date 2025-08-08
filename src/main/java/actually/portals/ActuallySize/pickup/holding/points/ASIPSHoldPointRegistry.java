package actually.portals.ActuallySize.pickup.holding.points;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemExplorerStatement;
import gunging.ootilities.GungingOotilitiesMod.exploring.ItemStackLocation;
import gunging.ootilities.GungingOotilitiesMod.exploring.entities.ISEEquipmentSlotted;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * A list of registered hold points linked to
 * Equipment Slots or any other object really.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIPSHoldPointRegistry {

    /**
     * The static collection of points that have been registered, by the meaning of their original index
     *
     * @since 1.0.0
     */
    @NotNull final HashMap<Object, ASIPSRegisterableHoldPoint> reindexedPoints = new HashMap<>();

    /**
     * The static collection of points that have been registered, by their original index
     *
     * @since 1.0.0
     */
    @NotNull final HashMap<ItemExplorerStatement<?,?>, ASIPSRegisterableHoldPoint> registeredPoints = new HashMap<>();

    /**
     * The static collection of points that have been registered, by their namespaced key
     *
     * @since 1.0.0
     */
    @NotNull final HashMap<ResourceLocation, ASIPSRegisterableHoldPoint> namespacedPoints = new HashMap<>();

    /**
     * @param index Enum or string or in general identifier of the hold point you want
     *
     * @return The hold point, if found, connected with this identifier
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable public ASIPSRegisterableHoldPoint getHoldPoint(@Nullable Object index) {
        if (index == null) { return null; }
        if (index instanceof ResourceLocation) { return getHoldPoint((ResourceLocation) index); }
        return reindexedPoints.get(reindex(index));
    }

    /**
     * @param namespacedKey The resource location / namespaced key of the hold point you seek.
     *
     * @return The hold point, if found, connected with this identifier
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable
    public ASIPSRegisterableHoldPoint getHoldPoint(@Nullable ResourceLocation namespacedKey) {
        if (namespacedKey == null) { return null; }
        return namespacedPoints.get(namespacedKey);
    }

    /**
     * @param namespace The namespace of the hold point you seek, by convention the MOD ID of the mod that added it
     * @param key The name of the hold point you seek.
     *
     * @return The hold point, if found, connected with this identifier
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable
    public ASIPSRegisterableHoldPoint getHoldPoint(@Nullable String namespace, @Nullable String key) {
        if (namespace == null || key == null) { return null; }
        return getHoldPoint(ResourceLocation.fromNamespaceAndPath(namespace, key));
    }

    /**
     * @param key The name of the hold point you seek, using my default namespace {@link actually.portals.ActuallySize.ActuallySizeInteractions#MODID}
     *
     * @return The hold point, if found, connected with this identifier
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Nullable
    public ASIPSRegisterableHoldPoint getHoldPoint(@Nullable String key) {
        if (key == null) { return null; }
        return getHoldPoint(ResourceLocation.fromNamespaceAndPath(ActuallySizeInteractions.MODID, key));
    }

    /**
     * @return The collection of points that have been registered
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public @NotNull HashMap<ItemExplorerStatement<?,?>, ASIPSRegisterableHoldPoint> getRegisteredPoints() { return registeredPoints; }

    /**
     * Include a brand new registerable hold point to be accessible through {@link #getHoldPoint(Object)}
     *
     * @param index Enum or string or anything to index this hold point by
     * @param point The hold point that you are registering
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void registerHoldPoint(@NotNull ItemExplorerStatement<?,?> index, @NotNull ASIPSRegisterableHoldPoint point) {

        // Register in dictionaries
        reindexedPoints.put(reindex(index), point);
        registeredPoints.put(index, point);
        namespacedPoints.put(point.getNamespacedKey(), point);
    }

    /**
     * Some special indices are treated differently, for example
     * ItemStackLocations that index by equipment slot are indexed
     * by the equipment slot they represent and not the ISL.
     *
     * @param index The original index
     *
     * @return The better index to index by
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @NotNull public Object reindex(@NotNull Object index) {

        // Index varous ISL by their statements
        if (index instanceof ItemStackLocation<?>) { index = ((ItemStackLocation<?>) index).getStatement(); }

        // IES Statements are indexed by their contents
        if (index instanceof ItemExplorerStatement<?,?>) {

            // Try by Equipment Slot
            if (index instanceof ISEEquipmentSlotted) {
                return ((ISEEquipmentSlotted) index).getEquipmentSlot();
            }

            // Index by its statement to-string
            return index.toString();
        }

        // No change
        return index;
    }

    /**
     * I believe that if you are calling this you have a good reason to do so.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void unregisterAllHoldPoints() {
        registeredPoints.clear();
        namespacedPoints.clear();
        reindexedPoints.clear();
    }

    public void log() {
        ActuallySizeInteractions.Log("ASI &dPS REG&7 Registry with x" + registeredPoints.size() + " points: ");
        for (Map.Entry<ItemExplorerStatement<?,?>, ASIPSRegisterableHoldPoint> ent : getRegisteredPoints().entrySet()) {
            ActuallySizeInteractions.Log("ASI &dPS REG&7 + &e " + ent.getKey().toString() + " &f = &6 " + ent.getValue().getNamespacedKey());
        }
    }
}
