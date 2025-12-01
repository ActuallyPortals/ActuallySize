package actually.portals.ActuallySize.mixin.holding.food;

import actually.portals.ActuallySize.ASIUtilities;
import actually.portals.ActuallySize.ActuallyServerConfig;
import actually.portals.ActuallySize.pickup.item.ASIPSHeldEntityItem;
import actually.portals.ActuallySize.pickup.mixininterfaces.PlayerBound;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(FoodData.class)
public abstract class FoodDataMixin implements PlayerBound {

    @Nullable @Unique Player actuallysize$playerBound;

    @Unique @Nullable ItemStack actuallysize$currentConsumption;

    @WrapMethod(method = "eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V", remap = false)
    public void onMultiConsume(Item pItem, ItemStack pStack, @Nullable LivingEntity entity, Operation<Void> original) {
        actuallysize$currentConsumption = pStack;
        original.call(pItem, pStack, entity);
    }

    @WrapMethod(method = "eat(IF)V")
    public void onConsumption(int pFoodLevelModifier, float pSaturationLevelModifier, Operation<Void> original) {
        int adjustedFood = pFoodLevelModifier;
        float adjustedSat = pSaturationLevelModifier;

        // Nerf food if beeg, buff food if smol
        if (actuallysize$playerBound != null && ActuallyServerConfig.hungryBeegs) {
            float size = (float) ASIUtilities.getEffectiveSize(actuallysize$playerBound);
            if (size != 1) {
                boolean itemKnown = actuallysize$currentConsumption != null;

                float sizeAmp = 1 / size;
                if (size > 1) {

                    // Some exemptions suffer the hunger nerf a LOT less
                    boolean itemExempted = itemKnown && actuallysize$currentConsumption.getItem() instanceof ASIPSHeldEntityItem;
                    if (itemExempted) { sizeAmp = (float) Math.sqrt(sizeAmp);

                    // Beegs get nerfed to the square cube law yay
                    } else { sizeAmp = sizeAmp * sizeAmp; }
                }

                // Calculate buffed or nerfed food nutrition
                float interimFood = adjustedFood * sizeAmp;
                float interimSat = adjustedSat * sizeAmp;

                // Beegs may eat several counts of stack at once if it means it will actually feed them
                int interimCount = 1;
                if (size > 1 && itemKnown) {
                    float totalFood = interimFood;
                    int maxConsumption = OotilityNumbers.ceil(size);
                    int itemCount = actuallysize$currentConsumption.getCount() - 1; // Minus one that is already being eaten

                    // Only need to eat until it is worth one foodie, within the beegs' max consumption, and within the ItemStack capacity
                    while ((totalFood < 1) && (interimCount <= maxConsumption) && (interimCount <= itemCount)) {

                        // Increase the total food and count of items consumed
                        totalFood += interimFood;
                        interimCount += 1;
                    }

                    // Multiply the nutrition of this food
                    interimFood = interimFood * interimCount;
                    interimSat = interimSat * interimCount;

                    // Decrease the stack
                    actuallysize$currentConsumption.setCount(itemCount - interimCount + 2);
                }

                adjustedFood = OotilityNumbers.round(interimFood);
                adjustedSat = interimSat;
                //FOO//ActuallySizeInteractions.Log("ASI &1 FDM-FOO &7 (Clientside? " + actuallysize$playerBound.level().isClientSide + ") Modification &b x" + size + " = " + sizeAmp + " &r (Cx" + interimCount + ")");
            }
        }
        //FOO//ActuallySizeInteractions.Log("ASI &1 FDM-FOO &r Hungered food from &6 N" + pFoodLevelModifier + " S" + pSaturationLevelModifier + " &7 to &e N"+ adjustedFood + " S" + adjustedSat);

        // Run
        original.call(adjustedFood, adjustedSat);
        actuallysize$currentConsumption = null;
    }

    @Override
    public @Nullable Player actuallysize$getBoundPlayer() {
        return actuallysize$playerBound;
    }

    @Override
    public void actuallysize$setBoundPlayer(@Nullable Player player) {
        actuallysize$playerBound = player;
    }
}
