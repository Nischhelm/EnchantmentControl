package enchantmentcontrol.util.vanillabehavior;

import com.google.gson.annotations.SerializedName;
import net.minecraft.entity.EnumCreatureAttribute;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

public class SharpnessBehavior implements BiFunction<Integer, EnumCreatureAttribute, Float> {
    @SerializedName("Constant")
    public float startDmg = 0.5F;
    @SerializedName("Per Lvl")
    public float dmgPerLvl = 0.5F;
    @SerializedName("Matching Creature Attributes")
    public Set<String> attributes = new HashSet<>();

    public SharpnessBehavior(float start, float perLvl, String... attributes) {
        this.startDmg = start;
        this.dmgPerLvl = perLvl;
        this.attributes.addAll(Arrays.asList(attributes));
    }

    @Override
    public Float apply(Integer lvl, EnumCreatureAttribute creatureAttribute) {
        return attributes.contains(creatureAttribute.name()) ? startDmg + dmgPerLvl * lvl : 0F;
    }
}
