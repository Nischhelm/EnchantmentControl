package enchantmentcontrol.mixin.vanilla.etable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(ContainerEnchantment.class)
public abstract class ContainerEnchantmentMixin_AlwaysReroll {
    @Shadow public int xpSeed;
    @Shadow @Final private Random rand;

    @Unique private EntityPlayer ec$player;

    @Inject(
            method = "<init>(Lnet/minecraft/entity/player/InventoryPlayer;Lnet/minecraft/world/World;)V",
            at = @At("TAIL")
    )
    private void ec_storePlayer(InventoryPlayer playerInv, World worldIn, CallbackInfo ci){
        this.ec$player = playerInv.player;
    }

    @Inject(
            method = "onCraftMatrixChanged",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/ContainerEnchantment;detectAndSendChanges()V", shift = At.Shift.AFTER)
    )
    private void ec_alwaysReroll(IInventory inventoryIn, CallbackInfo ci){
        ((EntityPlayerAccessor)ec$player).setXpSeed(this.rand.nextInt());
        this.xpSeed = ec$player.getXPSeed();
    }
}
