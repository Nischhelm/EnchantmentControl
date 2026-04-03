package enchantmentcontrol.mixin.vanilla.etable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.BlockPos;
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
            method = "<init>(Lnet/minecraft/entity/player/InventoryPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V",
            at = @At("TAIL")
    )
    private void ec_storePlayer(InventoryPlayer playerInv, World worldIn, BlockPos pos, CallbackInfo ci){
        this.ec$player = playerInv.player;
    }

    @Inject(
            method = "onCraftMatrixChanged",
            at = @At(value = "INVOKE", target = "Ljava/util/Random;setSeed(J)V")
    )
    private void ec_alwaysReroll(IInventory inventoryIn, CallbackInfo ci){
        this.xpSeed = this.rand.nextInt();
        ((EntityPlayerAccessor)ec$player).setXpSeed(this.xpSeed);
    }
}
