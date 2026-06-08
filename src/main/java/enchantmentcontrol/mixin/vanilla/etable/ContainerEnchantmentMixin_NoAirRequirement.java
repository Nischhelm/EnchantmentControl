package enchantmentcontrol.mixin.vanilla.etable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ContainerEnchantment.class)
public abstract class ContainerEnchantmentMixin_NoAirRequirement {
    @WrapOperation(
            method = "onCraftMatrixChanged",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isAirBlock(Lnet/minecraft/util/math/BlockPos;)Z")
    )
    private boolean ec_removeAirCondition(World instance, BlockPos pos, Operation<Boolean> original) {
        return true;
    }
}
