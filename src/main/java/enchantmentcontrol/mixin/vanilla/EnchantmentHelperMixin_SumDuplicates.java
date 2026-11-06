package enchantmentcontrol.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import enchantmentcontrol.util.DuplicateEnchantsUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.nbt.NBTTagList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin_SumDuplicates {
    // Vanilla behavior with duplicate enchantments on an item is inconsistent:
    // getEnchantmentLevel takes the first one, getEnchantments the last one, applyEnchantmentModifier applies each separately
    // This makes all of them see a single entry with the summed up level instead

    @ModifyExpressionValue(
            method = {"getEnchantmentLevel", "applyEnchantmentModifier", "getEnchantments"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getEnchantmentTagList()Lnet/minecraft/nbt/NBTTagList;")
    )
    private static NBTTagList ec_sumDuplicates(NBTTagList original) {
        return DuplicateEnchantsUtil.mergeDuplicates(original);
    }

    @ModifyExpressionValue(
            method = "getEnchantments",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemEnchantedBook;getEnchantments(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/nbt/NBTTagList;")
    )
    private static NBTTagList ec_sumDuplicatesBook(NBTTagList original) {
        return DuplicateEnchantsUtil.mergeDuplicates(original);
    }
}
