package enchantmentcontrol.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.config.provider.IncompatibleConfigProvider;
import enchantmentcontrol.config.provider.ItemTypeConfigProvider;
import enchantmentcontrol.core.EnchantmentDummy;
import enchantmentcontrol.util.EnchantmentInfo;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;

//@Debug(export = true)
@Mixin(targets = {
        "net.minecraft.enchantment.EnchantmentArrowDamage",
        "net.minecraft.enchantment.EnchantmentArrowFire",
        "net.minecraft.enchantment.EnchantmentArrowInfinite",
        "net.minecraft.enchantment.EnchantmentArrowKnockback",
        "net.minecraft.enchantment.EnchantmentBindingCurse",
        "net.minecraft.enchantment.EnchantmentDamage",
        "net.minecraft.enchantment.EnchantmentDigging",
        "net.minecraft.enchantment.EnchantmentDurability",
        "net.minecraft.enchantment.EnchantmentFireAspect",
        "net.minecraft.enchantment.EnchantmentFishingSpeed",
        "net.minecraft.enchantment.EnchantmentFrostWalker",
        "net.minecraft.enchantment.EnchantmentKnockback",
        "net.minecraft.enchantment.EnchantmentLootBonus",
        "net.minecraft.enchantment.EnchantmentMending",
        "net.minecraft.enchantment.EnchantmentOxygen",
        "net.minecraft.enchantment.EnchantmentProtection",
        "net.minecraft.enchantment.EnchantmentSweepingEdge",
        "net.minecraft.enchantment.EnchantmentThorns",
        "net.minecraft.enchantment.EnchantmentUntouching",
        "net.minecraft.enchantment.EnchantmentVanishingCurse",
        "net.minecraft.enchantment.EnchantmentWaterWalker",
        "net.minecraft.enchantment.EnchantmentWaterWorker"
}, value = EnchantmentDummy.class)
@SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
public abstract class VanillaEnchantmentMixin extends Enchantment { //copy of VanillaBaseEnchantmentMixin and modded.EnchantmentMixin just for all vanilla enchantments
    protected VanillaEnchantmentMixin(Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots) {
        super(rarityIn, typeIn, slots);
    }

    @WrapMethod(method = "getMinLevel")
    public int ec_getMinLevel(Operation<Integer> original) {
        EnchantmentInfo info = EnchantmentInfo.get(this);
        if(info != null && info.overwritesMinLvl) return info.minLvl;
        return original.call();
    }

    @WrapMethod(method = "getMaxLevel")
    public int ec_getMaxLevel(Operation<Integer> original) {
        EnchantmentInfo info = EnchantmentInfo.get(this);
        if(info != null && info.overwritesMaxLvl) return info.maxLvl;
        return original.call();
    }

    @WrapMethod(method = "getMinEnchantability")
    public int ec_getMinEnchantability(int enchantmentLevel, Operation<Integer> original) {
        EnchantmentInfo info = EnchantmentInfo.get(this);
        if(info != null && info.ench != null) return info.ench.getMinEnch(enchantmentLevel);
        return original.call(enchantmentLevel);
    }

    @WrapMethod(method = "getMaxEnchantability")
    public int ec_getMaxEnchantability(int enchantmentLevel, Operation<Integer> original) {
        EnchantmentInfo info = EnchantmentInfo.get(this);
        if(info != null && info.ench != null) return info.ench.getMaxEnch(enchantmentLevel);
        return original.call(enchantmentLevel);
    }

    @WrapMethod(method = "getTranslatedName")
    public String ec_getTranslatedName(int level, Operation<String> original) {
        EnchantmentInfo info = EnchantmentInfo.get(this);
        if(info != null && info.displayColor != null)
            return info.getTranslatedName(this, level);
        else
            return original.call(level);
    }

    @WrapMethod(method = "isTreasureEnchantment")
    public boolean ec_isTreasureEnchantment(Operation<Boolean> original) {
        EnchantmentInfo info = EnchantmentInfo.get(this);
        if(info != null && info.overwritesIsTreasure) return info.isTreasure;
        return original.call();
    }

    @WrapMethod(method = "isCurse")
    public boolean ec_isCurse(Operation<Boolean> original) {
        EnchantmentInfo info = EnchantmentInfo.get(this);
        if(info != null && info.overwritesIsCurse) return info.isCurse;
        return original.call();
    }

    @WrapMethod(method = "isAllowedOnBooks", remap = false)
    public boolean ec_isAllowedOnBooks(Operation<Boolean> original) {
        EnchantmentInfo info = EnchantmentInfo.get(this);
        if(info != null && info.overwritesIsAllowedOnBooks) return info.isAllowedOnBooks;
        return original.call();
    }

    // APPLICABILITY

    @WrapMethod(method = "canApplyTogether")
    protected boolean ec_canApplyTogether(Enchantment ench, Operation<Boolean> original) {
        if(!ConfigHandler.dev.printIncompats) return IncompatibleConfigProvider.areCompatible(this, ench);
        return original.call(ench);
    }

    @WrapMethod(method = "canApply")
    public boolean ec_canApply(ItemStack stack, Operation<Boolean> original) {
        if(ConfigHandler.dev.printTypes){
            ItemTypeConfigProvider.probeAnvil = true;
            return original.call(stack);
        }
        if(ItemTypeConfigProvider.shouldYieldToModdedBehavior(this, true)) return original.call(stack);
        return ItemTypeConfigProvider.canItemApply(this, stack, true) || original.call(stack);
    }

    @WrapMethod(method = "canApplyAtEnchantingTable", remap = false)
    public boolean ec_canApplyAtEnchantingTable(ItemStack stack, Operation<Boolean> original) {
        if(ConfigHandler.dev.printTypes){
            ItemTypeConfigProvider.probe = true;
            return original.call(stack);
        }
        if(ItemTypeConfigProvider.shouldYieldToModdedBehavior(this, false)) return original.call(stack);
        return ItemTypeConfigProvider.canItemApply(this, stack, false);
    }

    // VANILLA ENCHANTMENT BEHAVIORS

    @WrapMethod(method = "calcModifierDamage") //protection
    public int ec_calcModifierDamage(int level, DamageSource source, Operation<Integer> original) {
        EnchantmentInfo info = EnchantmentInfo.get(this);
        if(info != null && info.protectionBehavior != null)
            return info.protectionBehavior.apply(level, source);
        return original.call(level, source);
    }

    @WrapMethod(method = "calcDamageByCreature") //sharpness
    public float ec_calcDamageByCreature(int level, EnumCreatureAttribute creatureType, Operation<Float> original) {
        EnchantmentInfo info = EnchantmentInfo.get(this);
        if(info != null && info.sharpnessBehavior != null)
            return info.sharpnessBehavior.apply(level, creatureType);
        return original.call(level, creatureType);
    }

    @WrapMethod(method = "onEntityDamaged") //arthropod
    public void ec_onEntityDamaged(EntityLivingBase user, Entity target, int level, Operation<Void> original) {
        EnchantmentInfo info = EnchantmentInfo.get(this);
        if(info != null && info.arthropodBehavior != null) {
            info.arthropodBehavior.accept(user, target, level);
            return;
        }
        original.call(user, target, level);
    }

    @WrapMethod(method = "onUserHurt") //thorns
    public void ec_onUserHurt(EntityLivingBase user, Entity attacker, int level, Operation<Void> original) {
        EnchantmentInfo info = EnchantmentInfo.get(this);
        if(info != null && info.thornsBehavior != null) {
            info.thornsBehavior.accept(user, attacker, level);
            return;
        }
        original.call(user, attacker, level);
    }
}