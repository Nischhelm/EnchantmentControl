package enchantmentcontrol.mixin.vanilla;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import enchantmentcontrol.util.EnchantmentInfo;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemEnchantedBook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ItemEnchantedBook.class)
public abstract class ItemEnchantedBookMixin_HideEnchants {
    @WrapWithCondition(
            method = "addInformation",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z")
    )
    private boolean ec_hideEnchantOnBook(List<Object> tooltip, Object line, @Local Enchantment enchantment) {
        return !EnchantmentInfo.isHiddenOnBook(enchantment);
    }
}
