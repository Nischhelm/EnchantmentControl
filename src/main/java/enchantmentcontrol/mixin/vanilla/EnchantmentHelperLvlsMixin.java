package enchantmentcontrol.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import enchantmentcontrol.util.vanillasystem.VanillaSystem;
import enchantmentcontrol.util.vanillasystem.VanillaSystemOverride;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperLvlsMixin {
    @ModifyReturnValue(method = "getSweepingDamageRatio", at = @At("RETURN"))
    private static float ec_getSweepingDamageRatio(float original, EntityLivingBase entity) {
        return VanillaSystemOverride.applyAllOn(VanillaSystem.SWEEPING, original, entity);
    }

    @ModifyReturnValue(method = "getKnockbackModifier", at = @At("RETURN"))
    private static int ec_getKnockbackModifier(int original, EntityLivingBase entity) {
        return VanillaSystemOverride.applyAllOn(VanillaSystem.KNOCKBACK, original, entity);
    }

    @ModifyReturnValue(method = "getFireAspectModifier", at = @At("RETURN"))
    private static int ec_getFireAspectModifier(int original, EntityLivingBase entity) {
        return VanillaSystemOverride.applyAllOn(VanillaSystem.FIRE_ASPECT, original, entity);
    }

    @ModifyReturnValue(method = "getRespirationModifier", at = @At("RETURN"))
    private static int ec_getRespirationModifier(int original, EntityLivingBase entity) {
        return VanillaSystemOverride.applyAllOn(VanillaSystem.RESPIRATION, original, entity);
    }

    @ModifyReturnValue(method = "getDepthStriderModifier", at = @At("RETURN"))
    private static int ec_getDepthStriderModifier(int original, EntityLivingBase entity) {
        return VanillaSystemOverride.applyAllOn(VanillaSystem.DEPTH_STRIDER, original, entity);
    }

    @ModifyReturnValue(method = "getEfficiencyModifier", at = @At("RETURN"))
    private static int ec_getEfficiencyModifier(int original, EntityLivingBase entity) {
        return VanillaSystemOverride.applyAllOn(VanillaSystem.EFFICIENCY, original, entity);
    }

    @ModifyReturnValue(method = "getFishingLuckBonus", at = @At("RETURN"))
    private static int ec_getFishingLuckBonus(int original, ItemStack stack) {
        return VanillaSystemOverride.applyAllOn(VanillaSystem.LUCK_OF_THE_SEA, original, stack);
    }

    @ModifyReturnValue(method = "getFishingSpeedBonus", at = @At("RETURN"))
    private static int ec_getFishingSpeedBonus(int original, ItemStack stack) {
        return VanillaSystemOverride.applyAllOn(VanillaSystem.LURE, original, stack);
    }

    @ModifyReturnValue(method = "getLootingModifier", at = @At("RETURN"))
    private static int ec_getLootingModifier(int original, EntityLivingBase entity) {
        return VanillaSystemOverride.applyAllOn(VanillaSystem.LOOTING, original, entity);
    }

    @ModifyReturnValue(method = "getAquaAffinityModifier", at = @At("RETURN"))
    private static boolean ec_getAquaAffinityModifier(boolean original, EntityLivingBase entity) {
        return VanillaSystemOverride.applyAllOn(VanillaSystem.AQUA_AFFINITY, original, entity);
    }

    @ModifyReturnValue(method = "hasFrostWalkerEnchantment", at = @At("RETURN"))
    private static boolean ec_hasFrostWalkerEnchantment(boolean original, EntityLivingBase entity) {
        return VanillaSystemOverride.applyAllOn(VanillaSystem.FROST_WALKER, original, entity);
    }

    @ModifyReturnValue(method = "hasBindingCurse", at = @At("RETURN"))
    private static boolean ec_hasBindingCurse(boolean original, ItemStack stack) {
        return VanillaSystemOverride.applyAllOn(VanillaSystem.BINDING_CURSE, original, stack);
    }

    @ModifyReturnValue(method = "hasVanishingCurse", at = @At("RETURN"))
    private static boolean ec_hasVanishingCurse(boolean original, ItemStack stack) {
        return VanillaSystemOverride.applyAllOn(VanillaSystem.VANISHING_CURSE, original, stack);
    }
}