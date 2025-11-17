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
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Debug(export = true)
@Pseudo
@Mixin(targets = {"net.minecraft.enchantment.Enchantment"}, remap = false)
public abstract class EnchantmentMixin extends Enchantment {
    protected EnchantmentMixin(Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots) {
        super(rarityIn, typeIn, slots);
    }

    @WrapMethod(method = "func_185260_a")
    public List<ItemStack> ec_getEntityEquipment(EntityLivingBase entityIn, Operation<List<ItemStack>> original) {
        return original.call(entityIn);
    }

    @WrapMethod(method = "func_77324_c")
    public Enchantment.Rarity ec_getRarity(Operation<Enchantment.Rarity> original) {
        return original.call();
    }

    @WrapMethod(method = "func_77319_d")
    public int ec_getMinLevel(Operation<Integer> original) {
        return original.call();
    }

    @Unique
    @WrapMethod(method = "func_77325_b")
    public int ec_getMaxLevel(Operation<Integer> original) {
        return original.call();
    }

    @WrapMethod(method = "func_77321_a")
    public int ec_getMinEnchantability(int enchantmentLevel, Operation<Integer> original) {
        return original.call(enchantmentLevel);
    }

    @WrapMethod(method = "func_77317_b")
    public int ec_getMaxEnchantability(int enchantmentLevel, Operation<Integer> original) {
        return original.call(enchantmentLevel);
    }

    @WrapMethod(method = "func_77318_a")
    public int ec_calcModifierDamage(int level, DamageSource source, Operation<Integer> original) {
        return original.call(level, source);
    }

    @WrapMethod(method = "func_152376_a")
    public float ec_calcDamageByCreature(int level, EnumCreatureAttribute creatureType, Operation<Float> original) {
        return original.call(level, creatureType);
    }

    @WrapMethod(method = "func_77326_a")
    protected boolean ec_canApplyTogether(Enchantment ench, Operation<Boolean> original) {
        return original.call(ench);
    }

    @WrapMethod(method = "func_77316_c")
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

    @WrapMethod(method = "func_92089_a")
    public boolean ec_canApply(ItemStack stack, Operation<Boolean> original) {
        return original.call(stack);
    }

    @WrapMethod(method = "func_151368_a")
    public void ec_onEntityDamaged(EntityLivingBase user, Entity target, int level, Operation<Void> original) {
        original.call(user, target, level);
    }

    @WrapMethod(method = "func_151367_b")
    public void ec_onUserHurt(EntityLivingBase user, Entity attacker, int level, Operation<Void> original) {
        original.call(user, attacker, level);
    }

    @WrapMethod(method = "func_185261_e")
    public boolean ec_isTreasureEnchantment(Operation<Boolean> original) {
        return original.call();
    }

    @WrapMethod(method = "func_190936_d")
    public boolean ec_isCurse(Operation<Boolean> original) {
        return original.call();
    }

    @WrapMethod(method = "canApplyAtEnchantingTable")
    public boolean ec_canApplyAtEnchantingTable(ItemStack stack, Operation<Boolean> original) {
        return original.call(stack);
    }

    @WrapMethod(method = "isAllowedOnBooks")
    public boolean ec_isAllowedOnBooks(Operation<Boolean> original) {
        return original.call();
    }
}