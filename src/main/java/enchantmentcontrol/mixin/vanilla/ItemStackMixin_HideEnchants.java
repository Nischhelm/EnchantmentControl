package enchantmentcontrol.mixin.vanilla;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import enchantmentcontrol.util.EnchantmentInfo;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin_HideEnchants {
    // The list.add right after the enchantment name was built
    @WrapWithCondition(
            method = "getTooltip",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getTranslatedName(I)Ljava/lang/String;"))
    )
    private boolean ec_hideEnchantOnItem(List<Object> list, Object line, @Local Enchantment enchantment) {
        return !EnchantmentInfo.isHiddenOnItem(enchantment);
    }
}
