package enchantmentcontrol.mixin.modded.enchcontrol;

import austeretony.enchcontrol.common.config.EnumConfigSettings;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Disables Enchantments Control's level caps (enchanting table, anvil, librarians, loot, creative tab).
 * Most of its patches using them are blocked anyway (see EnchantmentControlPlugin), only its creative tab hook still runs.
 */
@Mixin(value = EnumConfigSettings.class)
public abstract class EnumConfigSettingsMixin {
    @ModifyReturnValue(method = "getIntValue", at = @At("RETURN"), remap = false)
    private int ec_disableLevelCaps(int original) {
        return ((Enum<?>) (Object) this).name().endsWith("_LEVEL_CAP") ? Integer.MAX_VALUE : original;
    }
}
