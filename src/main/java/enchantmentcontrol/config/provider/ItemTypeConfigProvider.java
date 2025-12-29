package enchantmentcontrol.config.provider;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.util.ConfigRef;
import enchantmentcontrol.util.enchantmenttypes.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class ItemTypeConfigProvider {
    private static final HashMap<String, ITypeMatcher> registeredMatchers = new HashMap<>();
    public static ITypeMatcher getMatcher(String name){
        return registeredMatchers.get(name);
    }

    public static void onResetConfig(){
        registeredMatchers.clear();
        itemTypes.clear();
        itemTypesAnvil.clear();
        blacklistedEnchantments.clear();
        blacklistedEnchantmentsAnvil.clear();
        initRegisteredItemTypesFromConfig();
        initItemTypeConfig();
    }

    public static void registerCustomTypeMatcher(ITypeMatcher matcher){
        registeredMatchers.put(matcher.getName(), matcher);
    }

    // ---------------- INIT ----------------

    public static void initRegisteredItemTypesFromConfig(){
        registeredMatchers.put("ANY_TYPE", new EnumEnchantmentTypeMatcher("ANY_TYPE", EnumEnchantmentType.ALL));
        registeredMatchers.put("ARMOR", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.ARMOR));
        registeredMatchers.put("ARMOR_HEAD", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.ARMOR_HEAD));
        registeredMatchers.put("ARMOR_CHEST", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.ARMOR_CHEST));
        registeredMatchers.put("ARMOR_LEGS", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.ARMOR_LEGS));
        registeredMatchers.put("ARMOR_FEET", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.ARMOR_FEET));
        registeredMatchers.put("SWORD", new EnumEnchantmentTypeMatcher("SWORD", EnumEnchantmentType.WEAPON));
        registeredMatchers.put("TOOL", new EnumEnchantmentTypeMatcher("TOOL", EnumEnchantmentType.DIGGER));
        registeredMatchers.put("FISHING_ROD", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.FISHING_ROD));
        registeredMatchers.put("BREAKABLE", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.BREAKABLE));
        registeredMatchers.put("BOW", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.BOW));
        registeredMatchers.put("WEARABLE", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.WEARABLE));

        registeredMatchers.put("ANY", new BooleanTypeMatcher("ANY", true));
        registeredMatchers.put("AXE", new InstanceofTypeMatcher("AXE", ItemAxe.class, Items.IRON_AXE));
        registeredMatchers.put("PICKAXE", new InstanceofTypeMatcher("PICKAXE", ItemPickaxe.class, Items.IRON_PICKAXE));
        registeredMatchers.put("HOE", new InstanceofTypeMatcher("HOE", ItemHoe.class, Items.IRON_HOE));
        registeredMatchers.put("SHOVEL", new InstanceofTypeMatcher("SHOVEL", ItemSpade.class, Items.IRON_SHOVEL));
        registeredMatchers.put("SHIELD", new InstanceofTypeMatcher("SHIELD", ItemShield.class, Items.SHIELD));
        registeredMatchers.put("NONE", new BooleanTypeMatcher("NONE", false));

        for (String s : ConfigHandler.itemTypes.customTypes) {
            String[] split = s.split(EnchantmentControl.SEP);

            String type;
            if (split.length < 2) {
                EnchantmentControl.LOGGER.warn("Invalid custom item type definition, skipping: {}", s);
                continue;
            } else if(split.length == 2) type = "regex_default";
            else type = split[1].trim();

            String name = split[0].trim();

            ITypeMatcher matcher;
            switch (type) {
                case "class" : matcher = new InstanceofTypeMatcher(name, split[2].trim()); break;
                case "modid" : matcher = new ModidMatcher(name, split[2].trim()); break;
                case "items" : matcher = new ListMatcher(name, Arrays.copyOfRange(split, 2, split.length)); break;
                case "regex" : matcher = new CustomTypeMatcher(name, split[2].trim()); break;
                case "enum"  : matcher = new EnumEnchantmentTypeMatcher(EnumEnchantmentType.valueOf(name)); break;
                default      : matcher = new CustomTypeMatcher(name, split[1].trim()); //split[1] cause this is the default where no type was named
            }
            if (matcher.isValid()) registeredMatchers.put(name, matcher);
        }
    }

    public static void initItemTypeConfig() {
        initItemTypes(ConfigHandler.itemTypes.general.itemTypes, itemTypes);
        initItemTypes(ConfigHandler.itemTypes.anvil.itemTypes, itemTypesAnvil);

        initBlacklist(ConfigHandler.itemTypes.general.blacklist, blacklistedEnchantments);
        initBlacklist(ConfigHandler.itemTypes.anvil.blacklist, blacklistedEnchantmentsAnvil);
    }

    public static final Map<Enchantment, Set<ITypeMatcher>> itemTypes = new HashMap<>();
    public static final Map<Enchantment, Set<ITypeMatcher>> itemTypesAnvil = new HashMap<>();
    private static void initItemTypes(String[] config, Map<Enchantment, Set<ITypeMatcher>> mapOut){
        for(String s : config){
            String[] split = s.split("=");
            if(split.length < 2) continue;

            String typeName = split[0].trim();

            boolean inverted = typeName.startsWith("!");
            if(inverted) typeName = typeName.substring(1);

            ITypeMatcher matcher = registeredMatchers.get(typeName);
            if(matcher == null){
                EnchantmentControl.LOGGER.warn("Could not find given item type while reading enchants per item type {}", typeName);
                continue;
            }
            if(inverted) matcher = new InvertedTypeMatcher(matcher);

            for(String enchName : split[1].split(EnchantmentControl.SEP)){
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

    private static final Set<Enchantment> blacklistedEnchantments = new HashSet<>();
    private static final Set<Enchantment> blacklistedEnchantmentsAnvil = new HashSet<>();
    private static void initBlacklist(String[] cfg, Set<Enchantment> set) {
        Arrays.stream(cfg)
                .map(String::trim)
                .map(Enchantment::getEnchantmentByLocation)
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

    public static boolean canItemApply(Enchantment enchantment, ItemStack stack, boolean forAnvil){
        Item item = stack.getItem();
        boolean isValid = false;
        boolean invertedMatches = false;
        String itemName = null;

        //Each enchantment has a set of matchers which items can try to match against
        Set<ITypeMatcher> matchers = (forAnvil ? itemTypesAnvil : itemTypes).get(enchantment);
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

        //Any inverted match makes this directly return false
        return isValid && !invertedMatches;
    }

    // ---------------- FIRST SETUP ----------------

    //This is just inference to try to match the original state during first setup
    public static void printDefaultItemTypes() {
        if(ConfigHandler.debug.printItemTypeBlacklists)
            printDefaultBlacklist();

        Map<String, Set<Enchantment>> byName = new HashMap<>();
        Map<String, Set<Enchantment>> byNameAnvil = new HashMap<>();
        Map<Enchantment, Set<String>> byEnchantment = new HashMap<>(); //byEnchantment view of the map only exists to make the simplification easier
        Map<Enchantment, Set<String>> byEnchantmentAnvil = new HashMap<>();

        registeredMatchers.keySet().forEach(k -> byName.put(k, new LinkedHashSet<>())); //each matcher name gets at least an empty line MATCHER =
        //anvil config doesn't get init with all types so it stays shorter

        //Note down each enchants original type
        for (Enchantment ench : Enchantment.REGISTRY) {
            if (ench.type == null) continue;
            List<ITypeMatcher> matchers = EnumEnchantmentTypeMatcher.byEnum(ench.type);
            matchers.forEach(matcher -> {
                byName.computeIfAbsent(matcher.getName(), k -> new HashSet<>()).add(ench);
                byEnchantment.computeIfAbsent(ench, k -> new HashSet<>()).add(matcher.getName());
            });
        }

        //Try to be smart, at least a little bit
        for (Map.Entry<String, ITypeMatcher> entry : registeredMatchers.entrySet()) {
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
        simplify(byName, byEnchantment);
        simplify(byNameAnvil, byEnchantmentAnvil);

        //Write that down

        List<String> out = new ArrayList<>();
        byName.forEach((matcherName, enchs) ->
                out.add(
                        matcherName + " = "
                        + String.join(
                        EnchantmentControl.SEP + " ",
                                enchs.stream()
                                    .map(Enchantment::getRegistryName)
                                    .filter(Objects::nonNull)
                                    .map(ResourceLocation::toString)
                                    .toArray(String[]::new)
                        )
                )
        );
        String[] outarr = out.toArray(new String[0]);
        EnchantmentControl.CONFIG.get("general.item types.general", "Item Types", ConfigHandler.itemTypes.general.itemTypes).set(outarr);
        ConfigHandler.itemTypes.general.itemTypes = outarr;

        //Also for anvil

        List<String> outAnv = new ArrayList<>();
        byNameAnvil.forEach((matcherName, enchs) -> {
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
        EnchantmentControl.CONFIG.get("general.item types.anvil", "Item Types", ConfigHandler.itemTypes.anvil.itemTypes).set(outarrAnv);
        ConfigHandler.itemTypes.anvil.itemTypes = outarrAnv;

        //Reset print toggle

        EnchantmentControl.CONFIG.get("general.first setup", ConfigRef.PRINT_TYPES_CONFIG_NAME, ConfigHandler.dev.printTypes).set(false);
        ConfigHandler.dev.printTypes = false;
        EnchantmentControl.configNeedsSaving = true;
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
            if(entry.getValue().contains("ANY")) removeFromAllExcept(byName, entry.getKey(), "ANY");
            else if(entry.getValue().contains("ANY_TYPE")) removeFromAllExcept(byName, entry.getKey(), "ANY_TYPE");
            else if(entry.getValue().contains("BREAKABLE")) removeFromAllExcept(byName, entry.getKey(), "BREAKABLE");
            else if(entry.getValue().contains("WEARABLE")) removeFromAllExcept(byName, entry.getKey(), "WEARABLE");
        }
    }

    private static void removeFromAllExcept(Map<String, Set<Enchantment>> validMatchers, Enchantment enchantment, String matcherName){
        validMatchers.forEach((k,v) -> {
            if(k.equals(matcherName)) return;
            v.remove(enchantment);
        });
    }

    public static boolean probe = false;
    public static boolean probeAnvil = false;
    private static void printDefaultBlacklist() {
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

        String[] outarr = blacklist.toArray(new String[0]);
        EnchantmentControl.CONFIG.get("general.item types.general", "Blacklist", ConfigHandler.itemTypes.general.blacklist).set(outarr);
        ConfigHandler.itemTypes.general.blacklist = outarr;
        EnchantmentControl.CONFIG.get("general.item types.general", "Allow Modded Enchantment Behaviors", ConfigHandler.itemTypes.general.allowCustomEnchantments).set(false);
        ConfigHandler.itemTypes.general.allowCustomEnchantments = false;

        String[] outarrAnv = blacklistAnvil.toArray(new String[0]);
        EnchantmentControl.CONFIG.get("general.item types.anvil", "Blacklist", ConfigHandler.itemTypes.anvil.blacklist).set(outarrAnv);
        ConfigHandler.itemTypes.anvil.blacklist = outarrAnv;
        EnchantmentControl.CONFIG.get("general.item types.anvil", "Allow Modded Enchantment Behaviors", ConfigHandler.itemTypes.anvil.allowCustomEnchantments).set(false);
        ConfigHandler.itemTypes.anvil.allowCustomEnchantments = false;
    }
}

