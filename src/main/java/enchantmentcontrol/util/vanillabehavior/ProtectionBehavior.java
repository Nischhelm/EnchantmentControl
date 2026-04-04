package enchantmentcontrol.util.vanillabehavior;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.DamageSource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

public class ProtectionBehavior implements BiFunction<Integer, DamageSource, Integer> {
    @SerializedName("Constant")
    public final int startProt;
    @SerializedName("Per Lvl")
    public final int protPerLvl;
    @SerializedName("Matching Damage Sources")
    public final Set<String> sources = new HashSet<>();

    public ProtectionBehavior(int start, int perLvl, String... sources) {
        this.startProt = start;
        this.protPerLvl = perLvl;
        this.sources.addAll(Arrays.asList(sources));
    }

    @Override
    public Integer apply(Integer lvl, DamageSource source) {
        //Output is used in getDamageAfterMagicAbsorb, clamped between sum value of 0 to 20 (each point is 4% reduction)
        return sources.contains(source.damageType) ? startProt + protPerLvl * lvl : 0;
    }
}
