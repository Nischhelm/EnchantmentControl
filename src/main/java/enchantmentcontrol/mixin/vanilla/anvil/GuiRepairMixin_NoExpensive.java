package enchantmentcontrol.mixin.vanilla.anvil;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.GuiRepair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiRepair.class)
public abstract class GuiRepairMixin_NoExpensive {
    @ModifyExpressionValue(
            method = "drawGuiContainerForegroundLayer",
            at = @At(value = "CONSTANT", args = "intValue=40")
    )
    private int ec_updateRepairOutput_removeCap(int original) {
        return Integer.MAX_VALUE;
    }
}
