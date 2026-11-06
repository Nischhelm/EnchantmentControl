package enchantmentcontrol.mixin.vanilla.anvil;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import enchantmentcontrol.util.UniversalRepairUtil;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ContainerRepair.class)
public abstract class ContainerRepairMixin_UniversalRepair {

    @WrapOperation(
            method = "updateRepairOutput",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getIsRepairable(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z")
    )
    private boolean ec_updateRepairOutput_universalRepairItem(
            Item item, ItemStack toRepair, ItemStack repairStack,
            Operation<Boolean> original,
            @Share("universalRepair") LocalBooleanRef isUniversalRepair
    ) {
        if (original.call(item, toRepair, repairStack)) return true; //normal repair material, keep vanilla amounts
        isUniversalRepair.set(UniversalRepairUtil.canRepair(toRepair, repairStack));
        return isUniversalRepair.get();
    }

    @Definition(id = "getMaxDamage", method = "Lnet/minecraft/item/ItemStack;getMaxDamage()I")
    @Expression("?.getMaxDamage() / 4")
    @ModifyExpressionValue(method = "updateRepairOutput", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int ec_updateRepairOutput_universalRepairAmount(
            int original,
            @Local(name = "itemstack1") ItemStack stackLeftCopy,
            @Share("universalRepair") LocalBooleanRef isUniversalRepair
    ) {
        if (isUniversalRepair.get()) return UniversalRepairUtil.getRepairAmount(stackLeftCopy.getMaxDamage());
        return original;
    }
}
