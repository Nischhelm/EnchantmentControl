package enchantmentcontrol.mixin.vanilla.etable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.util.ReEnchantUtil;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

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
            return ConfigHandler.etable.reEnchantMaxTimes <= 0 || ReEnchantUtil.getEnchantCount(stack) < ConfigHandler.etable.reEnchantMaxTimes;
        } else
            return false;
    }

    @ModifyExpressionValue(
            method = {"onCraftMatrixChanged"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/ContainerEnchantment;getEnchantmentList(Lnet/minecraft/item/ItemStack;II)Ljava/util/List;")
    )
    private List<EnchantmentData> ec_disallowIllegallReEnchant_simulate(List<EnchantmentData> rolledEnchants, @Local(name="itemstack") ItemStack stack) {
        return ReEnchantUtil.reenchant(rolledEnchants, stack, true);
    }

    @ModifyExpressionValue(
            method = {"enchantItem"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/ContainerEnchantment;getEnchantmentList(Lnet/minecraft/item/ItemStack;II)Ljava/util/List;")
    )
    private List<EnchantmentData> ec_disallowIllegallReEnchant_actual(List<EnchantmentData> rolledEnchants, @Local(name="itemstack") ItemStack stack) {
        return ReEnchantUtil.reenchant(rolledEnchants, stack, false);
    }

    @Inject(
            method = "enchantItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;onEnchant(Lnet/minecraft/item/ItemStack;I)V")
    )
    private void ec_incrementEnchantCount(EntityPlayer player, int id, CallbackInfoReturnable<Boolean> cir, @Local(name = "itemstack") ItemStack stack){
        if(player.getRNG().nextFloat() > ConfigHandler.etable.reEnchantSkipIncrementChance)
            ReEnchantUtil.setEnchantCount(stack, ReEnchantUtil.getEnchantCount(stack) + 1);
    }
}
