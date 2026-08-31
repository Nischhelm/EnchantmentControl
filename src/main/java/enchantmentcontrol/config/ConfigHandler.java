package enchantmentcontrol.config;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.folders.*;
import enchantmentcontrol.config.matcherregistry.CreatureAttributeTypeTypes;
import meldexun.betterconfig.api.BetterConfig;
import meldexun.betterconfig.api.LoadEarly;
import net.minecraftforge.common.config.Config;

import java.util.*;

@BetterConfig(modid = EnchantmentControl.MODID)
@LoadEarly
public class ConfigHandler {

	@Config.Comment("If you're a modpack dev just starting to set up this mod, you probably want to start here.")
	@Config.Name("First Setup")
	public static FirstSetupConfig dev = new FirstSetupConfig();

	@Config.Comment({
			"Each line is a group of mutually exclusive enchantments",
			"  like Smite, Sharpness and BoA",
			"Can be auto filled using \"First Setup.Print Default Incompatibilities\"",
			"Warning: this mod takes full control of enchantments incompatibilities with each other",
			"  so run the first setup every time you add mods that have enchants, then compare with what you set up to stay up to date"
	})
	@Config.Name("Incompatible Groups")
	public static Map<String, HashSet<String>> incompatibleGroups = new LinkedHashMap<>();

	@Config.Comment("Global Toggle to disable the entire Incompatible groups override")
	@Config.Name("Incompatible Groups Enabled")
	public static boolean incompatibleEnabled = true;

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
	public static Map<String, Integer> rarityWeights = new HashMap<>();

	@Config.Comment("Option to blacklist enchants to appear from various sources (or entirely)")
	@Config.Name("Blacklists")
	public static BlacklistConfig blacklists = new BlacklistConfig();

	@Config.Comment("What enchantment goes on which item?")
	@Config.Name("Item Types")
	public static ItemTypeConfig itemTypes = new ItemTypeConfig();

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
	public static Map<String, CustomCreatureAttribute> creatureAttributes = new HashMap<>();

	public static class CustomCreatureAttribute {
		public CreatureAttributeTypeTypes type = CreatureAttributeTypeTypes.MOB;
		public LinkedHashSet<String> values = new LinkedHashSet<>();
		public CustomCreatureAttribute() {}
	}

	@Config.Comment("Debug Options")
	@Config.Name("Debug")
	public static DebugConfig debug = new DebugConfig();

	@Config.Comment("Options for Anvil Mechanics")
	@Config.Name("Anvil Mechanics")
	public static AnvilConfig anvil = new AnvilConfig();

	@Config.Comment("Options for Enchantment Table Mechanics")
	@Config.Name("Enchantment Table Mechanics")
	public static EnchTableConfig etable = new EnchTableConfig();

	@Config.Comment("Mod Compatibility")
	@Config.Name("Compat")
	public static CompatConfig compat = new CompatConfig();

	@Config.Comment("Mixin Toggles")
	@Config.Name("Mixin Toggles")
	public static MixinToggleConfig mixintoggles = new MixinToggleConfig();
}