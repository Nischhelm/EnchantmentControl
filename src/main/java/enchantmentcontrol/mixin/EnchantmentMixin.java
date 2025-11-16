package enchantmentcontrol.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
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

import java.util.List;

@Debug(export = true)
@Mixin(targets = {"net.minecraft.enchantment"}) //set to .Enchantment to have the injectors succeed while working on code
public abstract class EnchantmentMixin extends Enchantment {
    protected EnchantmentMixin(Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots) {
        super(rarityIn, typeIn, slots);
    }

    @WrapMethod(method = "getEntityEquipment")
    public List<ItemStack> getEntityEquipment(EntityLivingBase entityIn, Operation<List<ItemStack>> original) {
        return original.call(entityIn);
    }

    @WrapMethod(method = "getRarity")
    public Enchantment.Rarity getRarity(Operation<Rarity> original) {
        return original.call();
    }

    @WrapMethod(method = "getMinLevel")
    public int getMinLevel(Operation<Integer> original) {
        return original.call();
    }

    @WrapMethod(method = "getMaxLevel")
    public int getMaxLevel(Operation<Integer> original) {
        return original.call();
    }

    @WrapMethod(method = "getMinEnchantability")
    public int getMinEnchantability(int enchantmentLevel, Operation<Integer> original) {
        return original.call(enchantmentLevel);
    }

    @WrapMethod(method = "getMaxEnchantability")
    public int getMaxEnchantability(int enchantmentLevel, Operation<Integer> original) {
        return original.call(enchantmentLevel);
    }

    @WrapMethod(method = "calcModifierDamage")
    public int calcModifierDamage(int level, DamageSource source, Operation<Integer> original) {
        return original.call(level, source);
    }

    @WrapMethod(method = "calcDamageByCreature")
    public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType, Operation<Float> original) {
        return original.call(level, creatureType);
    }

    @WrapMethod(method = "canApplyTogether")
    protected boolean canApplyTogether(Enchantment ench, Operation<Boolean> original) {
        return original.call(ench);
    }

    @WrapMethod(method = "getTranslatedName")
    public String getTranslatedName(int level, Operation<String> original) {
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
    public boolean canApply(ItemStack stack, Operation<Boolean> original) {
        return original.call(stack);
    }

    @WrapMethod(method = "onEntityDamaged")
    public void onEntityDamaged(EntityLivingBase user, Entity target, int level, Operation<Void> original) {
        original.call(user, target, level);
    }

    @WrapMethod(method = "onUserHurt")
    public void onUserHurt(EntityLivingBase user, Entity attacker, int level, Operation<Void> original) {
        original.call(user, attacker, level);
    }

    @WrapMethod(method = "isTreasureEnchantment")
    public boolean isTreasureEnchantment(Operation<Boolean> original) {
        return original.call();
    }

    @WrapMethod(method = "isCurse")
    public boolean isCurse(Operation<Boolean> original) {
        return original.call();
    }

    @WrapMethod(method = "canApplyAtEnchantingTable")
    public boolean canApplyAtEnchantingTable(ItemStack stack, Operation<Boolean> original) {
        return original.call(stack);
    }

    @WrapMethod(method = "isAllowedOnBooks")
    public boolean isAllowedOnBooks(Operation<Boolean> original) {
        return original.call();
    }
}