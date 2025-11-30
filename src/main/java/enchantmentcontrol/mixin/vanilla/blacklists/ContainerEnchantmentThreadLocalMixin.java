package enchantmentcontrol.mixin.vanilla.blacklists;

import enchantmentcontrol.util.FromEnchTableThreadLocal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.IInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ContainerEnchantment.class)
public abstract class ContainerEnchantmentThreadLocalMixin extends Container {
    @Inject(
            method =  "onCraftMatrixChanged",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/ContainerEnchantment;getEnchantmentList(Lnet/minecraft/item/ItemStack;II)Ljava/util/List;")
    )
    private void soManyEnchantments_vanillaContainerEnchantment_onCraftMatrixChanged_activateThreadLocals(IInventory inventoryIn, CallbackInfo ci) {
        //Tell Random Level Blacklist Mixin that this call is from Enchanting Table
        FromEnchTableThreadLocal.set(true);
    }

    @Inject(
            method =  "enchantItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/ContainerEnchantment;getEnchantmentList(Lnet/minecraft/item/ItemStack;II)Ljava/util/List;")
    )
    private void soManyEnchantments_vanillaContainerEnchantment_onCraftMatrixChanged_activateThreadLocals(EntityPlayer playerIn, int id, CallbackInfoReturnable<Boolean> cir) {
        //Tell Random Level Blacklist Mixin that this call is from Enchanting Table
        FromEnchTableThreadLocal.set(true);
    }
}