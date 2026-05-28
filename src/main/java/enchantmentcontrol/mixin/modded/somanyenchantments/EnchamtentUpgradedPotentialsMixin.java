package enchantmentcontrol.mixin.modded.somanyenchantments;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.shultrea.rin.enchantments.EnchantmentUpgradedPotentials;
import enchantmentcontrol.util.AnvilCostUtil;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantmentUpgradedPotentials.class)
public abstract class EnchamtentUpgradedPotentialsMixin {
    @WrapOperation(
            method = "onAnvilUpdateEvent",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setRepairCost(I)V")
    )
    private void ec_resetAnvilCount(ItemStack stack, int newCost, Operation<Void> original){
        AnvilCostUtil.setAnvilCount(stack, AnvilCostUtil.guessAnvilCount(newCost));
        original.call(stack, newCost);
    }
}
