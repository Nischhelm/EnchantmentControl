package enchantmentcontrol.mixin.vanilla.anvil;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(ContainerRepair.class)
public abstract class ContainerRepairMixin_NoExpensive {
    @ModifyExpressionValue(
            method = "updateRepairOutput",
            at = @At(value = "CONSTANT", args = "intValue=40", ordinal = 0)
    )
    private int ec_updateRepairOutput_re_disallowMultiInput(int original, @Local(name = "itemstack1") LocalRef<ItemStack> outputStack) {
        //Ususally is being taken care of at the other two positions of intValue40 but those get disabled
        outputStack.set(ItemStack.EMPTY);
        return original;
    }

    @ModifyExpressionValue(
            method = "updateRepairOutput",
            at = @At(value = "CONSTANT", args = "intValue=40"),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;isBookEnchantable(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"))
    )
    private int ec_updateRepairOutput_removeCap(int original) {
        return Integer.MAX_VALUE;
    }
}
