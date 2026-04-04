package enchantmentcontrol.mixin.vanilla.anvil;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.util.AnvilCostUtil;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ContainerRepair.class)
public abstract class ContainerRepairMixin_RepairCostScaling {

    @WrapOperation(
            method = "updateRepairOutput",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setRepairCost(I)V")
    )
    private void ec_updateRepairOutput_changeRepairCostScaling(
            ItemStack stackLeftCopy, int cost,
            Operation<Void> original,
            @Local(name = "itemstack2") ItemStack stackRight,
            @Local(name = "k") int renameCost,
            @Local(name = "i") int totalCost
    ) {
        int repairCost, useCount;
        int repairCostLeft = stackLeftCopy.getRepairCost();
        int repairCostRight = stackRight.getRepairCost();
        int useCountLeft = AnvilCostUtil.getAnvilCount(stackLeftCopy);
        int useCountRight = AnvilCostUtil.getAnvilCount(stackRight);

        //items from before this mixin got enabled
        if(useCountLeft == 0 && repairCostLeft > 0) {
            useCountLeft = MathHelper.floor(Math.log(repairCostLeft + 1) / Math.log(2));
            AnvilCostUtil.setAnvilCount(stackLeftCopy, useCountLeft);
        }
        if(useCountRight == 0 && repairCostRight > 0) {
            useCountRight = MathHelper.floor(Math.log(repairCostRight + 1) / Math.log(2));
            AnvilCostUtil.setAnvilCount(stackRight, useCountRight);
        }

        repairCost = AnvilCostUtil.combineCosts(repairCostLeft, repairCostRight);
        useCount = AnvilCostUtil.combineCounts(useCountLeft, useCountRight);

        float multi = ConfigHandler.anvil.repairCostScalingFactor;

        if (renameCost != totalCost || renameCost == 0) {
            switch(ConfigHandler.anvil.repairCostScalingType) {
                case CONST: // a
                    repairCost = (int) multi; break;
                case LINEAR: // a * useCount
                    repairCost = (int) (multi * useCount); break;
                case QUADRATIC: // a * useCount^2
                    repairCost = (int) (multi * useCount * useCount); break;
                case EXPONENTIAL: default: // (a ^ useCount) - 1
                    repairCost = MathHelper.ceil(repairCost * multi) + 1; break;
            }
        }

        AnvilCostUtil.setAnvilCount(stackLeftCopy, useCount + 1);
        original.call(stackLeftCopy, repairCost);
    }
}
