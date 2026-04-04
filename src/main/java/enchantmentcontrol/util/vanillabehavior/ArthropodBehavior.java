package enchantmentcontrol.util.vanillabehavior;

import com.google.gson.annotations.SerializedName;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.HashSet;
import java.util.Set;

public class ArthropodBehavior implements TriConsumer<EntityLivingBase, Entity, Integer> {
    @SerializedName("Potion")
    public String potionId = "minecraft:slowness";
    @SerializedName("Constant Amplifier")
    public float startAmp = 3;
    @SerializedName("Amplifier per Level")
    public float ampPerLevel = 0F;
    @SerializedName("Constant Duration")
    public float startDuration = 20;
    @SerializedName("Duration per Level")
    public float durationPerLevel = 5;
    @SerializedName("Creature Attributes")
    public final Set<String> attributes = new HashSet<>();

    transient private Potion potion = MobEffects.SLOWNESS;

    public ArthropodBehavior setPotion(String potion) {
        this.potion = Potion.getPotionFromResourceLocation(potion);
        return this;
    }

    public ArthropodBehavior setDuration(int start, float perLvl){
        this.startDuration = start;
        this.durationPerLevel = perLvl;
        return this;
    }

    public ArthropodBehavior setAmplifier(int start, float perLvl){
        this.startAmp = start;
        this.ampPerLevel = perLvl;
        return this;
    }

    public ArthropodBehavior addCreatureAttribute(String attribute){
        this.attributes.add(attribute);
        return this;
    }

    @Override
    public void accept(EntityLivingBase user, Entity target, Integer lvl) {
        if(lvl <= 0) return;
        if (!(target instanceof EntityLivingBase)) return;

        EntityLivingBase defender = (EntityLivingBase) target;
        if (attributes.contains(defender.getCreatureAttribute().name())) {
            int duration = Math.round(startDuration + durationPerLevel * lvl);
            int amp = Math.round(startAmp + ampPerLevel * lvl);
            defender.addPotionEffect(new PotionEffect(potion, duration, amp));
        }
    }
}
