package enchantmentcontrol.util;

import java.util.*;

public class DefaultIncompatibleGroups {
    public static final Map<String, List<String>> defaultIncompatibilities = new HashMap<>();
    static {
        defaultIncompatibilities.put("Vanilla Protection Enchants", Arrays.asList(
                "minecraft:blast_protection",
                "minecraft:fire_protection",
                "minecraft:projectile_protection",
                "minecraft:protection"
        ));
        defaultIncompatibilities.put("Vanilla Damage Enchants", Arrays.asList(
                "minecraft:bane_of_arthropods",
                "minecraft:sharpness",
                "minecraft:smite"
        ));
        defaultIncompatibilities.put("Infinity vs Mending", Arrays.asList(
                "minecraft:infinity",
                "minecraft:mending"
        ));
        defaultIncompatibilities.put("Fortune vs Silk Touch", Arrays.asList(
                "minecraft:fortune",
                "minecraft:silk_touch"
        ));
        defaultIncompatibilities.put("Depth Strider vs Frost Walker", Arrays.asList(
                "minecraft:depth_strider",
                "minecraft:frost_walker"
        ));
        defaultIncompatibilities.put("Vanilla Garble #1", Arrays.asList(
                "minecraft:looting",
                "minecraft:silk_touch"
        ));
        defaultIncompatibilities.put("Vanilla Garble #2", Arrays.asList(
                "minecraft:luck_of_the_sea",
                "minecraft:silk_touch"
        ));
    }

    public static String getName(ArrayList<String> groupList) {
        return defaultIncompatibilities.entrySet().stream()
                .filter(e -> groupList.size() == e.getValue().size())
                .filter(e -> groupList.containsAll(e.getValue()))
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);
    }
}
