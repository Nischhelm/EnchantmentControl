package enchantmentcontrol.mixin.vanilla.etable;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.inventory.ContainerEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

@Mixin(ContainerEnchantment.class)
public abstract class ContainerEnchantmentMixin_AlwaysReroll {
    @WrapWithCondition(
            method = {"onCraftMatrixChanged", "getEnchantmentList"},
            at = @At(value = "INVOKE", target = "Ljava/util/Random;setSeed(J)V")
    )
    private boolean ec_alwaysReroll(Random instance, long l){
        return false;
    }
}
