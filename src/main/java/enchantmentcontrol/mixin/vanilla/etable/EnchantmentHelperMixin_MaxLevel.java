package enchantmentcontrol.mixin.vanilla.etable;

import enchantmentcontrol.config.ConfigHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin_MaxLevel {
    @ModifyConstant(
            method = "calcItemStackEnchantability",
            constant = @Constant(intValue = 15)
    )
    private static int ec_modifyMaxETableLvl(int constant){
        return ConfigHandler.etable.maxLvl / 2;
    }
}
