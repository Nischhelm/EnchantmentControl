package enchantmentcontrol.core;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.config.provider.IncompatibleConfigProvider;
import enchantmentcontrol.config.provider.ItemTypeConfigProvider;
import enchantmentcontrol.util.EnchantmentInfo;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
public class EnchantmentHooks {

    // -------- BASIC PROPERTIES --------

    public static void getMinLevel(Enchantment ench, CallbackInfoReturnable<Integer> cir) {
        EnchantmentInfo info = EnchantmentInfo.get(ench);
        if (info != null && info.overwritesMinLvl)
            cir.setReturnValue(info.minLvl);
    }

    public static void getMaxLevel(Enchantment ench, CallbackInfoReturnable<Integer> cir) {
        EnchantmentInfo info = EnchantmentInfo.get(ench);
        if (info != null && info.overwritesMaxLvl)
            cir.setReturnValue(info.maxLvl);
    }

    public static void getMinEnchantability(Enchantment ench, int enchantmentLevel, CallbackInfoReturnable<Integer> cir) {
        EnchantmentInfo info = EnchantmentInfo.get(ench);
        if (info != null && info.ench != null)
            cir.setReturnValue(info.ench.getMinEnch(enchantmentLevel));
    }

    public static void getMaxEnchantability(Enchantment ench, int enchantmentLevel, CallbackInfoReturnable<Integer> cir) {
        EnchantmentInfo info = EnchantmentInfo.get(ench);
        if (info != null && info.ench != null)
            cir.setReturnValue(info.ench.getMaxEnch(enchantmentLevel));
    }

    public static void getTranslatedName(Enchantment ench, int level, CallbackInfoReturnable<String> cir) {
        EnchantmentInfo info = EnchantmentInfo.get(ench);
        if (info != null && info.displayColor != null)
            cir.setReturnValue(info.getTranslatedName(ench, level));
    }

    public static void isTreasureEnchantment(Enchantment ench, CallbackInfoReturnable<Boolean> cir) {
        EnchantmentInfo info = EnchantmentInfo.get(ench);
        if (info != null && info.overwritesIsTreasure)
            cir.setReturnValue(info.isTreasure);
    }

    public static void isCurse(Enchantment ench, CallbackInfoReturnable<Boolean> cir) {
        EnchantmentInfo info = EnchantmentInfo.get(ench);
        if (info != null && info.overwritesIsCurse)
            cir.setReturnValue(info.isCurse);
    }

    public static void isAllowedOnBooks(Enchantment ench, CallbackInfoReturnable<Boolean> cir) {
        EnchantmentInfo info = EnchantmentInfo.get(ench);
        if (info != null && info.overwritesIsAllowedOnBooks)
            cir.setReturnValue(info.isAllowedOnBooks);
    }

    // -------- APPLICABILITY --------

    //canApplyTogether
    public static void isCompatibleWith(Enchantment thisEnch, Enchantment otherEnch, CallbackInfoReturnable<Boolean> cir) {
        if (!ConfigHandler.incompatible.incompatibleEnabled) return;
        if (ConfigHandler.dev.printIncompats || !EnchantmentControl.loadingComplete) return;
        cir.setReturnValue(thisEnch != otherEnch && IncompatibleConfigProvider.areCompatible(thisEnch, otherEnch));
    }

    //used by vanilla canApply, named like this to be more clear
    public static void canApplyAtAnvil(Enchantment ench, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (ConfigHandler.dev.printTypes || !EnchantmentControl.loadingComplete) {
            ItemTypeConfigProvider.probeAnvil = true;
            return;
        }
        if (!ConfigHandler.itemTypes.enable || !ConfigHandler.itemTypes.anvil.enable) return;
        if (ItemTypeConfigProvider.shouldYieldToModdedBehavior(ench, true)) return;

        // return canItemApply || original
        if (ItemTypeConfigProvider.canItemApply(ench, stack, true))
            cir.setReturnValue(true);
    }

    //used by forge canApplyAtEnchantingTable, named like this to be more clear
    public static void canApplyInGeneral(Enchantment ench, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (ConfigHandler.dev.printTypes || !EnchantmentControl.loadingComplete) {
            ItemTypeConfigProvider.probe = true;
            return;
        }
        if (!ConfigHandler.itemTypes.enable || !ConfigHandler.itemTypes.general.enable) return;
        if (ItemTypeConfigProvider.shouldYieldToModdedBehavior(ench, false)) return;

        // Don't run original
        cir.setReturnValue(ItemTypeConfigProvider.canItemApply(ench, stack, false));
    }

    // -------- VANILLA BEHAVIORS --------

    //calcModifierDamage
    public static void protectionBehavior(Enchantment ench, int level, DamageSource source, CallbackInfoReturnable<Integer> cir) {
        EnchantmentInfo info = EnchantmentInfo.get(ench);
        if (info != null && info.protectionBehavior != null) {
            cir.setReturnValue(info.protectionBehavior.apply(level, source));
        }
    }

    //calcDamageByCreature
    public static void sharpnessBehavior(Enchantment ench, int level, EnumCreatureAttribute creatureType, CallbackInfoReturnable<Float> cir) {
        EnchantmentInfo info = EnchantmentInfo.get(ench);
        if (info != null && info.sharpnessBehavior != null) {
            cir.setReturnValue(info.sharpnessBehavior.apply(level, creatureType));
        }
    }

    //onEntityDamaged
    public static void arthropodBehavior(Enchantment ench, EntityLivingBase user, Entity target, int level, CallbackInfo ci) {
        EnchantmentInfo info = EnchantmentInfo.get(ench);
        if (info != null && info.arthropodBehavior != null) {
            info.arthropodBehavior.accept(user, target, level);
            ci.cancel();
        }
    }

    //onUserHurt
    public static void thornsBehavior(Enchantment ench, EntityLivingBase user, Entity attacker, int level, CallbackInfo ci) {
        EnchantmentInfo info = EnchantmentInfo.get(ench);
        if (info != null && info.thornsBehavior != null) {
            info.thornsBehavior.accept(user, attacker, level);
            ci.cancel();
        }
    }
}
