package enchantmentcontrol.mixin.vanilla.anvil;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(ContainerRepair.class)
public abstract class ContainerRepairMixin_FreeBookCombo {
    @WrapWithCondition(
            method = "updateRepairOutput",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setRepairCost(I)V")
    )
    private boolean ec_freeBookCombination(
            ItemStack output, int cost,
            @Local(name = "itemstack") ItemStack left,
            @Local(name = "itemstack2") ItemStack right,
            @Local(name = "flag") boolean rightIsBook
    ) {
        // Both enchanted books
        if (!rightIsBook) return true;
        if (left.getItem() != Items.ENCHANTED_BOOK) return true;

        // Both books no repair cost
        if (left.getRepairCost() > 0) return true;
        if (right.getRepairCost() > 0) return true;

        // Only one enchant
        Map<Enchantment, Integer> enchLeft = EnchantmentHelper.getEnchantments(left);
        if(enchLeft.size() != 1) return true;
        Map<Enchantment, Integer> enchRight = EnchantmentHelper.getEnchantments(right);
        if (enchRight.size() != 1) return true;

        // Same enchant, same level
        if(!enchLeft.entrySet().iterator().next().equals(
            enchRight.entrySet().iterator().next())
        )
            return true;

        // Don't change RepairCost
        return false;
    }
}