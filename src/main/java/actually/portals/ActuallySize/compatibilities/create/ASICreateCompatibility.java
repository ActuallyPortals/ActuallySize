package actually.portals.ActuallySize.compatibilities.create;

import actually.portals.ActuallySize.ActuallySizeInteractions;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.block.IBE;
import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Accesses the API of Create
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASICreateCompatibility {

    /**
     * @return If Create Mod is present. Will throw a
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static boolean TestIfCreatePresent() throws NoClassDefFoundError {
        String findCreate = OotilityNumbers.extractAfter(Create.class.getName(), "cre", true);
        return !findCreate.isEmpty();
    }

    /**
     * @param block The block being queried
     *
     * @return If this block is a Create functional block
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static boolean IsCreateFunctionalBlock(@NotNull Block block) {
        return block instanceof IBE<?>;
    }
}
