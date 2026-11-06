package enchantmentcontrol.util;

import enchantmentcontrol.config.ConfigHandler;
import net.minecraft.util.text.translation.I18n;

@SuppressWarnings("deprecation")
public class EnchantmentLevelFormat {
    /**
     * Vanilla only has lang entries for enchantment.level.1 to 10, anything else would display as "enchantment.level.11".
     * Uses the lang entry if there is one (so resource packs can still change them), otherwise the plain number.
     */
    public static String format(int lvl) {
        String key = "enchantment.level." + lvl;
        if (!ConfigHandler.various.fixEnchantmentLevelDisplay || I18n.canTranslate(key)) return I18n.translateToLocal(key);
        return String.valueOf(lvl);
    }
}
