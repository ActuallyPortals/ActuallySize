package actually.portals.ActuallySize.world;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallyServerConfig;
import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.compatibilities.create.ASICreateCompatibility;
import actually.portals.ActuallySize.world.blocks.BeegLightBlock;
import actually.portals.ActuallySize.world.blocks.BeegLightSource;
import actually.portals.ActuallySize.world.blocks.BlockItemRegistry;
import actually.portals.ActuallySize.world.blocks.furniture.ASIBeegFurnishing;
import actually.portals.ActuallySize.world.blocks.furniture.ASIBeegFurnitureRegistryEvent;
import actually.portals.ActuallySize.world.blocks.furniture.ASIBeegTorch;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * The class that handles systems related to interacting
 * with the world as a beeg or a tiny.
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIWorldSystemManager {

    //region Compatibility Checking
    /**
     * The result of checking if Create mod is present upon mod init
     *
     * @since 1.0.0
     */
    boolean createPresent = false;

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public boolean isCreatePresent() { return createPresent; }
    //endregion

    /**
     * Load this system onto the mod during mod loading initialization
     *
     * @param context Mod Loading context
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void OnModLoadInitialize(FMLJavaModLoadingContext context) {

        // It will either generate the error or succeed.
        try {
            createPresent = ASICreateCompatibility.TestIfCreatePresent();
        } catch (Error ignored) {
            createPresent = false;
        }
    }

    /**
     * @since 1.0.0
     * @author Actually Portals
     */
    public void onCommonSetup() {
        registerBeegFurnishings();
        standardBeegItems();
    }

    //region Beeg Building Furniture
    /**
     * The block state property for Simple Beeg Blocks
     *
     * @since 1.0.0
     */
    public static final IntegerProperty BEEG_SCALE = IntegerProperty.create("asi_scale", 1, 16);

    /**
     * Beeg blocks that sequentially stretch each other must know how much they have stretched
     *
     * @since 1.0.0
     */
    public static final IntegerProperty BEEG_SPREAD = IntegerProperty.create("asi_spread", 1, 16);

    /**
     * Beeg blocks that sequentially stretch each other must know how much they have stretched
     *
     * @since 1.0.0
     */
    public static final IntegerProperty BEEG_SPREADING = IntegerProperty.create("asi_spreading", 0, 2);

    /**
     * A block that emits hella light
     *
     * @since 1.0.0
     */
    public static final BlockItemRegistry BEEG_TORCH_BLOCK = new BlockItemRegistry("beeg_torch_block", () ->
            new BeegLightSource(
                    BlockBehaviour.Properties.copy(Blocks.OAK_LOG)
                    .mapColor(MapColor.COLOR_YELLOW)
                    .instabreak(), 15));

    /**
     * You cannot simply give a light block a tremendous light amount,
     * as such, Beeg Torches actually place a ton of light blocks
     * around them. These are the beeg light blocks that fill the
     * space when a torch is placed.
     *
     * @since 1.0.0
     */
    public static final BlockItemRegistry BEEG_LIGHT_BLOCK = new BlockItemRegistry("beeg_light_block", () ->
            new BeegLightBlock(BlockBehaviour.Properties.copy(Blocks.LIGHT).instabreak().air().pushReaction(PushReaction.DESTROY)));

    /**
     * A block that represents wood in beeg furniture, will
     * be replaced by OAK LOG or whatever first log you
     * have in your inventory
     *
     * @since 1.0.0
     */
    public static final BlockItemRegistry BEEG_LOG_BLOCK = new BlockItemRegistry("beeg_log_block", () ->
            new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG)));

    /**
     * A block that represents wood in beeg furniture, will
     * be replaced by OAK WOOD or whatever first wood you
     * have in your inventory
     *
     * @since 1.0.0
     */
    public static final BlockItemRegistry BEEG_WOOD_BLOCK = new BlockItemRegistry("beeg_wood_block", () ->
            new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)));

    /**
     * A block that represents wood in beeg furniture, will
     * be replaced by OAK PLANKS or whatever first planks you
     * have in your inventory
     *
     * @since 1.0.0
     */
    public static final BlockItemRegistry BEEG_PLANKS_BLOCK = new BlockItemRegistry("beeg_planks_block", () ->
            new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

    /**
     * A block that represents wood in beeg furniture, will
     * be replaced by RED WOOL or whatever first wool color you
     * have in your inventory
     *
     * @since 1.0.0
     */
    public static final BlockItemRegistry BEEG_WOOL_BLOCK = new BlockItemRegistry("beeg_wool_block", () ->
            new Block(BlockBehaviour.Properties.copy(Blocks.RED_WOOL)));

    /**
     * The Beeg Furniture that replaces torches
     *
     * @since 1.0.0
     */
    public static final ASIBeegFurnishing BEEG_TORCH = new ASIBeegTorch((StandingAndWallBlockItem) Items.TORCH, "torch", "wall_torch").withRequiresEmptyArea(false);

    /**
     * Called once to register the Beeg Furniture that ASI provides by default
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public void registerBeegFurnishings() {

        // Create event
        ASIBeegFurnitureRegistryEvent event = new ASIBeegFurnitureRegistryEvent();

        // Fill default values
        event.put(Items.TORCH, BEEG_TORCH);

        // Run event and accept
        MinecraftForge.EVENT_BUS.post(event);
        furnishingsRegistry.putAll(event.getFurnishingsRegistry());
    }

    /**
     * @return Returns the Beeg Furniture associated with this item
     *
     * @since 1.0.0
     */
    @Nullable public ASIBeegFurnishing isBeegFurniture(@NotNull Item item) {
        ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(item);
        return furnishingsRegistry.get(itemKey);
    }

    /**
     * The Beeg Furnishings that will be registered
     *
     * @since 1.0.0
     */
    @NotNull final HashMap<ResourceLocation, ASIBeegFurnishing> furnishingsRegistry = new HashMap<>();
    //endregion

    //region Beeg Building Item Drop Rate
    /**
     * Items that participate in beeg building system
     *
     * @since 1.0.0
     */
    @NotNull final HashMap<ResourceLocation, Boolean> beegBuildingItems = new HashMap<>();

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
    public void standardBeegItems() {
        RegisterBeegItem(Items.CLAY_BALL);
        RegisterBeegItem(Items.GLOWSTONE_DUST);

        /*
        RegisterBeegItem(Items.RAW_COPPER);
        RegisterBeegItem(Items.COPPER_INGOT);
        RegisterBeegItem(Items.RAW_IRON);
        RegisterBeegItem(Items.IRON_INGOT);
        RegisterBeegItem(Items.RAW_GOLD);
        RegisterBeegItem(Items.GOLD_INGOT);
        RegisterBeegItem(Items.COAL);
        RegisterBeegItem(Items.DIAMOND);
        RegisterBeegItem(Items.QUARTZ);
        RegisterBeegItem(Items.REDSTONE);
        RegisterBeegItem(Items.LAPIS_LAZULI);
         */
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
    public void RegisterBeegItem(@NotNull Item item) {

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
    public boolean canBeBeegBlock(@NotNull Block block) {

        // When disabled, no blocks are beeg blocks
        if (!ActuallyServerConfig.beegBuilding) { return false; }

        // Check collision shape to be a full block
        BlockState state = block.defaultBlockState();

        // Read shape potentially generating a null pointer exception
        VoxelShape shape = null;
        try { shape = block.getShape(state, null, null, CollisionContext.empty()); } catch (NullPointerException ignored) { }
        if (shape == null) { return false; }
        if (isCreatePresent()) { if (ASICreateCompatibility.IsCreateFunctionalBlock(block)) { return false; } }

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
    public boolean canBeBeegBlock(@NotNull ItemStack item) {

        // When disabled, no blocks are beeg blocks
        if (!ActuallyServerConfig.beegBuilding) { return false; }

        // Blocks are treated differently from normal items
        if (!(item.getItem() instanceof BlockItem)) {

            // Not a block? Well how about a packable item
            ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(item.getItem());
            return beegBuildingItems.containsKey(itemKey); }

        // Check collision shape to be a full block
        BlockItem block = (BlockItem) item.getItem();
        return canBeBeegBlock(block.getBlock());
    }
    //endregion

    //region Fear
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
    //endregion

    //region Damage Taken
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
        if (type.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) { return false; }
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
    //endregion
}
