package enchantmentcontrol.mixin.vanilla.anvil;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.inventory.ContainerRepair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ContainerRepair.class)
public abstract class ContainerRepairMixin_ZeroCostIfUnchanged {

    @ModifyVariable(
        method = "updateRepairOutput",
        at = @At(value = "LOAD", ordinal = 1),
        name = "k3"
    )
    private int ec_zeroCostIfNoChange(int rarityMulti, @Local(name = "i2") int leftEnchLvl, @Local(name = "j2") int combinedEnchLvl) {
        // If combined level is not higher than left level, adding this enchantment provides no change
        if (combinedEnchLvl <= leftEnchLvl) return 0;
        return rarityMulti;
    }
}