package enchantmentcontrol.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import enchantmentcontrol.util.EnchantmentLevelFormat;
import net.minecraft.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin_LevelDisplay {
    // ordinal 0 is the enchantment name, ordinal 1 the level
    @WrapOperation(
            method = "getTranslatedName",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/text/translation/I18n;translateToLocal(Ljava/lang/String;)Ljava/lang/String;", ordinal = 1)
    )
    private String ec_getTranslatedName_fixLevelDisplay(String key, Operation<String> original, int level) {
        return EnchantmentLevelFormat.format(level);
    }
}
