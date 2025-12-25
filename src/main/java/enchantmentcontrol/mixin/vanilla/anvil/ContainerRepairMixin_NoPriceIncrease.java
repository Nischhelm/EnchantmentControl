package enchantmentcontrol.mixin.vanilla.anvil;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(ContainerRepair.class)
public abstract class ContainerRepairMixin_NoPriceIncrease {

    @ModifyExpressionValue(
            method = "updateRepairOutput",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getEnchantments(Lnet/minecraft/item/ItemStack;)Ljava/util/Map;", ordinal = 0)
    )
    private Map<Enchantment, Integer> ec_updateRepairOutput_saveOriginalMap(
            Map<Enchantment, Integer> original,
            @Share("originalMap") LocalRef<Map<Enchantment, Integer>> originalMap
    ) {
        originalMap.set(new LinkedHashMap<>(original)); //copy of original map since that one gets modified later
        return original;
    }

    @WrapWithCondition(
            method = "updateRepairOutput",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setRepairCost(I)V")
    )
    private boolean ec_updateRepairOutput_removeCap(
            ItemStack instance, int cost,
            @Local(name = "map") Map<Enchantment, Integer> modifiedEnchants,
            @Share("originalMap") LocalRef<Map<Enchantment, Integer>> originalEnchants
    ) {
        return !modifiedEnchants.equals(originalEnchants.get()); //no cost increase if enchants didn't change
    }
}
