package enchantmentcontrol.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import enchantmentcontrol.util.EnchantmentInfo;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperLvlsMixin {
    @ModifyReturnValue(method = "getSweepingDamageRatio", at = @At("RETURN"))
    private static float ec_getSweepingDamageRatio(float original, EntityLivingBase user) {
        for(EnchantmentInfo info : EnchantmentInfo.getOverriders("sweeping")){
            int lvl = EnchantmentHelper.getMaxEnchantmentLevel(EnchantmentInfo.getEnchantmentObject(info), user);
            original = info.sweepingStrength.apply(original, lvl); //iteratively apply functions
        }
        return original;
    }

    @ModifyReturnValue(method = "getKnockbackModifier", at = @At("RETURN"))
    private static int ec_getKnockbackModifier(int original, EntityLivingBase entity) {
        float tmp = original; //treat internally as float
        for(EnchantmentInfo info : EnchantmentInfo.getOverriders("knockback")){
            int lvl = EnchantmentHelper.getMaxEnchantmentLevel(EnchantmentInfo.getEnchantmentObject(info), entity);
            tmp = info.knockbackStrength.apply(tmp, lvl); //iteratively apply functions
        }
        return (int) tmp;
    }

    @ModifyReturnValue(method = "getFireAspectModifier", at = @At("RETURN"))
    private static int ec_getFireAspectModifier(int original, EntityLivingBase entity) {
        float tmp = original; //treat internally as float
        for(EnchantmentInfo info : EnchantmentInfo.getOverriders("fireAspect")){
            int lvl = EnchantmentHelper.getMaxEnchantmentLevel(EnchantmentInfo.getEnchantmentObject(info), entity);
            tmp = info.fireAspectStrength.apply(tmp, lvl); //iteratively apply functions
        }
        return (int) tmp;
    }

    @ModifyReturnValue(method = "getRespirationModifier", at = @At("RETURN"))
    private static int ec_getRespirationModifier(int original, EntityLivingBase entity) {
        float tmp = original; //treat internally as float
        for(EnchantmentInfo info : EnchantmentInfo.getOverriders("respiration")){
            int lvl = EnchantmentHelper.getMaxEnchantmentLevel(EnchantmentInfo.getEnchantmentObject(info), entity);
            tmp = info.respirationStrength.apply(tmp, lvl); //iteratively apply functions
        }
        return (int) tmp;
    }

    @ModifyReturnValue(method = "getDepthStriderModifier", at = @At("RETURN"))
    private static int ec_getDepthStriderModifier(int original, EntityLivingBase entity) {
        float tmp = original; //treat internally as float
        for(EnchantmentInfo info : EnchantmentInfo.getOverriders("depthStrider")){
            int lvl = EnchantmentHelper.getMaxEnchantmentLevel(EnchantmentInfo.getEnchantmentObject(info), entity);
            tmp = info.depthStriderStrength.apply(tmp, lvl); //iteratively apply functions
        }
        return (int) tmp;
    }

    @ModifyReturnValue(method = "getEfficiencyModifier", at = @At("RETURN"))
    private static int ec_getEfficiencyModifier(int original, EntityLivingBase entity) {
        float tmp = original; //treat internally as float
        for(EnchantmentInfo info : EnchantmentInfo.getOverriders("efficiency")){
            int lvl = EnchantmentHelper.getMaxEnchantmentLevel(EnchantmentInfo.getEnchantmentObject(info), entity);
            tmp = info.efficiencyStrength.apply(tmp, lvl); //iteratively apply functions
        }
        return (int) tmp;
    }

    @ModifyReturnValue(method = "getFishingLuckBonus", at = @At("RETURN"))
    private static int ec_getFishingLuckBonus(int original, ItemStack stack) {
        float tmp = original; //treat internally as float
        for(EnchantmentInfo info : EnchantmentInfo.getOverriders("luckOfTheSea")){
            int lvl = EnchantmentHelper.getEnchantmentLevel(EnchantmentInfo.getEnchantmentObject(info), stack);
            tmp = info.luckOfTheSeaStrength.apply(tmp, lvl); //iteratively apply functions
        }
        return (int) tmp;
    }

    @ModifyReturnValue(method = "getFishingSpeedBonus", at = @At("RETURN"))
    private static int ec_getFishingSpeedBonus(int original, ItemStack stack) {
        float tmp = original; //treat internally as float
        for(EnchantmentInfo info : EnchantmentInfo.getOverriders("lure")){
            int lvl = EnchantmentHelper.getEnchantmentLevel(EnchantmentInfo.getEnchantmentObject(info), stack);
            tmp = info.lureStrength.apply(tmp, lvl); //iteratively apply functions
        }
        return (int) tmp;
    }

    @ModifyReturnValue(method = "getLootingModifier", at = @At("RETURN"))
    private static int ec_getLootingModifier(int original, EntityLivingBase entity) {
        float tmp = original; //treat internally as float
        for(EnchantmentInfo info : EnchantmentInfo.getOverriders("looting")){
            int lvl = EnchantmentHelper.getMaxEnchantmentLevel(EnchantmentInfo.getEnchantmentObject(info), entity);
            tmp = info.lootingStrength.apply(tmp, lvl); //iteratively apply functions
        }
        return (int) tmp;
    }

    @ModifyReturnValue(method = "getAquaAffinityModifier", at = @At("RETURN"))
    private static boolean ec_getAquaAffinityModifier(boolean original, EntityLivingBase entity) {
        //Return true if any of the overriders returns true (=has the capability)
        if(original) return true;
        for(EnchantmentInfo info : EnchantmentInfo.getOverriders("aquaAffinity")){
            int lvl = EnchantmentHelper.getMaxEnchantmentLevel(EnchantmentInfo.getEnchantmentObject(info), entity);
            if(info.hasAquaAffinity.apply(lvl)) return true; //iteratively apply functions
        }
        return false;
    }

    @ModifyReturnValue(method = "hasFrostWalkerEnchantment", at = @At("RETURN"))
    private static boolean ec_hasFrostWalkerEnchantment(boolean original, EntityLivingBase entity) {
        //Return true if any of the overriders returns true (=has the capability)
        if(original) return true;
        for(EnchantmentInfo info : EnchantmentInfo.getOverriders("frostWalker")){
            int lvl = EnchantmentHelper.getMaxEnchantmentLevel(EnchantmentInfo.getEnchantmentObject(info), entity);
            if(info.hasFrostWalker.apply(lvl)) return true; //iteratively apply functions
        }
        return false;
    }

    @ModifyReturnValue(method = "hasBindingCurse", at = @At("RETURN"))
    private static boolean ec_hasBindingCurse(boolean original, ItemStack stack) {
        //Return true if any of the overriders returns true (=has the capability)
        if(original) return true;
        for(EnchantmentInfo info : EnchantmentInfo.getOverriders("bindingCurse")){
            int lvl = EnchantmentHelper.getEnchantmentLevel(EnchantmentInfo.getEnchantmentObject(info), stack);
            if(info.hasBindingCurse.apply(lvl)) return true; //iteratively apply functions
        }
        return false;
    }

    @ModifyReturnValue(method = "hasVanishingCurse", at = @At("RETURN"))
    private static boolean ec_hasVanishingCurse(boolean original, ItemStack stack) {
        //Return true if any of the overriders returns true (=has the capability)
        if(original) return true;
        for(EnchantmentInfo info : EnchantmentInfo.getOverriders("vanishingCurse")){
            int lvl = EnchantmentHelper.getEnchantmentLevel(EnchantmentInfo.getEnchantmentObject(info), stack);
            if(info.hasVanishingCurse.apply(lvl)) return true; //iteratively apply functions
        }
        return false;
    }
}