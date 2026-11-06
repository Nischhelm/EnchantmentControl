package enchantmentcontrol.mixin.vanilla.anvil;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import enchantmentcontrol.Tags;
import enchantmentcontrol.util.AnvilCostUtil;
import enchantmentcontrol.util.mixinwrapper.IFreeRenameContainer;
import enchantmentcontrol.util.mixinwrapper.IFreeRepairContainer;
import net.minecraft.inventory.ContainerRepair;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ContainerRepair.class)
public abstract class ContainerRepairMixin_FreeRepair implements IFreeRepairContainer {
    @Unique private boolean ec$freeRepairOutput = false;

    @Inject(method = "updateRepairOutput", at = @At("HEAD"))
    private void ec_updateRepairOutput_resetFreeRepairOutput(CallbackInfo ci) {
        this.ec$freeRepairOutput = false;
    }

    @Override
    public boolean ec$isFreeRepairOutput() {
        return this.ec$freeRepairOutput;
    }

    /** Repair with material: i only contains the 1 lvl per material used at this point */
    @Inject(
            method = "updateRepairOutput",
            at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/inventory/ContainerRepair;materialCost:I", ordinal = 1)
    )
    private void ec_updateRepairOutput_markMaterialRepairFree(
            CallbackInfo ci,
            @Local(name = "i") int levelCost,
            @Share(value = "freeRepairXP", namespace = Tags.MODID) LocalIntRef freeRepair
    ) {
        freeRepair.set(levelCost);
    }

    /** Repair with second item: i only contains the 2 lvls for the repair (or 0 if no repair happened) at this point */
    @Inject(
            method = "updateRepairOutput",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getEnchantments(Lnet/minecraft/item/ItemStack;)Ljava/util/Map;", ordinal = 1)
    )
    private void ec_updateRepairOutput_markItemRepairFree(
            CallbackInfo ci,
            @Local(name = "i") int levelCost,
            @Share(value = "freeRepairXP", namespace = Tags.MODID) LocalIntRef freeRepair
    ) {
        freeRepair.set(levelCost);
    }

    /**
     * Removes the repair cost.
     * If FreeRename is loaded too, it does the same for its part. Both return 0 if everything is free, so applying both is fine.
     */
    @WrapOperation(
            method = "updateRepairOutput",
            at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/inventory/ContainerRepair;maximumCost:I", ordinal = 0),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lorg/apache/commons/lang3/StringUtils;isBlank(Ljava/lang/CharSequence;)Z", remap = false))
    )
    private void ec_updateRepairOutput_removeRepairCost(
            ContainerRepair instance, int totalCost,
            Operation<Void> original,
            @Local(name = "i") int levelCost,
            @Local(name = "k") int renameCost,
            @Share(value = "freeRepairXP", namespace = Tags.MODID) LocalIntRef freeRepair
    ) {
        int freeRename = (Object) this instanceof IFreeRenameContainer ? renameCost : 0;
        this.ec$freeRepairOutput = AnvilCostUtil.isFreeOutput(levelCost, freeRepair.get() + freeRename);
        original.call(instance, this.ec$freeRepairOutput ? 0 : totalCost - freeRepair.get());
    }
}
