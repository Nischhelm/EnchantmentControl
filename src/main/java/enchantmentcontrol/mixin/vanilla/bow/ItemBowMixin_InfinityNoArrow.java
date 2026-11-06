package enchantmentcontrol.mixin.vanilla.bow;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemBow.class)
public abstract class ItemBowMixin_InfinityNoArrow {
    // Vanilla only allows drawing the bow if there is ammo, even with Infinity. Shooting itself already works without ammo if there is Infinity.
    @ModifyExpressionValue(
            method = "onItemRightClick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 0)
    )
    private boolean ec_infinityNeedsNoArrow(boolean noAmmo, World world, EntityPlayer player, EnumHand hand) {
        return noAmmo && EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, player.getHeldItem(hand)) <= 0;
    }
}
