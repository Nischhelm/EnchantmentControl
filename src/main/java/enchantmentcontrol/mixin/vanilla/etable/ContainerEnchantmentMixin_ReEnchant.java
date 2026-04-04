package enchantmentcontrol.mixin.vanilla.etable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
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
            method = "onCraftMatrixChanged",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/ContainerEnchantment;getEnchantmentList(Lnet/minecraft/item/ItemStack;II)Ljava/util/List;")
    )
    private List<EnchantmentData> ec_disallowIllegallReEnchant(List<EnchantmentData> rolledEnchants, @Local ItemStack stack) {
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
        //TODO: why does this not work?
        return rolledEnchants.stream().filter(enchD -> !ec$isIllegallReEnchant(enchD.enchantment, map)).collect(Collectors.toList());
    }

    @WrapWithCondition(
            method = "enchantItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemEnchantedBook;addEnchantment(Lnet/minecraft/item/ItemStack;Lnet/minecraft/enchantment/EnchantmentData;)V")
    )
    private boolean ec_disallowIllegallReEnchant(ItemStack stack, EnchantmentData enchD, @Share("origEnchs") LocalRef<Map<Enchantment, Integer>> origEnchs) {
        if(origEnchs.get() == null) origEnchs.set(EnchantmentHelper.getEnchantments(stack));

        return ec$isIllegallReEnchant(enchD.enchantment, origEnchs.get());
    }

    @WrapWithCondition(
            method = "enchantItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;addEnchantment(Lnet/minecraft/enchantment/Enchantment;I)V")
    )
    private boolean ec_disallowIllegalReEnchant(ItemStack stack, Enchantment ench, int level, @Share("origEnchs") LocalRef<Map<Enchantment, Integer>> origEnchs) {
        if(origEnchs.get() == null) origEnchs.set(EnchantmentHelper.getEnchantments(stack));

        return ec$isIllegallReEnchant(ench, origEnchs.get());
    }

    @Unique
    private static boolean ec$isIllegallReEnchant(Enchantment ench, Map<Enchantment, Integer> origEnchs) {
        if(origEnchs.containsKey(ench)) return false; //already in
        if(origEnchs.keySet().stream().anyMatch(origE -> !origE.isCompatibleWith(ench))) return false; //incompat with one of previous

        return true;
    }
}
