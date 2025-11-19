package enchantmentcontrol.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.enchantment.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Debug(export = true)
@Mixin(value = {
        EnchantmentArrowDamage.class,
        EnchantmentArrowFire.class,
        EnchantmentArrowInfinite.class,
        EnchantmentArrowKnockback.class,
        EnchantmentBindingCurse.class,
        EnchantmentDamage.class,
        EnchantmentDigging.class,
        EnchantmentDurability.class,
        EnchantmentFireAspect.class,
        EnchantmentFishingSpeed.class,
        EnchantmentFrostWalker.class,
        EnchantmentKnockback.class,
        EnchantmentLootBonus.class,
        EnchantmentMending.class,
        EnchantmentOxygen.class,
        EnchantmentProtection.class,
        EnchantmentSweepingEdge.class,
        EnchantmentThorns.class,
        EnchantmentUntouching.class,
        EnchantmentVanishingCurse.class,
        EnchantmentWaterWalker.class,
        EnchantmentWaterWorker.class
})
@SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
public abstract class VanillaEnchantmentMixin extends Enchantment {
    protected VanillaEnchantmentMixin(Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots) {
        super(rarityIn, typeIn, slots);
    }

    @WrapMethod(method = "getEntityEquipment")
    public List<ItemStack> ec_getEntityEquipment(EntityLivingBase entityIn, Operation<List<ItemStack>> original) {
        return original.call(entityIn);
    }

    @WrapMethod(method = "getRarity")
    public Rarity ec_getRarity(Operation<Rarity> original) {
        return original.call();
    }

    @WrapMethod(method = "getMinLevel")
    public int ec_getMinLevel(Operation<Integer> original) {
        return original.call();
    }

    @WrapMethod(method = "getMaxLevel")
    public int ec_getMaxLevel(Operation<Integer> original) {
        return original.call();
    }

    @WrapMethod(method = "getMinEnchantability")
    public int ec_getMinEnchantability(int enchantmentLevel, Operation<Integer> original) {
        return original.call(enchantmentLevel);
    }

    @WrapMethod(method = "getMaxEnchantability")
    public int ec_getMaxEnchantability(int enchantmentLevel, Operation<Integer> original) {
        return original.call(enchantmentLevel);
    }

    @WrapMethod(method = "calcModifierDamage")
    public int ec_calcModifierDamage(int level, DamageSource source, Operation<Integer> original) {
        return original.call(level, source);
    }

    @WrapMethod(method = "calcDamageByCreature")
    public float ec_calcDamageByCreature(int level, EnumCreatureAttribute creatureType, Operation<Float> original) {
        return original.call(level, creatureType);
    }

    @WrapMethod(method = "canApplyTogether")
    protected boolean ec_canApplyTogether(Enchantment ench, Operation<Boolean> original) {
        return original.call(ench);
    }

    @WrapMethod(method = "getTranslatedName")
    public String ec_getTranslatedName(int level, Operation<String> original) {
        return original.call(level);
        //TODO: color
//        String s = I18n.translateToLocal(this.getName());
//
//        if (this.isCurse()) {
//            s = TextFormatting.RED + s;
//        }
//
//        return level == 1 && this.getMaxLevel() == 1 ? s : s + " " + I18n.translateToLocal("enchantment.level." + level);
    }

    @WrapMethod(method = "canApply")
    public boolean ec_canApply(ItemStack stack, Operation<Boolean> original) {
        return original.call(stack);
    }

    @WrapMethod(method = "onEntityDamaged")
    public void ec_onEntityDamaged(EntityLivingBase user, Entity target, int level, Operation<Void> original) {
        original.call(user, target, level);
    }

    @WrapMethod(method = "onUserHurt")
    public void ec_onUserHurt(EntityLivingBase user, Entity attacker, int level, Operation<Void> original) {
        original.call(user, attacker, level);
    }

    @WrapMethod(method = "isTreasureEnchantment")
    public boolean ec_isTreasureEnchantment(Operation<Boolean> original) {
        return original.call();
    }

    @WrapMethod(method = "isCurse")
    public boolean ec_isCurse(Operation<Boolean> original) {
        return original.call();
    }

    @WrapMethod(method = "canApplyAtEnchantingTable", remap = false)
    public boolean ec_canApplyAtEnchantingTable(ItemStack stack, Operation<Boolean> original) {
        return original.call(stack);
    }

    @WrapMethod(method = "isAllowedOnBooks", remap = false)
    public boolean ec_isAllowedOnBooks(Operation<Boolean> original) {
        return original.call();
    }
}