package enchantmentcontrol.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public class ItemStackMixin_Tooltip {
    @ModifyExpressionValue(
            method = "getTooltip",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getTranslatedName(I)Ljava/lang/String;")
    )
    private String ec_firstEnchantBold(
            String original,
            @Local(name = "nbttaglist")NBTTagList enchTagList,
            @Share("isNotFirst")LocalBooleanRef isNotFirst
    ){
        if(enchTagList.tagCount() <= 1) return original;
        if(isNotFirst.get()) return original;
        isNotFirst.set(true);

        return TextFormatting.UNDERLINE + original;
    }
}
