package enchantmentcontrol.config.folders;

import enchantmentcontrol.util.matchers.EnumMatcherType;
import meldexun.betterconfig.api.Order;
import net.minecraftforge.common.config.Config;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class AdvancedConfig {
    @Config.Comment({
            "Override vanilla rarity weights (COMMON = 10, UNCOMMON = 5, RARE = 2, VERY_RARE = 1)",
            "or define your own rarities with their own weights here.",
            "Pattern: I:YOUR_RARITY_NAME=weight",
            "Example:",
            "  I:COMMON=20",
            "  I:UNCOMMON=10",
            "  I:RARE=4",
            "  I:VERY_RARE=2",
            "  I:LEGENDARY=1"
    })
    @Config.Name("Rarities")
    @Config.RequiresMcRestart
    @Order(0)
    public Map<String, Integer> rarityWeights = new LinkedHashMap<>();

    @Config.Comment({
            "Creature attributes are used to know when to increase dmg on Smite/BoA or custom versions of them.",
            "Define custom creature attributes and how to match them to entities.",
            "Available types: ",
            "  MODID: only check modid(s) ",
            "  MOB: check against 1 or more mob registry names ",
            "  CLASS: check if any of the given java class names is in class hierarchy of the mob",
            "Examples:",
            "  LYCANITE, MODID, [lycanitesmobs]",
            "  DRAGON, MOB, [minecraft:ender_dragon, iceandfire:firedragon, iceandfire:icedragon]",
            "  ANIMAL, CLASS, [net.minecraft.entity.EntityAgeable]",
            "Note: this can also add mobs to existing creature attributes like UNDEAD, ARTHROPOD, ILLAGER"
    })
    @Config.Name("Creature Attributes")
    @Order(1)
    public Map<String, CustomCreatureAttribute> creatureAttributes = new LinkedHashMap<>();
    public static class CustomCreatureAttribute {
        public EnumMatcherType type = EnumMatcherType.EXACT;
        public LinkedHashSet<String> values = new LinkedHashSet<>();
        public CustomCreatureAttribute() {}
    }

    @Config.Comment({
            "If enabled, using \"Print Default Item Types\" will also print all enchants and items that have custom behavior for general or anvil item application.",
            " Prints go to each blacklist. Will also automatically turn them into whitelists by untoggling \"Allow Modded Behavior\"",
            "This option exists to make it easier for the few wanting full control over item types to see which enchantments have overriding behavior to account for"
    })
    @Config.Name("Print Item Type Blacklists")
    @Config.RequiresMcRestart
    @Order(2)
    public boolean printItemTypeBlacklists = false;
}
