package enchantmentcontrol.mixin.vanilla.anvil;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.inventory.ContainerRepair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ContainerRepair.class)
public abstract class ContainerRepairMixin_NoExpensive {
    @ModifyExpressionValue(
            method = "updateRepairOutput",
            at = @At(value = "CONSTANT", args = "intValue=40")
    )
    private int ec_updateRepairOutput_removeCap(int original) {
        return Integer.MAX_VALUE;
    }
}
