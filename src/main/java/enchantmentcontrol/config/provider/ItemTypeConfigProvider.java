package enchantmentcontrol.config.provider;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.compat.CompatUtil;
import enchantmentcontrol.compat.somanyenchantments.NewSMECompat;
import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.util.matchers.DefaultItemTypes;
import enchantmentcontrol.config.folders.ItemTypeConfig;
import enchantmentcontrol.util.enchantmenttypes.*;
import enchantmentcontrol.util.matchers.IMatcher;
import enchantmentcontrol.util.matchers.MatcherCreator;
import enchantmentcontrol.util.matchers.context.ItemTypeContext;
import enchantmentcontrol.util.matchers.matcher.InvertedMatcher;
import enchantmentcontrol.util.matchers.matcher.RegexMatcher;
import enchantmentcontrol.util.matchers.matcher.ExactStringMatcher;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class ItemTypeConfigProvider {
    private static final HashMap<String, ItemTypeMatcher> registeredMatchers = new LinkedHashMap<>();
    public static ItemTypeMatcher getMatcher(String name){
        return registeredMatchers.get(name);
    }

    public static void onResetConfig(){
        registeredMatchers.clear();
        itemTypes.clear();
        itemTypesAnvil.clear();
        blacklistedItems.clear();
        blacklistedEnchantments.clear();
        blacklistedEnchantmentsAnvil.clear();
        initRegisteredItemTypesFromConfig();
        initItemTypeConfig();
    }

    public static void registerCustomTypeMatcher(ItemTypeMatcher matcher){
        registeredMatchers.put(matcher.getName(), matcher);
    }

    // ---------------- INIT ----------------

    private static final Map<String, String> typeRenames = new HashMap<>();
    static {
        typeRenames.put("ALL", "ANY_TYPE");
        typeRenames.put("WEAPON", "SWORD");
        typeRenames.put("DIGGER", "TOOL");
    }
    private static final List<String> oldSMETypes = Arrays.asList("Combat Weapon", "Damageable", "Golden Apple", "Combat Tool", "Combat Axe", "Tool Axe", "Tool Pickaxe", "Tool Hoe", "Combat Sword", "Tool Shovel", "Combat Shield", "Combat", "All Tools", "All", "None");

    public static void initRegisteredItemTypesFromConfig(){
        for (EnumEnchantmentType registeredEnum : EnumEnchantmentType.values()){
            String enumName = registeredEnum.name();
            enumName = typeRenames.getOrDefault(enumName, enumName); // some vanilla names suck

            if (oldSMETypes.contains(enumName)) continue; //filter out additional enum types by old (pre 1.x) somanyenchantments

            EnumEnchantmentTypeMatcher oldMatcher = new EnumEnchantmentTypeMatcher(enumName, registeredEnum);
            registeredMatchers.put(enumName, oldMatcher);
        }

        // Register various default item types, mostly from vanilla EnumEnchantmentType
        for (DefaultItemTypes.DefaultType type : DefaultItemTypes.DefaultType.values()) {
            ItemTypeMatcher matcher = DefaultItemTypes.get(type);
            registeredMatchers.put(matcher.getName(), matcher);
        }

        // Create custom types and add them to the list
        for (Map.Entry<String, ItemTypeConfig.CustomItemType> entry : ConfigHandler.itemTypes.customTypes.entrySet()) {
            if(entry.getValue().values.isEmpty()) continue;

            String name = entry.getKey();
            ItemTypeMatcher matcher = new ItemTypeMatcher(name, MatcherCreator.ITEM_TYPE.createMatcher(entry.getValue().values, entry.getValue().type));

            registeredMatchers.put(name, matcher);
        }
    }

    public static void initItemTypeConfig() {
        initItemTypes(ConfigHandler.itemTypes.general.itemTypes, itemTypes);
        initItemTypes(ConfigHandler.itemTypes.anvil.itemTypes, itemTypesAnvil);

        initItemBlacklist(ConfigHandler.itemTypes.blacklist, blacklistedItems);
        initBlacklist(ConfigHandler.itemTypes.general.blacklist, blacklistedEnchantments);
        initBlacklist(ConfigHandler.itemTypes.anvil.blacklist, blacklistedEnchantmentsAnvil);
    }

    public static final Map<Enchantment, Set<ItemTypeMatcher>> itemTypes = new HashMap<>();
    public static final Map<Enchantment, Set<ItemTypeMatcher>> itemTypesAnvil = new HashMap<>();
    private static void initItemTypes(Map<String, ArrayList<String>> config, Map<Enchantment, Set<ItemTypeMatcher>> mapOut){
        for(Map.Entry<String, ArrayList<String>> entry : config.entrySet()){
            String typeName = entry.getKey().trim();
            boolean inverted = typeName.startsWith("!");
            if(inverted) typeName = typeName.substring(1);
            typeName = typeRenames.getOrDefault(typeName, typeName); // user config might have an old type name, treat internally as if it was renamed

            ItemTypeMatcher matcher = registeredMatchers.get(typeName);
            if(matcher == null){
                EnchantmentControl.LOGGER.warn("Could not find given item type while reading enchants per item type {}", typeName);
                continue;
            }
            if(inverted) {
                // Wrap the matcher in InvertedMatcher
                matcher = new ItemTypeMatcher("!"+typeName, MatcherCreator.ITEM_TYPE.createInvertedMatcher(matcher.getMatcher()));
            }

            for(String enchName : entry.getValue()){
                enchName = enchName.trim();
                if(enchName.isEmpty()) continue;
                Enchantment ench = Enchantment.getEnchantmentByLocation(enchName);
                if(ench == null){
                    EnchantmentControl.LOGGER.warn("Could not find enchantment {} while reading enchants per item type {}", enchName, typeName);
                    continue;
                }
                mapOut.computeIfAbsent(ench, k -> new HashSet<>()).add(matcher);
            }
        }
    }

    private static final Set<Item> blacklistedItems = new HashSet<>();
    private static final Set<Enchantment> blacklistedEnchantments = new HashSet<>();
    private static final Set<Enchantment> blacklistedEnchantmentsAnvil = new HashSet<>();
    private static void initBlacklist(List<String> cfg, Set<Enchantment> set) {
        cfg.stream()
                .map(String::trim)
                .map(Enchantment::getEnchantmentByLocation)
                .filter(Objects::nonNull)
                .forEach(set::add);
    }
    private static void initItemBlacklist(List<String> cfg, Set<Item> set) {
        cfg.stream()
                .map(String::trim)
                .map(Item::getByNameOrId)
                .filter(Objects::nonNull)
                .forEach(set::add);
    }

    // ---------------- RUNTIME ----------------

    public static boolean shouldYieldToModdedBehavior(Enchantment enchantment, boolean forAnvil){
        //config allows modded behavior in general
        boolean isGenerallyAllowed = forAnvil ? ConfigHandler.itemTypes.anvil.allowCustomEnchantments : ConfigHandler.itemTypes.general.allowCustomEnchantments;
        //or allows for this specific enchantment
        boolean enchantIsBlacklisted = (forAnvil ? blacklistedEnchantmentsAnvil : blacklistedEnchantments).contains(enchantment);

        //blacklisted usually false so
        // allowed + not blacklisted -> only runs original code (true)
        // not allowed + not blacklisted -> skips original code (false)
        return isGenerallyAllowed != enchantIsBlacklisted; //even if this returns true we might still run canItemApply in Enchantment.canApply/At
    }

    public static boolean shouldYieldToModdedBehavior(Item item){
        // allowed + not blacklisted -> only runs original code (true)
        // not allowed + not blacklisted -> skips original code (false)
        return ConfigHandler.itemTypes.allowCustomItems != blacklistedItems.contains(item);
    }

    public static boolean canItemApply(Enchantment enchantment, ItemStack stack, boolean forAnvil){
        Item item = stack.getItem();

        // Compute itemName lazily
        String itemName = null;

        //Each enchantment has a set of matchers which items can try to match against
        Set<ItemTypeMatcher> matchers = (forAnvil ? itemTypesAnvil : itemTypes).get(enchantment);
        if(matchers == null) return false;

        boolean isValid = false;
        boolean invertedMatches = false;

        for(ItemTypeMatcher matcher : matchers) {

            // Lazy compute itemName only if needed
            if(itemName == null && needsItemName(matcher.getMatcher())) {
                ResourceLocation loc = item.getRegistryName();
                itemName = (loc != null) ? loc.toString() : "";
            }

            ItemTypeContext context = new ItemTypeContext(enchantment, stack, item, itemName);
            boolean matches = matcher.matches(context);

            // Handle inversion
            if(matcher.getMatcher() instanceof InvertedMatcher) {
                invertedMatches = invertedMatches || matches;
            } else {
                isValid = isValid || matches;
            }
        }

        //Any inverted match makes this directly return false
        return isValid && !invertedMatches;
    }

    private static boolean needsItemName(IMatcher<ItemTypeContext> matcher) {
        // Check if matcher is one that uses itemName
        return matcher instanceof ExactStringMatcher ||
               matcher instanceof RegexMatcher ||
               (matcher instanceof InvertedMatcher && needsItemName(((InvertedMatcher<ItemTypeContext>) matcher).getInner()));
    }

    // ---------------- FIRST SETUP ----------------

    //This is just inference to try to match the original state during first setup
    public static void printDefaultItemTypes() {
        if(ConfigHandler.debug.printItemTypeBlacklists)
            printDefaultBlacklist();

        if(CompatUtil.somanyenchantments.isLoaded() && CompatUtil.versionInRange(CompatUtil.somanyenchantments, "[1.0.0,)"))
            NewSMECompat.addNewSMECustomTypes(); //as early as possible so others can override these. its mainly for having something available for the names

        Map<String, Set<Enchantment>> byName = new LinkedHashMap<>();
        Map<String, Set<Enchantment>> byNameAnvil = new LinkedHashMap<>();
        Map<Enchantment, Set<String>> byEnchantment = new HashMap<>(); //byEnchantment view of the map only exists to make the simplification easier
        Map<Enchantment, Set<String>> byEnchantmentAnvil = new HashMap<>();

        registeredMatchers.keySet().forEach(k -> byName.put(k, new LinkedHashSet<>())); //each matcher name gets at least an empty line MATCHER =
        //but anvil config doesn't get init with all types so it stays shorter

        //Note down each enchants original type
        for (Enchantment ench : Enchantment.REGISTRY) {
            if (ench.type == null) continue;
            List<ItemTypeMatcher> matchers = EnumEnchantmentTypeMatcher.byEnum(ench.type);
            matchers.forEach(matcher -> {
                if(matcher.getName().equals("NONE")) return; // If other mods use NONE enum
                byName.computeIfAbsent(matcher.getName(), k -> new HashSet<>()).add(ench);
                byEnchantment.computeIfAbsent(ench, k -> new HashSet<>()).add(matcher.getName());
            });
        }

        //Try to be smart, at least a little bit
        // Inferring applicability by offering a fakeStack to customItem.canApply-AtEnchantingTable(fakeStack)
        for (Map.Entry<String, ItemTypeMatcher> entry : registeredMatchers.entrySet()) {
            ItemStack fakeStack = entry.getValue().getFakeStack();
            if (fakeStack == null) continue; //the following only infers types using fake stacks

            Set<Enchantment> matchingEnchants = byName.get(entry.getKey());

            for (Enchantment ench : Enchantment.REGISTRY) {
                if (matchingEnchants.contains(ench)) continue; //already included by the original type check
                if (ench.canApplyAtEnchantingTable(fakeStack)) {
                    matchingEnchants.add(ench);
                    byEnchantment.computeIfAbsent(ench, k -> new HashSet<>()).add(entry.getKey());
                } else if (ench.canApply(fakeStack)) {
                    byNameAnvil.computeIfAbsent(entry.getKey(), k -> new LinkedHashSet<>()).add(ench);
                    byEnchantmentAnvil.computeIfAbsent(ench, k -> new HashSet<>()).add(entry.getKey());
                }
            }
        }
        if(CompatUtil.somanyenchantments.isLoaded() && CompatUtil.versionInRange(CompatUtil.somanyenchantments, "[1.0.0,)"))
            NewSMECompat.addNewSMETypes(byName, byEnchantment, byNameAnvil, byEnchantmentAnvil);

        simplify(byName, byEnchantment);
        simplify(byNameAnvil, byEnchantmentAnvil);

        //Write that down
        ConfigHandler.itemTypes.general.itemTypes = mapToConfigMap(byName);
        ConfigHandler.itemTypes.anvil.itemTypes = mapToConfigMap(byNameAnvil);

        //Reset print toggle
        ConfigHandler.dev.printTypes = false;

        EnchantmentControl.configNeedsSaving = true;
    }

    private static Map<String, ArrayList<String>> mapToConfigMap(Map<String, Set<Enchantment>> byName){
        Map<String, ArrayList<String>> out = new LinkedHashMap<>();
        byName.forEach((matcherName, enchs) -> {
            ArrayList<String> list = new ArrayList<>();
            enchs.stream()
                    .map(Enchantment::getRegistryName)
                    .filter(Objects::nonNull)
                    .map(ResourceLocation::toString)
                    .forEach(list::add);
            Collections.sort(list);
            out.put(matcherName, list);
        });
        return out;
    }

    private static void simplify(Map<String, Set<Enchantment>> byName, Map<Enchantment, Set<String>> byEnch) {
        for(Map.Entry<Enchantment, Set<String>> entry : byEnch.entrySet()){
            //All ARMOR types -> only ARMOR
            if(entry.getValue().contains("ARMOR_HEAD") && entry.getValue().contains("ARMOR_CHEST") && entry.getValue().contains("ARMOR_LEGS") &&  entry.getValue().contains("ARMOR_FEET")){
                byName.get("ARMOR_HEAD").remove(entry.getKey());
                byName.get("ARMOR_CHEST").remove(entry.getKey());
                byName.get("ARMOR_LEGS").remove(entry.getKey());
                byName.get("ARMOR_FEET").remove(entry.getKey());
                byName.computeIfAbsent("ARMOR", k -> new HashSet<>()).add(entry.getKey());
            }
            //Both PICKAXE and SHOVEL -> only TOOL
            if(entry.getValue().contains("PICKAXE") && entry.getValue().contains("SHOVEL")){
                byName.get("PICKAXE").remove(entry.getKey());
                byName.get("SHOVEL").remove(entry.getKey());
                byName.computeIfAbsent("TOOL", k -> new HashSet<>()).add(entry.getKey());
            }
            //DIGGER is TOOL
            if(entry.getValue().contains("DIGGER")){
                byName.get("DIGGER").remove(entry.getKey());
                byName.computeIfAbsent("TOOL", k -> new HashSet<>()).add(entry.getKey());
            }
            //WEAPON is SWORD
            if(entry.getValue().contains("WEAPON")) {
                byName.get("WEAPON").remove(entry.getKey());
                byName.computeIfAbsent("SWORD", k -> new HashSet<>()).add(entry.getKey());
            }
            //Both BATTLEAXE and WEAPON -> only WEAPON
            if(entry.getValue().contains("BATTLEAXE") && entry.getValue().contains("WEAPON")){
                byName.get("BATTLEAXE").remove(entry.getKey());
            }
            //Only use ANY as catchall
            if(entry.getValue().contains("ANY")) removeFromAllExcept(byName, entry.getKey(), "ANY");
            else if(entry.getValue().contains("ANY_TYPE") || entry.getValue().contains("ALL") || entry.getValue().contains("ALL_TYPES") || entry.getValue().contains("ALL_ITEMS")){
                //ALL is vanilla, ALL_TYPES and ALL_ITEMS is new SME
                removeFromAllExcept(byName, entry.getKey(), "ANY");
                byName.computeIfAbsent("ANY", k -> new HashSet<>()).add(entry.getKey());
            }
            //Vanilla:
            else if(entry.getValue().contains("BREAKABLE")) removeFromAllExcept(byName, entry.getKey(), "BREAKABLE");
            else if(entry.getValue().contains("WEARABLE")) removeFromAllExcept(byName, entry.getKey(), "WEARABLE");
        }

        //Remove enums that shouldn't be used for inference at all
        byName.remove("WEAPON");
        byName.remove("DIGGER");
        byName.remove("ANY_TYPE");
        byName.remove("ALL");
        byName.remove("ALL_TYPES");
        byName.remove("ALL_ITEMS");
    }

    private static void removeFromAllExcept(Map<String, Set<Enchantment>> validMatchers, Enchantment enchantment, String matcherName){
        validMatchers.forEach((k,v) -> {
            if(k.equals(matcherName)) return;
            v.remove(enchantment);
        });
    }

    public static boolean probe = false;
    public static boolean probeAnvil = false; //needs to be a second boolean cause canApplyAtEnch runs inside canApply
    private static void printDefaultBlacklist() {
        List<String> itemBlacklist = new ArrayList<>();
        List<String> blacklist = new ArrayList<>();
        List<String> blacklistAnvil = new ArrayList<>();

        for(Enchantment enchantment : Enchantment.REGISTRY){
            ResourceLocation loc = enchantment.getRegistryName();
            if(loc == null) continue;

            probe = false;
            enchantment.canApplyAtEnchantingTable(ItemStack.EMPTY);
            if(probe) blacklist.add(loc.toString());

            probeAnvil = false;
            enchantment.canApply(ItemStack.EMPTY);
            if(probeAnvil) blacklistAnvil.add(loc.toString());
        }

        for(Item item : Item.REGISTRY){
            ResourceLocation loc = item.getRegistryName();
            if(loc == null) continue;

            boolean hasOverride = false;
            try {
                hasOverride = !Item.class.getName().equals(item.getClass().getMethod("canApplyAtEnchantingTable", ItemStack.class, Enchantment.class).getDeclaringClass().getName());
            } catch (Exception ignored){}
            if(hasOverride) itemBlacklist.add(loc.toString());
        }

        ConfigHandler.itemTypes.general.blacklist = blacklist;
        ConfigHandler.itemTypes.general.allowCustomEnchantments = false;

        ConfigHandler.itemTypes.anvil.blacklist = blacklistAnvil;
        ConfigHandler.itemTypes.anvil.allowCustomEnchantments = false;

        ConfigHandler.itemTypes.blacklist = itemBlacklist;
        ConfigHandler.itemTypes.allowCustomItems = false;
    }
}

