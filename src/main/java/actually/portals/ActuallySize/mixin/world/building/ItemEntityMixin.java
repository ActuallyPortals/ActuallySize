package actually.portals.ActuallySize.mixin.world.building;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.world.mixininterfaces.BeegPicker;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements TraceableEntity {

    public ItemEntityMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow public abstract ItemStack getItem();

    @Inject(method = "playerTouch", at = @At("RETURN"))
    public void OnItemPickupByPlayer(Player pEntity, CallbackInfo ci) {
        if (pEntity.level().isClientSide) { return; }

        // If the item was not picked up fully
        ItemStack myItem = getItem();
        int leftovers = myItem.getCount();
        if (leftovers < 1) { return; }

        BeegPicker myPicker = (BeegPicker) (Object) myItem;
        int trueItemCount = myPicker.actuallysize$getOriginalCount();
        int sizedCount = myPicker.actuallysize$getSizedCount();
        if (sizedCount <= 0) { return; }
        if (trueItemCount == sizedCount) { return; }

        double returner = (double) trueItemCount / (double) sizedCount;
        double returned = leftovers * returner;
        int remainder = OotilityNumbers.floor(leftovers * returner);
        returned -= remainder;
        if (returned > 0 && OotilityNumbers.rollSuccess(returned)) { remainder++; }

        myItem.setCount(trueItemCount);
        myItem.setCount(remainder);
    }
}
