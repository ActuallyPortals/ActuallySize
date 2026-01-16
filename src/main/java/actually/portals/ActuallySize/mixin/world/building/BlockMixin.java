package actually.portals.ActuallySize.mixin.world.building;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.world.mixininterfaces.BeegBreaker;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(Block.class)
public abstract class BlockMixin implements BeegBreaker {


    @WrapMethod(method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;")
    private static List<ItemStack> OnTileDrop(BlockState pState, ServerLevel pLevel, BlockPos pPos, BlockEntity pBlockEntity, Entity pEntity, ItemStack pTool, Operation<List<ItemStack>> original) {

        List<ItemStack> ret = original.call(pState, pLevel, pPos, pBlockEntity, pEntity, pTool);

        // If it is a player, hy-jack drops
        if (pEntity instanceof BeegBreaker) {
            BeegBreaker beeg = (BeegBreaker) pEntity;
            if (beeg.actuallysize$isBeegBreaking()) {
                for (ItemStack item : ret) { beeg.actuallysize$addBeegBreakingDrop(item); }
                ret.clear(); } }

        return ret;
    }

    @WrapOperation(method = "popResource(Lnet/minecraft/world/level/Level;Ljava/util/function/Supplier;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private static boolean OnBeegItemDropped(Level instance, Entity entity, Operation<Boolean> original) {

        if (actuallysize$beegBreaking) {
            if (actuallysize$beeg != null) {

                ItemEntity asItemEntity = (ItemEntity) entity;
                ItemStack item = asItemEntity.getItem();
                boolean beegBlock = ActuallySizeInteractions.WORLD_SYSTEM.canBeBeegBlock(item);

                if (beegBlock) {

                    double beegScale = ASIUtilities.getEntityScale(actuallysize$beeg);
                    double inv = 1 / beegScale;
                    //int ceilScale = OotilityNumbers.ceil(beegScale);
                    //int idealCount = ceilScale * ceilScale * ceilScale;
                    //int smallCount = OotilityNumbers.floor(idealCount * inv);

                    ASIUtilities.setEntityScale(asItemEntity, beegScale);
                    item.setCount(OotilityNumbers.ceil(item.getCount() * inv * inv));

                }
            }
        }

        return original.call(instance, entity);
    }

    @Unique private static boolean actuallysize$beegBreaking;

    @Override
    public boolean actuallysize$isBeegBreaking() { return actuallysize$beegBreaking; }

    @Override
    public void actuallysize$setBeegBreaking(boolean is) { actuallysize$beegBreaking = is; if (!is) { actuallysize$beeg = null; } }

    @Override
    public void actuallysize$addBeegBreakingDrop(@NotNull ItemStack drop) { }

    @Override
    public @NotNull ArrayList<ItemStack> actuallysize$getBeegBreakingDrops() { return new ArrayList<ItemStack>(); }

    @Unique @Nullable private static ServerPlayer actuallysize$beeg;

    @Override
    public void actuallysize$setBeegBreaker(@Nullable ServerPlayer beeg) { actuallysize$beeg = beeg; }

    @Override
    public @Nullable ServerPlayer actuallysize$getBeegBreaker() { return actuallysize$beeg; }
}
