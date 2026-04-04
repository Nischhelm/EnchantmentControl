package enchantmentcontrol.mixin.vanilla.etable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mixin(ContainerEnchantment.class)
public abstract class ContainerEnchantmentMixin_ReEnchant {
    @ModifyExpressionValue(
            method = "onCraftMatrixChanged",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemEnchantable()Z")
    )
    private boolean ec_allowReEnchanting(boolean original, @Local ItemStack stack) {
        return original || (stack.getItem().isEnchantable(stack) && stack.isItemEnchanted()); //counters stack.isItemEnchantable to allow isEnchanted
    }

    @ModifyExpressionValue(
            method = {"onCraftMatrixChanged", "enchantItem"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/ContainerEnchantment;getEnchantmentList(Lnet/minecraft/item/ItemStack;II)Ljava/util/List;")
    )
    private List<EnchantmentData> ec_disallowIllegallReEnchant(List<EnchantmentData> rolledEnchants, @Local(name="itemstack") ItemStack stack) {
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
        return rolledEnchants.stream().filter(enchD -> ec$isLegallReEnchant(enchD.enchantment, map)).collect(Collectors.toList());
    }

    @Unique
    private static boolean ec$isLegallReEnchant(Enchantment ench, Map<Enchantment, Integer> origEnchs) {
        if(origEnchs.containsKey(ench)) return false; //already in
        if(origEnchs.keySet().stream().anyMatch(origE -> !origE.isCompatibleWith(ench))) return false; //incompat with one of previous

        return true;
    }
}
