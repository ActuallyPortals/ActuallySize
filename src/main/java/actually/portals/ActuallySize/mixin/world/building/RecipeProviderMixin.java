package actually.portals.ActuallySize.mixin.world.building;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import actually.portals.ActuallySize.world.ASIWorldSystemManager;
import actually.portals.ActuallySize.world.mixininterfaces.JustDoIt;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(RecipeProvider.class)
public abstract class RecipeProviderMixin implements JustDoIt {

    @Shadow protected abstract void buildRecipes(Consumer<FinishedRecipe> pWriter);

    @Inject(method = "twoByTwoPacker", at = @At("HEAD"))
    private static void voidOn2x2Packer(Consumer<FinishedRecipe> pFinishedRecipeConsumer, RecipeCategory pCategory, ItemLike pPacked, ItemLike pUnpacked, CallbackInfo ci) {

        /*
         * Danger category: Building Blocks
         *
         * Packing these will allow beegs to mass-dupe them to the point it is inconvenient
         */
        if (pCategory == RecipeCategory.BUILDING_BLOCKS && pPacked instanceof Block) {

            // If the packed version is a block
            if (ActuallySizeInteractions.WORLD_SYSTEM.canBeBeegBlock((Block) pPacked)) {

                // Register this beeg item
                ActuallySizeInteractions.WORLD_SYSTEM.RegisterBeegItem(pUnpacked.asItem());
            }
        }
    }

    @Inject(method = "threeByThreePacker(Ljava/util/function/Consumer;Lnet/minecraft/data/recipes/RecipeCategory;Lnet/minecraft/world/level/ItemLike;Lnet/minecraft/world/level/ItemLike;Ljava/lang/String;)V", at = @At("HEAD"))
    private static void voidOn3x3Packer(Consumer<FinishedRecipe> pFinishedRecipeConsumer, RecipeCategory pCategory, ItemLike pPacked, ItemLike pUnpacked, String pCriterionName, CallbackInfo ci) {

        /*
         * Danger category: Building Blocks
         *
         * Packing these will allow beegs to mass-dupe them to the point it is inconvenient
         */
        if (pCategory == RecipeCategory.BUILDING_BLOCKS && pPacked instanceof Block) {

            // If the packed version is a block
            if (ActuallySizeInteractions.WORLD_SYSTEM.canBeBeegBlock((Block) pPacked)) {

                // Register this beeg item
                ActuallySizeInteractions.WORLD_SYSTEM.RegisterBeegItem(pUnpacked.asItem());
            }
        }
    }

    @Override public void actuallysize$doIt() { buildRecipes((finished) -> {}); }
}
