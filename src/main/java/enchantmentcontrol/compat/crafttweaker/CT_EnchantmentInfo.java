package enchantmentcontrol.compat.crafttweaker;

import com.teamacronymcoders.contenttweaker.modules.vanilla.enchantments.EnchantmentBuilder;
import crafttweaker.annotations.ZenRegister;
import enchantmentcontrol.mixin.modded.contenttweaker.EnchantmentBuilderAccessor;
import enchantmentcontrol.util.EnchantmentInfo;
import enchantmentcontrol.util.MaxEnchantabilityMode;
import enchantmentcontrol.util.vanillasystem.VanillaSystem;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.text.TextFormatting;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ZenRegister
@ZenExpansion("mods.contenttweaker.enchantments.EnchantmentBuilder")
@SuppressWarnings("unused")
public class CT_EnchantmentInfo {
    private static final Map<EnchantmentBuilder, EnchantmentInfo> map = new HashMap<>();

    public static void onBuilderCreate(EnchantmentBuilder builder){
        map.put(builder, new EnchantmentInfo(builder.domain + ":" + builder.name));
    }

    public static void onBuilderRegister(EnchantmentBuilder builder){
        EnchantmentInfo info = map.get(builder);

        info.slots = Arrays.stream(builder.applicableSlots).map(isl -> (EntityEquipmentSlot) isl.getInternal()).collect(Collectors.toList());
        info.rarity = ((EnchantmentBuilderAccessor)builder).getRarity();
        info.type = ((EnchantmentBuilderAccessor)builder).getType();
        info.isTreasure = builder.treasure;
        info.isCurse = builder.curse;
        info.isAllowedOnBooks = builder.allowedOnBooks;
        info.minLvl = builder.minLevel;
        info.maxLvl = builder.maxLevel;
        EnchantmentInfo.register(info);
    }

    @ZenSetter("displayColor")
    public static void setDisplayColor(EnchantmentBuilder builder, String color){
        map.get(builder).displayColor = TextFormatting.valueOf(color);
    }

    @ZenGetter("displayColor")
    public static String getDisplayColor(EnchantmentBuilder builder){
        return map.get(builder).displayColor.name();
    }

    @ZenSetter("doublePrice")
    public static void setIsDoublePrice(EnchantmentBuilder builder, boolean doublePrice){
        map.get(builder).doublePrice = doublePrice;
    }

    @ZenGetter("doublePrice")
    public static boolean getIsDoublePrice(EnchantmentBuilder builder){
        return map.get(builder).doublePrice;
    }

    @ZenMethod
    public static void setEnchantabilityCalc(EnchantmentBuilder builder, int minEnch, int lvlSpan, int range){
        map.get(builder).ench = new EnchantmentInfo.EnchantabilityCalc(minEnch, lvlSpan, range, MaxEnchantabilityMode.NORMAL);
    }

    @ZenMethod
    public static void setEnchantabilityCalc(EnchantmentBuilder builder, int minEnch, int lvlSpan, int range, String mode){
        map.get(builder).ench = new EnchantmentInfo.EnchantabilityCalc(minEnch, lvlSpan, range, MaxEnchantabilityMode.valueOf(mode));
    }

    @ZenGetter("enchMin")
    public static int getMinEnch(EnchantmentBuilder builder){
        return map.get(builder).ench.minEnchLvl;
    }

    @ZenGetter("enchLvlSpan")
    public static int getLvlSpan(EnchantmentBuilder builder){
        return map.get(builder).ench.enchLvlSpan;
    }

    @ZenGetter("enchRange")
    public static int getRange(EnchantmentBuilder builder){
        return map.get(builder).ench.enchLvlRange;
    }

    @ZenGetter("enchMaxMode")
    public static String getMode(EnchantmentBuilder builder){
        return map.get(builder).ench.enchMode.name();
    }

    @ZenMethod
    public static void setVanillaOverride(EnchantmentBuilder builder, String system, float multi){
        map.get(builder).registerVanillaSystemOverride(VanillaSystem.valueOf(system), multi);
    }

    @ZenMethod
    public static void setVanillaOverride(EnchantmentBuilder builder, String system, CT_SystemOverride override){
        map.get(builder).registerVanillaSystemOverride(VanillaSystem.valueOf(system), override);
    }

    @ZenMethod
    public static void setTypes(EnchantmentBuilder builder, String[] types){
        //TODO
    }

    @ZenMethod
    public static void setTypesForAnvil(EnchantmentBuilder builder, String[] types){
        //TODO
    }

    @ZenMethod
    public static void setIncompatibleTo(EnchantmentBuilder builder, String[] types){
        //TODO
    }
}
