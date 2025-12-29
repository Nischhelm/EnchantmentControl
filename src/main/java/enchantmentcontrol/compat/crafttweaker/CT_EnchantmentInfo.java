package enchantmentcontrol.compat.crafttweaker;

import com.teamacronymcoders.contenttweaker.modules.vanilla.enchantments.EnchantmentBuilder;
import crafttweaker.annotations.ZenRegister;
import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.EarlyConfigReader;
import enchantmentcontrol.config.provider.IncompatibleConfigProvider;
import enchantmentcontrol.config.provider.ItemTypeConfigProvider;
import enchantmentcontrol.util.EnchantmentInfo;
import enchantmentcontrol.util.MaxEnchantabilityMode;
import enchantmentcontrol.util.enchantmenttypes.ITypeMatcher;
import enchantmentcontrol.util.vanillasystem.VanillaSystem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
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
        String id = builder.domain + ":" + name;

        ResourceLocation remap = EarlyConfigReader.getRemap(id);
        if(remap != null) id = remap.toString();

        EnchantmentInfo info = EnchantmentInfo.get(id);
        if(info == null) info = new EnchantmentInfo(builder.domain, name); //if no json

        map.put(builder, info);
    }

    public static void onBuilderRegister(EnchantmentBuilder builder, Enchantment enchant){
        EnchantmentInfo info = map.get(builder);

        //if renamed
        if(!info.modId.equals(builder.domain)) {
            EnchantmentControl.LOGGER.warn("Modifying enchantment domain via CT. This is not recommended and can lead to crashes! Use the id remap config of EnchantmentControl instead.");
            info.modId = builder.domain;
            info.enchId = builder.name;
            info.id = builder.domain + ":" + builder.name;
        }

        //If jsons control it too, the jsons win
        if(info.slots != null) info.slots = Arrays.stream(builder.applicableSlots).map(isl -> (EntityEquipmentSlot) isl.getInternal()).collect(Collectors.toList());
        if(info.rarity != null) info.rarity = enchant.getRarity();
        if(info.type != null) info.type = enchant.type;
        if(!info.overwritesIsTreasure) info.isTreasure = enchant.isTreasureEnchantment();
        if(!info.overwritesIsCurse) info.isCurse = enchant.isCurse();
        if(!info.overwritesIsAllowedOnBooks) info.isAllowedOnBooks = enchant.isAllowedOnBooks();
        if(!info.overwritesMinLvl) info.minLvl = enchant.getMinLevel();
        if(!info.overwritesMaxLvl) info.maxLvl = enchant.getMaxLevel();
        EnchantmentInfo.register(info, enchant);
    }

    @ZenSetter("displayColor")
    public static void setDisplayColor(EnchantmentBuilder builder, String color){
        EnchantmentInfo info = map.get(builder);
        if(info.displayColor == null)
            info.displayColor = TextFormatting.valueOf(color);
    }

    @ZenGetter("displayColor")
    public static String getDisplayColor(EnchantmentBuilder builder){
        return map.get(builder).displayColor.name();
    }

    @ZenSetter("doublePrice")
    public static void setIsDoublePrice(EnchantmentBuilder builder, boolean doublePrice){
        EnchantmentInfo info = map.get(builder);
        if(!info.overwritesDoublePrice)
            info.doublePrice = doublePrice;
    }

    @ZenGetter("doublePrice")
    public static boolean getIsDoublePrice(EnchantmentBuilder builder){
        return map.get(builder).doublePrice;
    }

    @ZenMethod
    public static void setEnchantabilityCalc(EnchantmentBuilder builder, int minEnch, int lvlSpan, int range){
        EnchantmentInfo info = map.get(builder);
        if(info.ench == null)
            info.ench = new EnchantmentInfo.EnchantabilityCalc(minEnch, lvlSpan, range, MaxEnchantabilityMode.NORMAL);
    }

    @ZenMethod
    public static void setEnchantabilityCalc(EnchantmentBuilder builder, int minEnch, int lvlSpan, int range, String mode){
        EnchantmentInfo info = map.get(builder);
        if(info.ench == null)
            info.ench = new EnchantmentInfo.EnchantabilityCalc(minEnch, lvlSpan, range, MaxEnchantabilityMode.valueOf(mode));
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
        EnchantmentInfo info = map.get(builder);
        VanillaSystem systemEnum = VanillaSystem.valueOf(system);
        if(!info.vanillaSystemStrengths.containsKey(systemEnum))
            info.registerVanillaSystemOverride(systemEnum, multi);
    }

    @ZenMethod
    public static void setVanillaOverride(EnchantmentBuilder builder, String system, CT_SystemOverride override){
        EnchantmentInfo info = map.get(builder);
        VanillaSystem systemEnum = VanillaSystem.valueOf(system);
        if(!info.vanillaSystemStrengths.containsKey(systemEnum))
            info.registerVanillaSystemOverride(systemEnum, override);
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
