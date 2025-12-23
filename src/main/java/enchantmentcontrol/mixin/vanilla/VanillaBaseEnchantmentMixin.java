package enchantmentcontrol.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Enchantment.class) //copy of VanillaEnchantmentMixin and modded.EnchantmentMixin just for net.minecraft.enchantment.Enchantment
public abstract class VanillaBaseEnchantmentMixin {
    @Shadow public abstract String getName();

    @WrapMethod(method = "getMinLevel")
    public int ec_getMinLevel(Operation<Integer> original) {
        EnchantmentInfo info = EnchantmentInfo.get((Enchantment) (Object) this);
        EnchantmentControl.LOGGER.debug("Enchantment: {} has min level: {}", this.getName(), info==null ? "no info" : !info.overwritesMinLvl? "doesnt overwrite" : info.minLvl);
        if(info != null && info.overwritesMinLvl) return info.minLvl;
        return original.call();
    }

    @WrapMethod(method = "getMaxLevel")
    public int ec_getMaxLevel(Operation<Integer> original) {
        EnchantmentInfo info = EnchantmentInfo.get((Enchantment) (Object) this);
        EnchantmentControl.LOGGER.debug("Enchantment: {} has max level: {}", this.getName(), info==null ? "no info" : !info.overwritesMaxLvl? "doesnt overwrite" : info.maxLvl);
        if(info != null && info.overwritesMaxLvl) return info.maxLvl;
        return original.call();
    }

    @WrapMethod(method = "getMinEnchantability")
    public int ec_getMinEnchantability(int enchantmentLevel, Operation<Integer> original) {
        EnchantmentInfo info = EnchantmentInfo.get((Enchantment) (Object) this);
        if(info != null && info.ench != null) return info.ench.getMinEnch(enchantmentLevel);
        return original.call(enchantmentLevel);
    }

    @WrapMethod(method = "getMaxEnchantability")
    public int ec_getMaxEnchantability(int enchantmentLevel, Operation<Integer> original) {
        EnchantmentInfo info = EnchantmentInfo.get((Enchantment) (Object) this);
        if(info != null && info.ench != null) return info.ench.getMaxEnch(enchantmentLevel);
        return original.call(enchantmentLevel);
    }

    @WrapMethod(method = "getTranslatedName")
    public String ec_getTranslatedName(int level, Operation<String> original) {
        EnchantmentInfo info = EnchantmentInfo.get((Enchantment) (Object) this);
        if(info != null && info.displayColor != null)
            return info.getTranslatedName((Enchantment) (Object) this, level);
        else
            return original.call(level);
    }

    @WrapMethod(method = "isTreasureEnchantment")
    public boolean ec_isTreasureEnchantment(Operation<Boolean> original) {
        EnchantmentInfo info = EnchantmentInfo.get((Enchantment) (Object) this);
        if(info != null && info.overwritesIsTreasure) return info.isTreasure;
        return original.call();
    }

    @WrapMethod(method = "isCurse")
    public boolean ec_isCurse(Operation<Boolean> original) {
        EnchantmentInfo info = EnchantmentInfo.get((Enchantment) (Object) this);
        if(info != null && info.overwritesIsCurse) return info.isCurse;
        return original.call();
    }

    @WrapMethod(method = "isAllowedOnBooks", remap = false)
    public boolean ec_isAllowedOnBooks(Operation<Boolean> original) {
        EnchantmentInfo info = EnchantmentInfo.get((Enchantment) (Object) this);
        if(info != null && info.overwritesIsAllowedOnBooks) return info.isAllowedOnBooks;
        return original.call();
    }

    // APPLICABILITY

    @WrapMethod(method = "canApplyTogether")
    protected boolean ec_canApplyTogether(Enchantment ench, Operation<Boolean> original) {
        if(!ConfigHandler.dev.readIncompats) return IncompatibleConfigProvider.areCompatible((Enchantment) (Object)this, ench);
        return original.call(ench);
        //EnchantmentInfo info = EnchantmentInfo.get(this);
        //if(info != null && info.incompats != null) return !info.incompats.contains(ench) && this != ench;
        //return original.call(ench);
    }

    @WrapMethod(method = "canApply")
    public boolean ec_canApply(ItemStack stack, Operation<Boolean> original) {
        if(ConfigHandler.dev.readTypes) return original.call(stack);
        if(!ItemTypeConfigProvider.isSupported((Enchantment) (Object) this, true)) return original.call(stack);
        return ItemTypeConfigProvider.canItemApply((Enchantment) (Object) this, stack, true) || original.call(stack);
    }

    @WrapMethod(method = "canApplyAtEnchantingTable", remap = false)
    public boolean ec_canApplyAtEnchantingTable(ItemStack stack, Operation<Boolean> original) {
        if(ConfigHandler.dev.readTypes) return original.call(stack);
        if(!ItemTypeConfigProvider.isSupported((Enchantment) (Object) this, false)) return original.call(stack);
        return ItemTypeConfigProvider.canItemApply((Enchantment) (Object) this, stack, false);
    }

    // VANILLA ENCHANTMENT BEHAVIORS

    @WrapMethod(method = "calcModifierDamage") //protection
    public int ec_calcModifierDamage(int level, DamageSource source, Operation<Integer> original) {
        EnchantmentInfo info = EnchantmentInfo.get((Enchantment) (Object) this);
        if(info != null && info.protectionBehavior != null)
            return info.protectionBehavior.apply(level, source);
        return original.call(level, source);
    }

    @WrapMethod(method = "calcDamageByCreature") //sharpness
    public float ec_calcDamageByCreature(int level, EnumCreatureAttribute creatureType, Operation<Float> original) {
        EnchantmentInfo info = EnchantmentInfo.get((Enchantment) (Object) this);
        if(info != null && info.sharpnessBehavior != null)
            return info.sharpnessBehavior.apply(level, creatureType);
        return original.call(level, creatureType);
    }

    @WrapMethod(method = "onEntityDamaged") //arthropod
    public void ec_onEntityDamaged(EntityLivingBase user, Entity target, int level, Operation<Void> original) {
        EnchantmentInfo info = EnchantmentInfo.get((Enchantment) (Object) this);
        if(info != null && info.arthropodBehavior != null) {
            info.arthropodBehavior.accept(user, target, level);
            return;
        }
        original.call(user, target, level);
    }

    @WrapMethod(method = "onUserHurt") //thorns
    public void ec_onUserHurt(EntityLivingBase user, Entity attacker, int level, Operation<Void> original) {
        EnchantmentInfo info = EnchantmentInfo.get((Enchantment) (Object) this);
        if(info != null && info.thornsBehavior != null) {
            info.thornsBehavior.accept(user, attacker, level);
            return;
        }
        original.call(user, attacker, level);
    }
}