package enchantmentcontrol.mixin.vanilla.anvil;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.inventory.ContainerRepair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiRepair.class)
public abstract class GuiRepairMixin_NoXPCost {
    @Shadow @Final private ContainerRepair anvil;

    /** Only show the cost line if the output is actually blocked by being Too Expensive, anything else is free */
    @Definition(id = "maximumCost", field = "Lnet/minecraft/inventory/ContainerRepair;maximumCost:I")
    @Expression("?.maximumCost > 0")
    @ModifyExpressionValue(method = "drawGuiContainerForegroundLayer", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean ec_drawGuiContainerForegroundLayer_hideCost(boolean original) {
        return original
                && this.anvil.maximumCost >= 40
                && !Minecraft.getMinecraft().player.capabilities.isCreativeMode
                && !this.anvil.getSlot(2).getHasStack();
    }
}
