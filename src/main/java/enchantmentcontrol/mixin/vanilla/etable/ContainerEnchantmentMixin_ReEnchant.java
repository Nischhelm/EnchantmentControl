package enchantmentcontrol.mixin.vanilla.etable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.util.AnvilCostUtil;
import enchantmentcontrol.util.ReEnchantUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Filtering+changing the rolled enchants is done in ContainerEnchantmentMixin_ReEnchantRoll (no EE)
 * or via GenerateEnchantmentListEvent if Expanded Events is installed.
 */
@Mixin(ContainerEnchantment.class)
public abstract class ContainerEnchantmentMixin_ReEnchant {
    @ModifyExpressionValue(
            method = "onCraftMatrixChanged",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemEnchantable()Z")
    )
    private boolean ec_allowReEnchanting(boolean original, @Local ItemStack stack) {
        if(original) return true;
        if(!stack.getItem().isEnchantable(stack)) return false; //counters stack.isItemEnchantable
        if(stack.isItemEnchanted()) { //allows isEnchanted too
            return (ConfigHandler.etable.reEnchant.maxTimes <= 0 || ReEnchantUtil.getEnchantCount(stack) < ConfigHandler.etable.reEnchant.maxTimes) && (ConfigHandler.etable.reEnchant.allowAlreadyAnviled || (stack.getRepairCost() <= 0 && !AnvilCostUtil.hasAnvilCount(stack)));
        } else
            return false;
    }

    @WrapOperation(
            method = "enchantItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;addEnchantment(Lnet/minecraft/enchantment/Enchantment;I)V")
    )
    private void ec_combineExisting(ItemStack stack, Enchantment enchantment, int level, Operation<Void> original) {
        if(!ReEnchantUtil.combineNewLevelWithExisting(stack, enchantment, level))
            original.call(stack, enchantment, level);
    }

    @Inject(
            method = "enchantItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;onEnchant(Lnet/minecraft/item/ItemStack;I)V")
    )
    private void ec_incrementEnchantCount(EntityPlayer player, int id, CallbackInfoReturnable<Boolean> cir, @Local(name = "itemstack") ItemStack stack){
        ReEnchantUtil.incrementEnchantCount(player.getRNG(), stack);
    }
}
