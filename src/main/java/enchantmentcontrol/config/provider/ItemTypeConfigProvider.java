package enchantmentcontrol.config.provider;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.compat.CompatUtil;
import enchantmentcontrol.compat.somanyenchantments.NewSMECompat;
import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.config.folders.ItemTypeConfig;
import enchantmentcontrol.util.enchantmenttypes.*;
import enchantmentcontrol.util.matcher.IMatcher;
import enchantmentcontrol.util.matcher.matcher.InvertedMatcher;
import enchantmentcontrol.util.matcher.matcher.ModIdMatcher;
import enchantmentcontrol.util.matcher.matcher.RegexMatcher;
import enchantmentcontrol.util.matcher.context.ItemTypeContext;
import enchantmentcontrol.util.matcher.context.ItemTypeMatcherRegistry;
import enchantmentcontrol.util.matcher.matcher.StringListMatcher;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;

import java.util.*;
import java.util.stream.Collectors;

public class ItemTypeConfigProvider {
    private static final HashMap<String, ItemTypeMatcherRegistry> registeredMatchers = new HashMap<>();
    public static ITypeMatcher getMatcher(String name){
        ItemTypeMatcherRegistry registry = registeredMatchers.get(name);
        if(registry == null) return null;

        return new ITypeMatcher() {
            @Override
            public boolean matches(Enchantment enchantment, ItemStack stack, Item item, String itemName) {
                return registry.getMatcher().matches(new ItemTypeContext(enchantment, stack, item, itemName));
            }

            @Override
            public String getName() {
                return registry.getName();
            }

            @Override
            public boolean isValid() {
                return registry.isValid();
            }

            @Override
            public ItemStack getFakeStack() {
                return registry.getFakeStack();
            }
        };
    }

    public static ItemTypeMatcherRegistry getMatcherRegistry(String name){
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

    public static void registerCustomTypeMatcher(ITypeMatcher matcher){
        ItemTypeMatcherRegistry registry = new ItemTypeMatcherRegistry(
            matcher.getName(),
            ctx -> matcher.matches(ctx.getEnchantment(), ctx.getStack(), ctx.getItem(), ctx.getItemName()),
            matcher::isValid,
            matcher.getFakeStack()
        );
        registeredMatchers.put(matcher.getName(), registry);
    }

    public static ITypeMatcher createRegexMatcher(String name, Set<String> regexes) {
        ItemTypeMatcherRegistry registry = createRegistration(
            name,
            new RegexMatcher<>(regexes, ItemTypeContext::getItemName),
            () -> !regexes.isEmpty(),
            null
        );
        registeredMatchers.put(name, registry);
        return getMatcher(name);
    }

    private static ItemTypeMatcherRegistry createRegistration(String name, IMatcher<ItemTypeContext> matcher, java.util.function.Supplier<Boolean> isValid, ItemStack fakeStack) {
        return new ItemTypeMatcherRegistry(name, matcher, isValid, fakeStack);
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
            ItemTypeMatcherRegistry registry = new ItemTypeMatcherRegistry(
                oldMatcher.getName(),
                ctx -> oldMatcher.matches(ctx.getEnchantment(), ctx.getStack(), ctx.getItem(), ctx.getItemName()),
                oldMatcher::isValid,
                oldMatcher.getFakeStack()
            );
            registeredMatchers.put(enumName, registry);
        }

        // Register boolean and instanceof matchers
        BooleanTypeMatcher anyMatcher = new BooleanTypeMatcher("ANY", true);
        registeredMatchers.put("ANY", new ItemTypeMatcherRegistry(
            anyMatcher.getName(),
            ctx -> anyMatcher.matches(ctx.getEnchantment(), ctx.getStack(), ctx.getItem(), ctx.getItemName()),
            anyMatcher::isValid,
            anyMatcher.getFakeStack()
        ));

        InstanceofTypeMatcher axeMatcher = new InstanceofTypeMatcher("AXE", ItemAxe.class, Items.IRON_AXE);
        registeredMatchers.put("AXE", new ItemTypeMatcherRegistry(
            axeMatcher.getName(),
            ctx -> axeMatcher.matches(ctx.getEnchantment(), ctx.getStack(), ctx.getItem(), ctx.getItemName()),
            axeMatcher::isValid,
            axeMatcher.getFakeStack()
        ));

        InstanceofTypeMatcher pickaxeMatcher = new InstanceofTypeMatcher("PICKAXE", ItemPickaxe.class, Items.IRON_PICKAXE);
        registeredMatchers.put("PICKAXE", new ItemTypeMatcherRegistry(
            pickaxeMatcher.getName(),
            ctx -> pickaxeMatcher.matches(ctx.getEnchantment(), ctx.getStack(), ctx.getItem(), ctx.getItemName()),
            pickaxeMatcher::isValid,
            pickaxeMatcher.getFakeStack()
        ));

        InstanceofTypeMatcher hoeMatcher = new InstanceofTypeMatcher("HOE", ItemHoe.class, Items.IRON_HOE);
        registeredMatchers.put("HOE", new ItemTypeMatcherRegistry(
            hoeMatcher.getName(),
            ctx -> hoeMatcher.matches(ctx.getEnchantment(), ctx.getStack(), ctx.getItem(), ctx.getItemName()),
            hoeMatcher::isValid,
            hoeMatcher.getFakeStack()
        ));

        InstanceofTypeMatcher shovelMatcher = new InstanceofTypeMatcher("SHOVEL", ItemSpade.class, Items.IRON_SHOVEL);
        registeredMatchers.put("SHOVEL", new ItemTypeMatcherRegistry(
            shovelMatcher.getName(),
            ctx -> shovelMatcher.matches(ctx.getEnchantment(), ctx.getStack(), ctx.getItem(), ctx.getItemName()),
            shovelMatcher::isValid,
            shovelMatcher.getFakeStack()
        ));

        InstanceofTypeMatcher shieldMatcher = new InstanceofTypeMatcher("SHIELD", ItemShield.class, Items.SHIELD);
        registeredMatchers.put("SHIELD", new ItemTypeMatcherRegistry(
            shieldMatcher.getName(),
            ctx -> shieldMatcher.matches(ctx.getEnchantment(), ctx.getStack(), ctx.getItem(), ctx.getItemName()),
            shieldMatcher::isValid,
            shieldMatcher.getFakeStack()
        ));

        InstanceofTypeMatcher shearsMatcher = new InstanceofTypeMatcher("SHEARS", ItemShears.class, Items.SHEARS);
        registeredMatchers.put("SHEARS", new ItemTypeMatcherRegistry(
            shearsMatcher.getName(),
            ctx -> shearsMatcher.matches(ctx.getEnchantment(), ctx.getStack(), ctx.getItem(), ctx.getItemName()),
            shearsMatcher::isValid,
            shearsMatcher.getFakeStack()
        ));

        BooleanTypeMatcher noneMatcher = new BooleanTypeMatcher("NONE", false);
        registeredMatchers.put("NONE", new ItemTypeMatcherRegistry(
            noneMatcher.getName(),
            ctx -> noneMatcher.matches(ctx.getEnchantment(), ctx.getStack(), ctx.getItem(), ctx.getItemName()),
            noneMatcher::isValid,
            noneMatcher.getFakeStack()
        ));

        for (Map.Entry<String, ItemTypeConfig.CustomItemType> entry : ConfigHandler.itemTypes.customTypes.entrySet()) {
            String name = entry.getKey();
            Set<String> values = entry.getValue().values;

            ItemTypeMatcherRegistry registry;
            switch (entry.getValue().type) {
                case CLASS:
                    InstanceofTypeMatcher classMatcher = new InstanceofTypeMatcher(name, values.stream().map(String::trim).collect(Collectors.toList()));
                    registry = new ItemTypeMatcherRegistry(
                        classMatcher.getName(),
                        ctx -> classMatcher.matches(ctx.getEnchantment(), ctx.getStack(), ctx.getItem(), ctx.getItemName()),
                        classMatcher::isValid,
                        classMatcher.getFakeStack()
                    );
                    break;
                case MODID:
                    registry = createRegistration(
                        name,
                        new ModIdMatcher<>(values, ctx -> {
                            if (ctx.getItem().getRegistryName() == null) return null;
                            return ctx.getItem().getRegistryName().getNamespace();
                        }),
                        () -> values.stream().anyMatch(net.minecraftforge.fml.common.Loader::isModLoaded),
                        null
                    );
                    break;
                case ITEMID:
                    Set<String> trimmed = values.stream().map(String::trim).collect(Collectors.toSet());
                    registry = createRegistration(
                        name,
                        new StringListMatcher<>(trimmed, ItemTypeContext::getItemName),
                        () -> !trimmed.isEmpty(),
                        null
                    );
                    break;
                case REGEX:
                default:
                    registry = createRegistration(
                        name,
                        new RegexMatcher<>(values, ItemTypeContext::getItemName),
                        () -> !values.isEmpty(),
                        null
                    );
                    break;
            }
            if (registry.isValid()) registeredMatchers.put(name, registry);
        }
    }

    public static void initItemTypeConfig() {
        initItemTypes(ConfigHandler.itemTypes.general.itemTypes, itemTypes);
        initItemTypes(ConfigHandler.itemTypes.anvil.itemTypes, itemTypesAnvil);

        initItemBlacklist(ConfigHandler.itemTypes.blacklist, blacklistedItems);
        initBlacklist(ConfigHandler.itemTypes.general.blacklist, blacklistedEnchantments);
        initBlacklist(ConfigHandler.itemTypes.anvil.blacklist, blacklistedEnchantmentsAnvil);
    }

    public static final Map<Enchantment, Set<ItemTypeMatcherRegistry>> itemTypes = new HashMap<>();
    public static final Map<Enchantment, Set<ItemTypeMatcherRegistry>> itemTypesAnvil = new HashMap<>();
    private static void initItemTypes(List<String> config, Map<Enchantment, Set<ItemTypeMatcherRegistry>> mapOut){
        for(String s : config){
            String[] split = s.split("=");
            if(split.length < 2) continue;

            String typeName = split[0].trim();

            boolean inverted = typeName.startsWith("!");
            if(inverted) typeName = typeName.substring(1);
            typeName = typeRenames.getOrDefault(typeName, typeName); // user config might have an old type name, treat internally as if it was renamed

            ItemTypeMatcherRegistry registry = registeredMatchers.get(typeName);
            if(registry == null){
                EnchantmentControl.LOGGER.warn("Could not find given item type while reading enchants per item type {}", typeName);
                continue;
            }
            if(inverted) {
                // Wrap the matcher in InvertedMatcher
                IMatcher<ItemTypeContext> invertedMatcher = new InvertedMatcher<>(registry.getMatcher());
                registry = new ItemTypeMatcherRegistry(
                    "inverted",
                    invertedMatcher,
                    registry::isValid,
                    registry.getFakeStack()
                );
            }

            for(String enchName : split[1].split(EnchantmentControl.SEP)){
                enchName = enchName.trim();
                if(enchName.isEmpty()) continue;
                Enchantment ench = Enchantment.getEnchantmentByLocation(enchName);
                if(ench == null){
                    EnchantmentControl.LOGGER.warn("Could not find enchantment {} while reading enchants per item type {}", enchName, typeName);
                    continue;
                }
                mapOut.computeIfAbsent(ench, k -> new HashSet<>()).add(registry);
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
        Set<ItemTypeMatcherRegistry> matchers = (forAnvil ? itemTypesAnvil : itemTypes).get(enchantment);
        if(matchers == null) return false;

        boolean isValid = false;
        boolean invertedMatches = false;

        for(ItemTypeMatcherRegistry registration : matchers) {
            IMatcher<ItemTypeContext> matcher = registration.getMatcher();

            // Lazy compute itemName only if needed
            if(itemName == null && needsItemName(matcher)) {
                ResourceLocation loc = item.getRegistryName();
                itemName = (loc != null) ? loc.toString() : "";
            }

            ItemTypeContext context = new ItemTypeContext(enchantment, stack, item, itemName);
            boolean matches = matcher.matches(context);

            // Handle inversion
            if(matcher instanceof InvertedMatcher) {
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
        return matcher instanceof ListMatcher ||
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
                if(matcher.getName().equals("NONE")) return; // If other mods use NONE enum
                byName.computeIfAbsent(matcher.getName(), k -> new HashSet<>()).add(ench);
                byEnchantment.computeIfAbsent(ench, k -> new HashSet<>()).add(matcher.getName());
            });
        }

        //Try to be smart, at least a little bit
        // Inferring applicability by offering a fakeStack to customItem.canApply-AtEnchantingTable(fakeStack)
        for (Map.Entry<String, ItemTypeMatcherRegistry> entry : registeredMatchers.entrySet()) {
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
        ConfigHandler.itemTypes.general.itemTypes = out;

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
        ConfigHandler.itemTypes.anvil.itemTypes = outAnv;

        //Reset print toggle

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

