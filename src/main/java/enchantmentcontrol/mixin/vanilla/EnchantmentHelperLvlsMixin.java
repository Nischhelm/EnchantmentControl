package enchantmentcontrol.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperLvlsMixin {
    @ModifyReturnValue(method = "getSweepingDamageRatio", at = @At("RETURN"))
    private static float ec_getSweepingDamageRatio(float original) {
        return original;
    }

    @ModifyReturnValue(method = "getKnockbackModifier", at = @At("RETURN"))
    private static int ec_getKnockbackModifier(int original) {
        return original;
    }

    @ModifyReturnValue(method = "getFireAspectModifier", at = @At("RETURN"))
    private static int ec_getFireAspectModifier(int original) {
        return original;
    }

    @ModifyReturnValue(method = "getRespirationModifier", at = @At("RETURN"))
    private static int ec_getRespirationModifier(int original) {
        return original;
    }

    @ModifyReturnValue(method = "getDepthStriderModifier", at = @At("RETURN"))
    private static int ec_getDepthStriderModifier(int original) {
        return original;
    }

    @ModifyReturnValue(method = "getEfficiencyModifier", at = @At("RETURN"))
    private static int ec_getEfficiencyModifier(int original) {
        return original;
    }

    @ModifyReturnValue(method = "getFishingLuckBonus", at = @At("RETURN"))
    private static int ec_getFishingLuckBonus(int original) {
        return original;
    }

    @ModifyReturnValue(method = "getFishingSpeedBonus", at = @At("RETURN"))
    private static int ec_getFishingSpeedBonus(int original) {
        return original;
    }

    @ModifyReturnValue(method = "getLootingModifier", at = @At("RETURN"))
    private static int ec_getLootingModifier(int original) {
        return original;
    }

    @ModifyReturnValue(method = "getAquaAffinityModifier", at = @At("RETURN"))
    private static boolean ec_getAquaAffinityModifier(boolean original) {
        return original;
    }

    @ModifyReturnValue(method = "hasFrostWalkerEnchantment", at = @At("RETURN"))
    private static boolean ec_hasFrostWalkerEnchantment(boolean original) {
        return original;
    }

    @ModifyReturnValue(method = "hasBindingCurse", at = @At("RETURN"))
    private static boolean ec_hasBindingCurse(boolean original) {
        return original;
    }

    @ModifyReturnValue(method = "hasVanishingCurse", at = @At("RETURN"))
    private static boolean ec_hasVanishingCurse(boolean original) {
        return original;
    }
}