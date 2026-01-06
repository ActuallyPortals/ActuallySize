package actually.portals.ActuallySize.world.blocks;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * A registry for a Block Item
 *
 * @author Actually Portals
 * @since 1.0.0
 */
public class BlockItemRegistry {

    /**
     * The Block registered
     *
     * @since 1.0.0
     */
    public RegistryObject<Block> getAsBlock() { return asBlock; }

    /**
     * The Item Registered
     *
     * @since 1.0.0
     */
    public RegistryObject<Item> getAsItem() { return asItem; }

    /**
     * The Block being registered
     *
     * @since 1.0.0
     */
    final RegistryObject<Block> asBlock;

    /**
     * The Item being Registered
     *
     * @since 1.0.0
     */
    final RegistryObject<Item> asItem;

    /**
     * @author Actually Portals
     * @since 1.0.0
     */
    public BlockItemRegistry(@NotNull String name, @NotNull Supplier<Block> block) {
        this.asBlock = registerBlock(name, block);
        this.asItem = registerBlockItem(name, asBlock);
    }

    /**
     * Apparently registers a block item
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    private static <T extends Block> RegistryObject<T> registerBlock(@NotNull String name, Supplier<T> block) {
        return ActuallySizeInteractions.BLOCK_REGISTRY.register(name, block);
    }

    /**
     * Apparently registers a block item
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    private static <T extends Block> RegistryObject<Item> registerBlockItem(@NotNull String name, RegistryObject<T> block) {
        return ActuallySizeInteractions.ITEM_REGISTRY.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}
