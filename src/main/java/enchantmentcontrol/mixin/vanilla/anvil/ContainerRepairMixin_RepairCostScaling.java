package enchantmentcontrol.mixin.vanilla.anvil;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import enchantmentcontrol.config.ConfigHandler;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
        int useCountLeft = ec$getAnvilCount(stackLeftCopy);
        int useCountRight = ec$getAnvilCount(stackRight);

        //items from before this mixin got enabled
        if(useCountLeft == 0 && repairCostLeft > 0) {
            useCountLeft = MathHelper.floor(Math.log(repairCostLeft + 1) / Math.log(2));
            ec$setAnvilCount(stackLeftCopy, useCountLeft);
        }
        if(useCountRight == 0 && repairCostRight > 0) {
            useCountRight = MathHelper.floor(Math.log(repairCostRight + 1) / Math.log(2));
            ec$setAnvilCount(stackRight, useCountRight);
        }

        switch(ConfigHandler.anvil.repairCostCombinationType) {
            case MIN: //prob doesn't make sense to use but whatever
                repairCost = Math.min(repairCostLeft, repairCostRight);
                useCount = Math.min(useCountLeft, useCountRight);
                break;
            case SUM:
                repairCost = repairCostLeft + repairCostRight;
                useCount = useCountLeft + useCountRight;
                break;
            case AVERAGE:
                repairCost = (repairCostLeft + repairCostRight) / 2;
                useCount = (useCountLeft + useCountRight) / 2;
                break;
            case MAX: default:
                repairCost = Math.max(repairCostLeft, repairCostRight);
                useCount = Math.max(useCountLeft, useCountRight);
                break;
        }

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

        ec$setAnvilCount(stackLeftCopy, useCount + 1);
        original.call(stackLeftCopy, repairCost);
    }

    @Unique private static final String ec$key = "AnvilCount";

    @Unique
    public int ec$getAnvilCount(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(ec$key, 3) ? stack.getTagCompound().getInteger(ec$key) : 0;
    }

    @Unique
    public void ec$setAnvilCount(ItemStack stack, int count) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger(ec$key, count);
    }
}
