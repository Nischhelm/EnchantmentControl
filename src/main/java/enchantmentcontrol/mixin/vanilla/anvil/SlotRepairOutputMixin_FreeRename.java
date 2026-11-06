package enchantmentcontrol.mixin.vanilla.anvil;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import enchantmentcontrol.util.mixinwrapper.IFreeRenameContainer;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Anvil output slot. Vanilla requires maximumCost > 0 to take the output, which would block free renames */
@Mixin(targets = "net.minecraft.inventory.ContainerRepair$2")
public abstract class SlotRepairOutputMixin_FreeRename {
    @Definition(id = "maximumCost", field = "Lnet/minecraft/inventory/ContainerRepair;maximumCost:I")
    @Expression("?.maximumCost > 0")
    @ModifyExpressionValue(method = "canTakeStack", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean ec_canTakeStack_allowFreeRename(boolean original, @Local(argsOnly = true) EntityPlayer player) {
        return original || (player.openContainer instanceof IFreeRenameContainer && ((IFreeRenameContainer) player.openContainer).ec$isFreeRenameOutput());
    }
}
