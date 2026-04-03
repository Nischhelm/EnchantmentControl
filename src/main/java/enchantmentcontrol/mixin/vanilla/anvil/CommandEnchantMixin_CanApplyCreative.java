package enchantmentcontrol.mixin.vanilla.anvil;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.config.folders.ItemTypeConfig;
import net.minecraft.command.CommandEnchant;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CommandEnchant.class)
public abstract class CommandEnchantMixin_CanApplyCreative {
    @ModifyExpressionValue(
            method = "execute",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;canApply(Lnet/minecraft/item/ItemStack;)Z")
    )
    private boolean ec_overrideCanApply(boolean original, @Local(argsOnly = true) ICommandSender sender){
        if(ConfigHandler.itemTypes.creativeOptions != ItemTypeConfig.EnumCreativeAllowed.CMD && ConfigHandler.itemTypes.creativeOptions != ItemTypeConfig.EnumCreativeAllowed.BOTH)
            return original;
        if(sender instanceof EntityPlayer && ((EntityPlayer)sender).capabilities.isCreativeMode) return true; //skip canApply condition
        return original;
    }
}
