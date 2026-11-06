package enchantmentcontrol.mixin.vanilla.anvil;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Anvil output slot. Taking the output neither requires nor costs any XP */
@Mixin(targets = "net.minecraft.inventory.ContainerRepair$2")
public abstract class SlotRepairOutputMixin_NoXPCost {
    // Too Expensive outputs are already removed in updateRepairOutput, so any present output can be taken
    @ModifyReturnValue(method = "canTakeStack", at = @At("RETURN"))
    private boolean ec_canTakeStack_noXPRequired(boolean original) {
        return ((Slot) (Object) this).getHasStack();
    }

    @WrapOperation(
            method = "onTake",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;addExperienceLevel(I)V")
    )
    private void ec_onTake_noXPCost(EntityPlayer player, int levels, Operation<Void> original) {
        // don't remove any levels
    }
}
