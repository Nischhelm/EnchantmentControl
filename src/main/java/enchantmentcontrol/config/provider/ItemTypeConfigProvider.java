package enchantmentcontrol.config.provider;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.util.ConfigRef;
import enchantmentcontrol.util.enchantmenttypes.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class ItemTypeConfigProvider {
    private static final HashMap<String, ITypeMatcher> typeMatchers = new HashMap<>();

    public static void onResetConfig(){
        typeMatchers.clear();
        enchantToTypeMatchers.clear();
        enchantToTypeMatchersAnvil.clear();
        supportedEnchantments.clear();
        supportedEnchantmentsAnvil.clear();
        readItemTypesFromConfig();
        readItemTypeMappingsFromConfig();
    }

    public static void readItemTypesFromConfig(){
        typeMatchers.put("ANY_TYPE", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.ALL));
        typeMatchers.put("ARMOR", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.ARMOR));
        typeMatchers.put("ARMOR_HEAD", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.ARMOR_HEAD));
        typeMatchers.put("ARMOR_CHEST", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.ARMOR_CHEST));
        typeMatchers.put("ARMOR_LEGS", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.ARMOR_LEGS));
        typeMatchers.put("ARMOR_FEET", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.ARMOR_FEET));
        typeMatchers.put("SWORD", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.WEAPON));
        typeMatchers.put("TOOL", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.DIGGER));
        typeMatchers.put("FISHING_ROD", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.FISHING_ROD));
        typeMatchers.put("BREAKABLE", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.BREAKABLE));
        typeMatchers.put("BOW", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.BOW));
        typeMatchers.put("WEARABLE", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.WEARABLE));

        typeMatchers.put("ANY", new BooleanTypeMatcher("ANY", true));
        typeMatchers.put("AXE", new InstanceofTypeMatcher("AXE", ItemAxe.class, Items.IRON_AXE));
        typeMatchers.put("PICKAXE", new InstanceofTypeMatcher("PICKAXE", ItemPickaxe.class, Items.IRON_PICKAXE));
        typeMatchers.put("HOE", new InstanceofTypeMatcher("HOE", ItemHoe.class, Items.IRON_HOE));
        typeMatchers.put("SHOVEL", new InstanceofTypeMatcher("SHOVEL", ItemSpade.class, Items.IRON_SHOVEL));
        typeMatchers.put("SHIELD", new InstanceofTypeMatcher("SHIELD", ItemShield.class, Items.SHIELD));
        typeMatchers.put("NONE", new BooleanTypeMatcher("NONE", false));

        for (String s : ConfigHandler.itemTypes.customTypes) {
            String[] split = s.split(EnchantmentControl.SEP);
            String type;
            if (split.length < 2) {
                EnchantmentControl.LOGGER.warn("Invalid custom item type definition, skipping: {}", s);
                continue;
            } else if(split.length == 2) type = "regex_def";
            else type = split[1].trim();

            ITypeMatcher matcher;
            switch (type) {
                case "class" :
                    matcher = new InstanceofTypeMatcher(split[2].trim());
                    if(matcher.isValid()) typeMatchers.put(split[0].trim(), matcher);
                    break;
                case "modid" :
                    matcher = new ModidMatcher(split[0].trim(), split[2].trim());
                    if(matcher.isValid()) typeMatchers.put(split[0].trim(), matcher);
                    break;
                case "items" :
                    matcher = new ListMatcher(split);
                    if (matcher.isValid()) typeMatchers.put(split[0].trim(), matcher);
                    break;
                case "regex" :
                    matcher = new CustomTypeMatcher(split[0].trim(), split[2].trim());
                    if (matcher.isValid()) typeMatchers.put(split[0].trim(), matcher);
                    break;
                default:
                    matcher = new CustomTypeMatcher(split[0].trim(), split[1].trim());
                    if (matcher.isValid()) typeMatchers.put(split[0].trim(), matcher);
                    break;
            }
        }
    }

    public static void readItemTypeMappingsFromConfig() {
        initEnchantmentToMatchersMap(ConfigHandler.itemTypes.itemTypes, supportedEnchantments, enchantToTypeMatchers);
        initEnchantmentToMatchersMap(ConfigHandler.itemTypes.itemTypesAnvil, supportedEnchantmentsAnvil, enchantToTypeMatchersAnvil);
    }

    private static void initEnchantmentToMatchersMap(String[] config, Set<Enchantment> suppEnch, Map<Enchantment, Set<ITypeMatcher>> mapOut){
        for(String s : config){
            String[] split = s.split("=");
            if(split.length < 2) continue;

            String typeName = split[0].trim();

            boolean inverted = typeName.startsWith("!");
            if(inverted) typeName = typeName.substring(1);

            ITypeMatcher matcher = typeMatchers.get(typeName);
            if(matcher == null){
                EnchantmentControl.LOGGER.warn("Could not find given item type while reading enchants per item type {}", typeName);
                continue;
            }
            if(inverted) matcher = new InvertedTypeMatcher(matcher); //TODO: does that work? uniqueness and shit

            for(String enchName : split[1].split(EnchantmentControl.SEP)){
                enchName = enchName.trim();
                Enchantment ench = Enchantment.getEnchantmentByLocation(enchName);
                if(ench == null){
                    EnchantmentControl.LOGGER.warn("Could not find enchantment {} while reading enchants per item type {}", enchName, typeName);
                    continue;
                }
                suppEnch.add(ench);
                mapOut.computeIfAbsent(ench, k -> new HashSet<>()).add(matcher);
            }
        }
    }

    private static final Set<Enchantment> supportedEnchantments = new HashSet<>();
    private static final Set<Enchantment> supportedEnchantmentsAnvil = new HashSet<>();
    private static final Map<Enchantment, Set<ITypeMatcher>> enchantToTypeMatchers = new HashMap<>();
    private static final Map<Enchantment, Set<ITypeMatcher>> enchantToTypeMatchersAnvil = new HashMap<>();

    public static boolean isSupported(Enchantment enchantment, boolean forAnvil){
        return forAnvil ? supportedEnchantmentsAnvil.contains(enchantment) : supportedEnchantments.contains(enchantment);
    }

    public static boolean canItemApply(Enchantment enchantment, ItemStack stack, boolean forAnvil){
        Item item = stack.getItem();
        boolean isValid = false;
        boolean invertedMatches = false;
        String itemName = null;

        Set<ITypeMatcher> matchers = forAnvil ? enchantToTypeMatchersAnvil.get(enchantment) : enchantToTypeMatchers.get(enchantment);
        if(matchers == null) return false;

        for(ITypeMatcher typeMatcher: matchers){
            //Configs can list types starting with ! to disable those
            boolean inverted = typeMatcher instanceof InvertedTypeMatcher;

            //First time check of a custom type: get item name
            if(typeMatcher instanceof ITypeMatcher.UsesItemLoc && itemName == null) {
                ResourceLocation loc = item.getRegistryName();
                if (loc != null) itemName = loc.toString();
                else itemName = ""; //edge case shouldn't match anything
            }

            boolean matches = typeMatcher.matches(enchantment, stack, item, itemName);

            if(!inverted) isValid = isValid || matches;
            else invertedMatches = invertedMatches || matches;
        }
        if(enchantment == Enchantments.RESPIRATION && item == Items.IRON_CHESTPLATE){
            EnchantmentControl.LOGGER.info("Trying sharp on iron hoe {} {} {}", isValid, invertedMatches, matchers.size());
        }
        return isValid && !invertedMatches;
    }

    //This is just inference to try to match the original state during first setup
    public static void writeDefaultItemTypes() {
        Map<String, List<Enchantment>> validMatchers = new HashMap<>();
        Map<String, List<Enchantment>> validMatchersAnvil = new HashMap<>();
        Map<Enchantment, Set<String>> enchantToTypeMatchers = new HashMap<>();
        Map<Enchantment, Set<String>> enchantToTypeMatchersAnvil = new HashMap<>();

        typeMatchers.keySet().forEach(k -> validMatchers.put(k, new ArrayList<>()));
        //anvil config doesnt get init with all types so it stays shorter

        for (Map.Entry<String, ITypeMatcher> entry : typeMatchers.entrySet()) {
            ItemStack fakeStack = entry.getValue().getFakeStack();
            if(fakeStack == null) continue;
            Item item = fakeStack.getItem();
            ResourceLocation loc = item.getRegistryName();
            if(loc == null) continue;
            for (Enchantment ench : Enchantment.REGISTRY) {
                if(ench.canApplyAtEnchantingTable(fakeStack)){
                    validMatchers.get(entry.getKey()).add(ench);
                    enchantToTypeMatchers.computeIfAbsent(ench, k -> new HashSet<>()).add(entry.getKey());
                }
                else if(ench.canApply(fakeStack)){
                    validMatchersAnvil.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(ench);
                    enchantToTypeMatchersAnvil.computeIfAbsent(ench, k -> new HashSet<>()).add(entry.getKey());
                }
            }
        }

        simplify(validMatchers, enchantToTypeMatchers);
        List<String> out = new ArrayList<>();
        validMatchers.forEach((matcherName, enchs) -> out.add(matcherName + " = " + String.join(EnchantmentControl.SEP + " ", enchs.stream().map(Enchantment::getRegistryName).filter(Objects::nonNull).map(ResourceLocation::toString).toArray(String[]::new))));
        String[] outarr = out.toArray(new String[0]);
        EnchantmentControl.CONFIG.get("general.item types", "Item Types", ConfigHandler.itemTypes.itemTypes).set(outarr);
        ConfigHandler.itemTypes.itemTypes = outarr;

        simplify(validMatchersAnvil, enchantToTypeMatchersAnvil);

        List<String> outAnv = new ArrayList<>();
        validMatchersAnvil.forEach((matcherName, enchs) -> {
                if(enchs.isEmpty()) return;
                outAnv.add(
                        matcherName + " = " + String.join(
                        EnchantmentControl.SEP + " ",
                                enchs.stream()
                                    .map(Enchantment::getRegistryName)
                                    .filter(Objects::nonNull)
                                    .map(ResourceLocation::toString)
                                    .toArray(String[]::new)
                        )
                );
            }
        );
        String[] outarrAnv = outAnv.toArray(new String[0]);
        EnchantmentControl.CONFIG.get("general.item types", "Item Types Anvil", ConfigHandler.itemTypes.itemTypesAnvil).set(outarrAnv);
        ConfigHandler.itemTypes.itemTypesAnvil = outarrAnv;

        EnchantmentControl.CONFIG.get("general.first setup", ConfigRef.PRINT_TYPES_CONFIG_NAME, ConfigHandler.dev.readTypes).set(false);
        ConfigHandler.dev.readTypes = false;
        EnchantmentControl.configNeedsSaving = true;
    }

    private static void simplify(Map<String, List<Enchantment>> validMatchers, Map<Enchantment, Set<String>> enchantToTypeMatchers) {
        for(Map.Entry<Enchantment, Set<String>> entry : enchantToTypeMatchers.entrySet()){
            //All ARMOR types -> only ARMOR
            if(entry.getValue().contains("ARMOR_HEAD") && entry.getValue().contains("ARMOR_CHEST") && entry.getValue().contains("ARMOR_LEGS") &&  entry.getValue().contains("ARMOR_FEET")){
                validMatchers.get("ARMOR_HEAD").remove(entry.getKey());
                validMatchers.get("ARMOR_CHEST").remove(entry.getKey());
                validMatchers.get("ARMOR_LEGS").remove(entry.getKey());
                validMatchers.get("ARMOR_FEET").remove(entry.getKey());
                validMatchers.computeIfAbsent("ARMOR", k -> new ArrayList<>()).add(entry.getKey());
            }
            //Both PICKAXE and SHOVEL -> only TOOL
            if(entry.getValue().contains("PICKAXE") && entry.getValue().contains("SHOVEL")){
                validMatchers.get("PICKAXE").remove(entry.getKey());
                validMatchers.get("SHOVEL").remove(entry.getKey());
                //already added to TOOL
            }
        }
    }
}
