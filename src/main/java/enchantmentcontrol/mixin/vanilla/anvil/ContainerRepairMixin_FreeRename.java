package enchantmentcontrol.mixin.vanilla.anvil;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import enchantmentcontrol.Tags;
import enchantmentcontrol.util.AnvilCostUtil;
import enchantmentcontrol.util.mixinwrapper.IFreeRenameContainer;
import net.minecraft.inventory.ContainerRepair;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ContainerRepair.class)
public abstract class ContainerRepairMixin_FreeRename implements IFreeRenameContainer {
    @Unique private boolean ec$freeRenameOutput = false;

    @Inject(method = "updateRepairOutput", at = @At("HEAD"))
    private void ec_updateRepairOutput_resetFreeRenameOutput(CallbackInfo ci) {
        this.ec$freeRenameOutput = false;
    }

    @Override
    public boolean ec$isFreeRenameOutput() {
        return this.ec$freeRenameOutput;
    }

    /**
     * Removes the rename cost (vanilla local "k").
     * If FreeRepair is loaded too, it does the same for its part. Both return 0 if everything is free, so applying both is fine.
     */
    @WrapOperation(
            method = "updateRepairOutput",
            at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/inventory/ContainerRepair;maximumCost:I", ordinal = 0),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lorg/apache/commons/lang3/StringUtils;isBlank(Ljava/lang/CharSequence;)Z", remap = false))
    )
    private void ec_updateRepairOutput_removeRenameCost(
            ContainerRepair instance, int totalCost,
            Operation<Void> original,
            @Local(name = "i") int levelCost,
            @Local(name = "k") int renameCost,
            @Share(value = "freeRepairXP", namespace = Tags.MODID) LocalIntRef freeRepair //stays 0 if FreeRepair isn't loaded
    ) {
        this.ec$freeRenameOutput = AnvilCostUtil.isFreeOutput(levelCost, renameCost + freeRepair.get());
        original.call(instance, this.ec$freeRenameOutput ? 0 : totalCost - renameCost);
    }
}
