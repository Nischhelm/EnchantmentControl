package enchantmentcontrol.mixin.vanilla.anvil;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.config.folders.ItemTypeConfig;
import net.minecraft.inventory.ContainerRepair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ContainerRepair.class)
public abstract class ContainerRepairMixin_CanApplyCreative {
    @ModifyExpressionValue(
            method = "updateRepairOutput",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerCapabilities;isCreativeMode:Z", ordinal = 0)
    )
    private boolean ec_overrideCanApply(boolean original){
        return original && (ConfigHandler.itemTypes.creativeOptions == ItemTypeConfig.EnumCreativeAllowed.ANVIL || ConfigHandler.itemTypes.creativeOptions == ItemTypeConfig.EnumCreativeAllowed.BOTH);
    }
}
