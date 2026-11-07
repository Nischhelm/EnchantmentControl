package enchantmentcontrol.mixin.vanilla.etable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import enchantmentcontrol.util.ReEnchantUtil;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

/**
 * Only applied if Expanded Events is NOT installed.
 * With Expanded Events, this is done in GenerateEnchantmentListEvent instead.
 */
@Mixin(ContainerEnchantment.class)
public abstract class ContainerEnchantmentMixin_ReEnchantRoll {
    @ModifyExpressionValue(
            method = {"onCraftMatrixChanged", "enchantItem"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/ContainerEnchantment;getEnchantmentList(Lnet/minecraft/item/ItemStack;II)Ljava/util/List;")
    )
    private List<EnchantmentData> ec_disallowIllegalReEnchant(List<EnchantmentData> rolledEnchants, @Local(name="itemstack") ItemStack stack) {
        return ReEnchantUtil.reenchantIfPossible(rolledEnchants, stack, true);
    }
}
