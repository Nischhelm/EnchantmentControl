package enchantmentcontrol.util.vanillabehavior;

import com.google.gson.annotations.SerializedName;
import enchantmentcontrol.util.EnchantmentInfo;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.Random;

public class ThornsBehavior implements TriConsumer<EntityLivingBase, Entity, Integer> {
    transient private final EnchantmentInfo info;
    transient private Enchantment ench;

    @SerializedName("Constant Chance")
    public float startChance = 0;
    @SerializedName("Chance per Level")
    public float chancePerLevel = 0.15F;

    @SerializedName("Constant Dmg Min")
    public float startDmgMin = 1;
    @SerializedName("Constant Dmg Max")
    public float startDmgMax = 4;
    @SerializedName("Dmg Per Lvl")
    public float dmgPerLevel = 0;
    @SerializedName("Durability Cost")
    public int durabilityDmg = 3;

    public ThornsBehavior (EnchantmentInfo info) {
        this.info = info;
    }

    public ThornsBehavior setDamage(float startMin, float startMax, float perLvl){
        this.startDmgMin = startMin;
        this.startDmgMax = startMax;
        this.dmgPerLevel = perLvl;
        return this;
    }

    public ThornsBehavior setChances(float startChance, float chancePerLvl){
        this.startChance = startChance;
        this.chancePerLevel = chancePerLvl;
        return this;
    }

    public ThornsBehavior setDurabilityDamage(int duraDmg){
        this.durabilityDmg = duraDmg;
        return this;
    }

    @Override
    public void accept(EntityLivingBase user, Entity attacker, Integer lvl) {
        if(lvl <= 0) return;
        if(attacker == null) return;

        Random rand = user.getRNG();
        if (rand.nextFloat() >= startChance + chancePerLevel * (float) lvl) return;

        if(ench == null) ench = EnchantmentInfo.getEnchantmentObject(info);
        if(ench == null) return;
        ItemStack randomStack = EnchantmentHelper.getEnchantedItem(ench, user);
        if (randomStack.isEmpty()) return;

        float dmg = startDmgMin + rand.nextFloat() * (startDmgMax - startDmgMin) + dmgPerLevel * (float) lvl;
        attacker.attackEntityFrom(DamageSource.causeThornsDamage(user), dmg);

        damageArmor(randomStack, durabilityDmg, user);
    }

    private void damageArmor(ItemStack stackToDamage, int amount, EntityLivingBase user) {
        int slot = 0;
        boolean found = false;
        for (ItemStack invArmor : user.getArmorInventoryList()) {
            if (invArmor == stackToDamage) {
                found = true;
                break;
            }
            slot++;
        }
        if (!found) return;
        if (stackToDamage.getItem() instanceof ISpecialArmor) {
            ISpecialArmor specialItem = (ISpecialArmor) stackToDamage.getItem();
            specialItem.damageArmor(user, stackToDamage, DamageSource.causeThornsDamage(user), amount, slot);
        } else
            stackToDamage.damageItem(1, user);
    }
}
