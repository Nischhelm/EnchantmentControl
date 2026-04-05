package enchantmentcontrol.mixin.vanilla.etable;

import com.llamalad7.mixinextras.sugar.Local;
import enchantmentcontrol.util.ReEnchantUtil;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ContainerRepair.class)
public abstract class ContainerRepairMixin_ReEnchant {
    @Inject(
            method = "updateRepairOutput",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setRepairCost(I)V")
    )
    private void ec_keepEnchantCount(CallbackInfo ci, @Local(name = "itemstack1") ItemStack stack1, @Local(name = "itemstack2") ItemStack stack2) {
        ReEnchantUtil.setEnchantCount(stack1, Math.max(ReEnchantUtil.getEnchantCount(stack1), ReEnchantUtil.getEnchantCount(stack2)));
    }
}
