package enchantmentcontrol.mixin.modded.enchantertools;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.sirsquidly.enchanter_tools.common.RuneEnchantmentLogic;
import com.sirsquidly.enchanter_tools.common.items.ItemLapisRune;
import enchantmentcontrol.util.ReEnchantUtil;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(RuneEnchantmentLogic.class)
public abstract class RuneEnchantmentLogicMixin {
    @ModifyReturnValue(method = "buildRunePool", at = @At("RETURN"), remap = false)
    private static List<EnchantmentData> ec_reenchantLapisRuneEnchanting(List<EnchantmentData> original, ItemLapisRune.RuneType runeType, ItemStack stack){
        return ReEnchantUtil.reenchant(original, stack, true); // can't use not-simulated (so combining with existing) because of how EnchanterTools handles it
    }
}
