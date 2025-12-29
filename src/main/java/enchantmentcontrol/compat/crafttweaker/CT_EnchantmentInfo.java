package enchantmentcontrol.compat.crafttweaker;

import com.teamacronymcoders.contenttweaker.modules.vanilla.enchantments.EnchantmentBuilder;
import crafttweaker.annotations.ZenRegister;
import enchantmentcontrol.config.provider.IncompatibleConfigProvider;
import enchantmentcontrol.config.provider.ItemTypeConfigProvider;
import enchantmentcontrol.util.EnchantmentInfo;
import enchantmentcontrol.util.MaxEnchantabilityMode;
import enchantmentcontrol.util.enchantmenttypes.ITypeMatcher;
import enchantmentcontrol.util.vanillasystem.VanillaSystem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.text.TextFormatting;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

import java.util.*;
import java.util.stream.Collectors;

@ZenRegister
@ZenExpansion("mods.contenttweaker.enchantments.EnchantmentBuilder")
@SuppressWarnings("unused")
public class CT_EnchantmentInfo {
    private static final Map<EnchantmentBuilder, EnchantmentInfo> map = new HashMap<>();

    public static void onBuilderCreate(EnchantmentBuilder builder, String name){
        map.put(builder, new EnchantmentInfo(builder.domain, name));
    }

    public static void onBuilderRegister(EnchantmentBuilder builder, Enchantment enchant){
        EnchantmentInfo info = map.get(builder);

        //if renamed
        if(!info.modId.equals(builder.domain)) {
            info.modId = builder.domain;
            info.enchId = builder.name;
            info.id = builder.domain + ":" + builder.name;
        }

        info.slots = Arrays.stream(builder.applicableSlots).map(isl -> (EntityEquipmentSlot) isl.getInternal()).collect(Collectors.toList());
        info.rarity = enchant.getRarity();
        info.type = enchant.type;
        info.isTreasure = enchant.isTreasureEnchantment();
        info.isCurse = enchant.isCurse();
        info.isAllowedOnBooks = enchant.isAllowedOnBooks();
        info.minLvl = enchant.getMinLevel();
        info.maxLvl = enchant.getMaxLevel();
        EnchantmentInfo.register(info, enchant);
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
    public static void setTypes(EnchantmentBuilder builder, String[] matchers){
        types.put(builder, matchers);
    }

    @ZenMethod
    public static void setTypesForAnvil(EnchantmentBuilder builder, String[] matchers){
        typesAnvil.put(builder, matchers);
    }

    @ZenMethod
    public static void setIncompatibleTo(EnchantmentBuilder builder, String[] enchants){
        incompat.put(builder, enchants);
    }

    private static final Map<EnchantmentBuilder, String[]> types = new HashMap<>();
    private static final Map<EnchantmentBuilder, String[]> typesAnvil = new HashMap<>();
    private static final Map<EnchantmentBuilder, String[]> incompat = new HashMap<>();

    public static void postInit(){
        for(Map.Entry<EnchantmentBuilder, String[]> entry : types.entrySet()){
            if(entry.getValue().length == 0) continue;
            Enchantment ench = EnchantmentInfo.getEnchantmentObject(map.get(entry.getKey()));
            Set<ITypeMatcher> set = ItemTypeConfigProvider.itemTypes.computeIfAbsent(ench, e -> new HashSet<>());
            Arrays.stream(entry.getValue())
                    .map(ItemTypeConfigProvider::getMatcher)
                    .filter(Objects::nonNull)
                    .forEach(set::add);
        }
        for(Map.Entry<EnchantmentBuilder, String[]> entry : typesAnvil.entrySet()){
            if(entry.getValue().length == 0) continue;
            Enchantment ench = EnchantmentInfo.getEnchantmentObject(map.get(entry.getKey()));
            Set<ITypeMatcher> set = ItemTypeConfigProvider.itemTypesAnvil.computeIfAbsent(ench, e -> new HashSet<>());
            Arrays.stream(entry.getValue())
                    .map(ItemTypeConfigProvider::getMatcher)
                    .filter(Objects::nonNull)
                    .forEach(set::add);
        }
        for(Map.Entry<EnchantmentBuilder, String[]> entry : incompat.entrySet()){
            if(entry.getValue().length == 0) continue;
            Enchantment ench = EnchantmentInfo.getEnchantmentObject(map.get(entry.getKey()));
            Set<Enchantment> set = IncompatibleConfigProvider.incompatibleEnchantments.computeIfAbsent(ench, e -> new HashSet<>());
            Arrays.stream(entry.getValue())
                    .map(Enchantment::getEnchantmentByLocation)
                    .filter(Objects::nonNull)
                    .forEach(set::add);
        }
    }
}
