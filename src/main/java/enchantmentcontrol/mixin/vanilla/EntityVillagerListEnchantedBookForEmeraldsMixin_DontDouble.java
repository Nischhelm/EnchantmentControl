package enchantmentcontrol.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import enchantmentcontrol.util.EnchantmentInfo;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.passive.EntityVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityVillager.ListEnchantedBookForEmeralds.class)
public abstract class EntityVillagerListEnchantedBookForEmeraldsMixin_DontDouble {

    @ModifyExpressionValue(
            method = "addMerchantRecipe",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;isTreasureEnchantment()Z")
    )
    private boolean soManyEnchantments_vanillaEntityVillagerListEnchantedBookForEmeralds_addMerchantRecipe(boolean original, @Local Enchantment enchantment) {
        EnchantmentInfo info = EnchantmentInfo.get(enchantment);
        if(info != null && info.overwritesDoublePrice) return info.doublePrice; //if its set, it uses the set value, otherwise it uses default isTreasure
        return original;
    }
}