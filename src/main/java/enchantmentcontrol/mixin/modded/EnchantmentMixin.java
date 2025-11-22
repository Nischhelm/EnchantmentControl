package enchantmentcontrol.mixin.modded;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import enchantmentcontrol.util.EnchantmentInfo;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;

@Debug(export = true)
@Mixin(targets = {"net.minecraft.enchantment.Enchantment"})
@SuppressWarnings({"MixinSuperClass"})
public abstract class EnchantmentMixin extends Enchantment {
    protected EnchantmentMixin(Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots) {
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
        if(info != null && info.enchMode != null) return info.getMinEnchLvl(enchantmentLevel);
        return original.call(enchantmentLevel);
    }

    @WrapMethod(method = "getMaxEnchantability")
    public int ec_getMaxEnchantability(int enchantmentLevel, Operation<Integer> original) {
        EnchantmentInfo info = EnchantmentInfo.get(this);
        if(info != null && info.enchMode != null) return info.getMaxEnchLvl(enchantmentLevel);
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
        EnchantmentInfo info = EnchantmentInfo.get(this);
        if(info != null && info.incompats != null) return !info.incompats.contains(ench) && this != ench;
        return original.call(ench);
    }

    @WrapMethod(method = "canApply")
    public boolean ec_canApply(ItemStack stack, Operation<Boolean> original) {
        return original.call(stack);
    }

    @WrapMethod(method = "canApplyAtEnchantingTable", remap = false)
    public boolean ec_canApplyAtEnchantingTable(ItemStack stack, Operation<Boolean> original) {
        return original.call(stack);
    }

    // VANILLA ENCHANTMENT BEHAVIORS

    @WrapMethod(method = "calcModifierDamage") //protection
    public int ec_calcModifierDamage(int level, DamageSource source, Operation<Integer> original) {
        return original.call(level, source);
    }

    @WrapMethod(method = "calcDamageByCreature") //sharpness
    public float ec_calcDamageByCreature(int level, EnumCreatureAttribute creatureType, Operation<Float> original) {
        return original.call(level, creatureType);
    }

    @WrapMethod(method = "onEntityDamaged") //arthropod
    public void ec_onEntityDamaged(EntityLivingBase user, Entity target, int level, Operation<Void> original) {
        original.call(user, target, level);
    }

    @WrapMethod(method = "onUserHurt") //thorns
    public void ec_onUserHurt(EntityLivingBase user, Entity attacker, int level, Operation<Void> original) {
        original.call(user, attacker, level);
    }
}