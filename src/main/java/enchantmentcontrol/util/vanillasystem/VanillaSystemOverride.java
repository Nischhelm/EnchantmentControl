package enchantmentcontrol.util.vanillasystem;

import enchantmentcontrol.util.EnchantmentInfo;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class VanillaSystemOverride implements BiFunction<Float, Integer, Float> {

    public final float multiplier;

    public VanillaSystemOverride(){
        this(0);
    }

    public VanillaSystemOverride(float cfgValue){
        this.multiplier = cfgValue;
    }

    @Override
    public Float apply(Float valIn, Integer lvl) {
        //Regular implementation readable via json config. more complicated implementations via CT TODO
        return valIn + lvl * multiplier;
    }


    // -------- STATIC --------

    private static final Map<VanillaSystem, List<EnchantmentInfo>> vanillaSystemOverriders = new HashMap<VanillaSystem, List<EnchantmentInfo>>(){{
        for (VanillaSystem value : VanillaSystem.values()) put(value, new ArrayList<>());
    }};

    public static List<EnchantmentInfo> getOverriders(VanillaSystem system){
        return vanillaSystemOverriders.get(system);
    }

    public static float applyAllOn(VanillaSystem system, float original, EntityLivingBase entity){
        for(EnchantmentInfo info : getOverriders(system)){
            int lvl = EnchantmentHelper.getMaxEnchantmentLevel(EnchantmentInfo.getEnchantmentObject(info), entity);
            original = info.getVanillaSystemOverride(system).apply(original, lvl); //iteratively apply functions
        }
        return original;
    }

    public static int applyAllOn(VanillaSystem system, int original, EntityLivingBase entity){
        float tmp = original; //treat internally as float
        for(EnchantmentInfo info : getOverriders(system)){
            int lvl = EnchantmentHelper.getMaxEnchantmentLevel(EnchantmentInfo.getEnchantmentObject(info), entity);
            tmp = info.getVanillaSystemOverride(system).apply(tmp, lvl); //iteratively apply functions
        }
        return (int) tmp;
    }

    public static boolean applyAllOn(VanillaSystem system, boolean original, EntityLivingBase entity){
        //Return true if any of the overriders returns true (=has the capability)
        if(original) return true;
        float val = 0;
        for(EnchantmentInfo info : getOverriders(system)){
            int lvl = EnchantmentHelper.getMaxEnchantmentLevel(EnchantmentInfo.getEnchantmentObject(info), entity);
            val = info.getVanillaSystemOverride(system).apply(val, lvl);
        }
        return val >= 1; //iteratively apply functions
    }

    public static int applyAllOn(VanillaSystem system, int original, ItemStack stack){
        float tmp = original; //treat internally as float
        for(EnchantmentInfo info : getOverriders(system)){
            int lvl = EnchantmentHelper.getEnchantmentLevel(EnchantmentInfo.getEnchantmentObject(info), stack);
            tmp = info.getVanillaSystemOverride(system).apply(tmp, lvl); //iteratively apply functions
        }
        return (int) tmp;
    }

    public static boolean applyAllOn(VanillaSystem system, boolean original, ItemStack stack){
        //Return true if any of the overriders returns true (=has the capability)
        if(original) return true;
        float val = 0;
        for(EnchantmentInfo info : getOverriders(system)){
            int lvl = EnchantmentHelper.getEnchantmentLevel(EnchantmentInfo.getEnchantmentObject(info), stack);
            val = info.getVanillaSystemOverride(system).apply(val, lvl);
        }
        return val >= 1;
    }
}
