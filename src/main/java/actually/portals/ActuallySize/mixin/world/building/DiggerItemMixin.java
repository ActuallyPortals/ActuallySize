package actually.portals.ActuallySize.mixin.world.building;

import actually.portals.ActuallySize.world.mixininterfaces.DitzDestroyer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DiggerItem.class)
public class DiggerItemMixin implements DitzDestroyer {

    @Shadow @Final private TagKey<Block> blocks;

    @Override public boolean actuallysize$canDitzDestroy(@NotNull ItemStack tool, @NotNull BlockState block) { return block.is(blocks); }
}
