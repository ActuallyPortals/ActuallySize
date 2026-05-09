package actually.portals.ActuallySize.compatibilities.figura;

import gunging.ootilities.GungingOotilitiesMod.ootilityception.OotilityNumbers;
import org.figuramc.figura.utils.ui.UIHelper;

/**
 * Accesses the API of Figura
 *
 * @since 1.0.0
 * @author Actually Portals
 */
public class ASIFiguraCompatibility {

    /**
     * @return If Create Mod is present. Will throw a
     *
     * @since 1.0.0
     * @author Actually Portals
     */
    public static boolean TestIfFiguraPresent() throws NoClassDefFoundError {
        String findCreate = OotilityNumbers.extractAfter(UIHelper.class.getName(), "fig", true);
        return !findCreate.isEmpty();
    }
}
