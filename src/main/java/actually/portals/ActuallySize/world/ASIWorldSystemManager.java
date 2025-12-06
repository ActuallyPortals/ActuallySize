package actually.portals.ActuallySize.world;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallyServerConfig;
import actually.portals.ActuallySize.ActuallySizeInteractions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * The class that handles systems related to interacting
 * with the world as a beeg or a tiny.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIWorldSystemManager {

    /**
     * Items that participate in beeg building system
     *
     * @since 1.0.0
     */
    @NotNull static final HashMap<ResourceLocation, Boolean> beegBuildingItems = new HashMap<>();

    /**
     * Provisional initialization of beeg building items
     * while I figure out a way to identify packing
     * recipes.
     *
     * @deprecated Provisional while a better method comes in place
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    @Deprecated
    public static void StandardBeegItems() {
        RegisterBeegItem(Items.CLAY_BALL);
        RegisterBeegItem(Items.GLOWSTONE_DUST);

        RegisterBeegItem(Items.RAW_COPPER);
        RegisterBeegItem(Items.RAW_IRON);
        RegisterBeegItem(Items.RAW_GOLD);
        RegisterBeegItem(Items.COAL);
        RegisterBeegItem(Items.DIAMOND);
        RegisterBeegItem(Items.QUARTZ);
        RegisterBeegItem(Items.REDSTONE);
        RegisterBeegItem(Items.GOLD_NUGGET);
    }

    /**
     * Registers an item that can be packed into building blocks,
     * so that picking these items up as a beeg will be nerfed
     * so that their packed version is a little harder to mass-dupe...
     * and also kinda balances beeg mining a bit since most ores
     * happen to fall in this category
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static void RegisterBeegItem(@NotNull Item item) {

        // Obtain this item's resource key
        ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(item);
        if (beegBuildingItems.containsKey(itemKey)) { return; }

        // Include it
        beegBuildingItems.put(itemKey, false);
    }

    /**
     * @return If this is a block that will be placed in
     *         the beeg grid when beeg building is enabled.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static boolean CanBeBeegBlock(@NotNull Block block) {

        // When disabled, no blocks are beeg blocks
        if (!ActuallyServerConfig.beegBuilding) { return false; }

        // Check collision shape to be a full block
        BlockState state = block.defaultBlockState();

        // Read shape potentially generating a null pointer exception
        VoxelShape shape = null;
        try {
            shape = block.getShape(state, null, null, CollisionContext.empty());
        } catch (NullPointerException ignored) { }
        if (shape == null) { return false; }

        // It must be full-size
        return Block.isShapeFullBlock(shape);
    }

    /**
     * @return If this is a block that will be placed in
     *         the beeg grid when beeg building is enabled.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static boolean CanBeBeegBlock(@NotNull ItemStack item) {

        // When disabled, no blocks are beeg blocks
        if (!ActuallyServerConfig.beegBuilding) { return false; }

        // Blocks are treated differently from normal items
        if (!(item.getItem() instanceof BlockItem)) {

            // Not a block? Well how about a packable item
            ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(item.getItem());
            return beegBuildingItems.containsKey(itemKey); }

        // Check collision shape to be a full block
        BlockItem block = (BlockItem) item.getItem();
        return CanBeBeegBlock(block.getBlock());
    }

    /**
     * @param beeg The beeg
     * @param tiny The tiny
     * @param relative How much bigger is the beeg to the tiny, so 4X bigger is 4.0
     *
     * @return If the specified entity may panic when encountering a beeg
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static boolean CanPanic(@NotNull LivingEntity beeg, @NotNull LivingEntity tiny, double relative) {

        // Needs a higher panic threshold
        double panicThreshold = ActuallyServerConfig.fearThreshold * 1.5;
        if (panicThreshold > relative) { return false; }

        // Zombies do not panic
        boolean isSkeleton = tiny.getType().is(EntityTypeTags.SKELETONS);
        boolean isUndead = tiny.getMobType() == MobType.UNDEAD;
        if (isUndead && !isSkeleton) { return false; }

        // Golems do not panic
        if (tiny instanceof AbstractGolem) { return false; }

        // Creepers do not panic
        if (tiny instanceof Creeper) { return false; }

        // Everything else panics when the beeg is near
        double pos = beeg.position().distanceToSqr(tiny.position());
        double eye = beeg.position().distanceToSqr(tiny.position());
        double least = Math.min(pos, eye);
        return least < (relative * relative * 4);
    }

    /**
     * @param beeg The beeg
     * @param tiny The tiny
     * @param relative How much bigger is the beeg to the tiny, so 4X bigger is 4.0
     *
     * @return If the specified entity may fear when encountering a beeg
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static boolean CanFear(@NotNull LivingEntity beeg, @NotNull LivingEntity tiny, double relative) {

        // Must meet fear threshold
        if (ActuallyServerConfig.fearThreshold > relative) { return false; }

        // Golems do not fear
        if (tiny instanceof AbstractGolem) { return false; }

        // Everything else fears when a beeg is near
        double pos = beeg.position().distanceToSqr(tiny.position());
        double eye = beeg.position().distanceToSqr(tiny.position());
        double least = Math.min(pos, eye);
        return least < (relative * relative * 49);
    }

    /**
     * @param world The world where damage takes place
     * @param type The type of damage
     *
     * @return If this damage type is adjusted by ASI
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static boolean IsAdjustableDamage(@NotNull Level world, @NotNull DamageSource type) {

        // Fall is not affected by ASI
        if (type == world.damageSources().fall()) { return false; }
        if (type == world.damageSources().cramming()) { return false; }
        if (type == world.damageSources().drown()) { return false; }
        if (type == world.damageSources().starve()) { return false; }
        if (type == world.damageSources().fellOutOfWorld()) { return false; }
        if (type == world.damageSources().wither()) { return false; }
        if (type == world.damageSources().outOfBorder()) { return false; }
        if (type == world.damageSources().genericKill()) { return false; }

        // Everything else is
        return true;
    }

    /**
     * Some types of damage should not be made more powerful
     * against tinies, but it is fine if beegs resist them more.
     * This doesn't make a lot of "sense" sense but if they were
     * boosted it would just make tiny play painful unnecessarily*
     *
     * @param world The world where damage takes place
     * @param type The type of damage
     *
     * @return If this damage type is adjusted by ASI for beegs
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static boolean IsAdjustedForTinies(@NotNull Level world, @NotNull DamageSource type) {

        // Walls make no sense to make more damage to tinies
        if (type == world.damageSources().inWall()) { return false; }
        if (type == world.damageSources().dragonBreath()) { return false; }
        if (type == world.damageSources().dryOut()) { return false; }
        if (type == world.damageSources().cactus()) { return false; }
        if (type == world.damageSources().hotFloor()) { return false; }
        if (type == world.damageSources().flyIntoWall()) { return false; }
        if (type == world.damageSources().sweetBerryBush()) { return false; }

        // Everything else is
        return true;
    }
    /**
     * Some types of damage should not be made more powerful
     * against tinies, but it is fine if beegs resist them more.
     * This doesn't make a lot of "sense" sense but if they were
     * boosted it would just make tiny play painful unnecessarily*
     *
     * @param world The world where damage takes place
     * @param type The type of damage
     *
     * @return If this damage type is adjusted by ASI for beegs
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static boolean IsBypassedByTinies(@NotNull Level world, @NotNull DamageSource type) {

        // Walls make no sense to make more damage to tinies
        if (type == world.damageSources().cactus()) { return true; }
        if (type == world.damageSources().sweetBerryBush()) { return true; }

        // Everything else is
        return false;
    }

    /**
     * Based on the options in the config, this method will adjust the damage
     * dealt between one or two entities of different sizes. In general, beegs
     * take less damage and tinies take more damage.
     *
     * @param originalDamage The amount of damage
     * @param victim The receiver of damage
     * @param attack The attack information
     *
     * @return Damage, adjusted to account for matters of size.
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static double ASICombatAdjust(double originalDamage, @NotNull LivingEntity victim, @NotNull DamageSource attack) {
        if (!IsAdjustableDamage(victim.level(), attack)) { return originalDamage; }
        if (!ActuallyServerConfig.strongBeegs && !ActuallyServerConfig.tankyBeegs && !ActuallyServerConfig.delicateTinies) { return originalDamage; }

        /*
         * Damage calculations use scale, not effective size. This
         * is because using effective size would make larger entities
         * that already deal more damage deal even more damage lol.
         */
        double mySize = ASIUtilities.getEntityScale(victim);
        double buffingLimit = ActuallyServerConfig.strongestBeeg;
        //ATT//ActuallySizeInteractions.Log("ASI &2 WSM &7 [" + victim.getScoreboardName() + "] Adjusting hurt &3 " + originalDamage + " &r up to &b " + buffingLimit + " &f at &e x" + mySize);

        /*
         * Is there an aggressive entity? Then we must bother
         * with their damage being amplified if they are bigger.
         */
        double aggressorScale = 0;
        double aggressorAmplificationFactor = 1;
        boolean aggressorIsCrouching;
        if (attack.getDirectEntity() != null) {
            aggressorScale = ASIUtilities.getEntityScale(attack.getDirectEntity());
            aggressorIsCrouching = (attack.getDirectEntity() instanceof Player) && attack.getDirectEntity().isCrouching();
            //ATT//ActuallySizeInteractions.Log("ASI &2 WSM &7 [" + attack.getDirectEntity().getScoreboardName() + "] Aggressor (" + aggressorIsCrouching + ") at &e x" + aggressorScale);

            if (aggressorScale > 0) {

                // Adjust damage based on the relative scale from aggressor to me
                aggressorAmplificationFactor = ASIUtilities.beegBalanceResist(mySize / aggressorScale, buffingLimit, 0);

                // If the beeg is crouching, and bigger than us, there is no amplification. Tinies remain reduced tho.
                if (aggressorIsCrouching && aggressorScale > mySize) { aggressorAmplificationFactor = 1; }
            }
            //ATT//ActuallySizeInteractions.Log("ASI &2 WSM &7 Aggressor Factor: &6 " + aggressorAmplificationFactor + " &r for a total &e " + (originalDamage * aggressorAmplificationFactor));

            // Combat takes precedence to the other stuff below
            return originalDamage * aggressorAmplificationFactor;
        }

        /*
         * Size matters
         */
        double sizeAmplificationFactor = 1;

        // When beeg
        if (mySize > 1 && ActuallyServerConfig.tankyBeegs) {
            //ATT//ActuallySizeInteractions.Log("ASI &2 WSM &7 Tanky beeg");

            // Reduce damage from all sources
            sizeAmplificationFactor = ASIUtilities.beegBalanceResist(mySize, buffingLimit, 0);

        // When smol
        } else if (mySize < 1 && ActuallyServerConfig.delicateTinies) {
            //ATT//ActuallySizeInteractions.Log("ASI &2 WSM &7 Delicate smol");
            if (mySize < 0.25 && IsBypassedByTinies(victim.level(), attack)) { return 0; }
            if (!IsAdjustedForTinies(victim.level(), attack)) { return originalDamage; }

            // Increase damage from all sources
            sizeAmplificationFactor = ASIUtilities.beegBalanceResist(mySize, buffingLimit, 0);
        }
        //ATT//ActuallySizeInteractions.Log("ASI &2 WSM &7 Size Factor: &6 " + sizeAmplificationFactor + " &r for a total &e " + (originalDamage * sizeAmplificationFactor));

        // Adjust effect
        return originalDamage * sizeAmplificationFactor;
    }
}
