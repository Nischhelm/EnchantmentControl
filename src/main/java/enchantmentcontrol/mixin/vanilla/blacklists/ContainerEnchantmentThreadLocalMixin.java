package enchantmentcontrol.mixin.vanilla.blacklists;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import enchantmentcontrol.util.EnchantmentContext;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ContainerEnchantment.class)
public abstract class ContainerEnchantmentThreadLocalMixin {
    @Shadow @Final private World world;
    @Shadow @Final private BlockPos position;
    @Unique private EntityPlayer ec$player;

    @Inject(
            method = "<init>(Lnet/minecraft/entity/player/InventoryPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V",
            at = @At("TAIL")
    )
    private void ec_capturePlayer(InventoryPlayer playerInv, World worldIn, BlockPos pos, CallbackInfo ci) {
        this.ec$player = playerInv.player;
    }

    @WrapOperation(
            method = {"onCraftMatrixChanged", "enchantItem"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/ContainerEnchantment;getEnchantmentList(Lnet/minecraft/item/ItemStack;II)Ljava/util/List;")
    )
    private List<EnchantmentData> ec_setContext_onCraftMatrixChanged(ContainerEnchantment instance, ItemStack stack, int enchantSlot, int level, Operation<List<EnchantmentData>> original) {
        EnchantmentContext.setContext(EnchantmentContext.fromEnchantingTable(this.world, this.ec$player, enchantSlot + 1, this.position));
        return original.call(instance, stack, enchantSlot, level);
    }
}