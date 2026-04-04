package enchantmentcontrol.mixin.vanilla.etable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ContainerEnchantment.class)
public abstract class ContainerEnchantmentMixin_ReEnchant {
    @ModifyExpressionValue(
            method = "onCraftMatrixChanged",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemEnchantable()Z")
    )
    private boolean ec_allowEnchanted(boolean original, @Local ItemStack stack) {
        return original || (stack.getItem().isEnchantable(stack) && stack.isItemEnchanted()); //counters stack.isItemEnchantable to allow isEnchanted
    }
}
