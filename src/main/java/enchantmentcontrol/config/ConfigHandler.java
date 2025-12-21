package enchantmentcontrol.config;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.folders.BlacklistConfig;
import enchantmentcontrol.config.folders.DebugConfig;
import enchantmentcontrol.config.folders.FirstSetupConfig;
import enchantmentcontrol.config.folders.ItemTypeConfig;
import enchantmentcontrol.config.provider.BlacklistConfigProvider;
import enchantmentcontrol.config.provider.IncompatibleConfigProvider;
import enchantmentcontrol.config.provider.ItemTypeConfigProvider;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

@Config(modid = EnchantmentControl.MODID)
public class ConfigHandler {

	@Config.Comment("List groupings of enchantments that should be incompatible with each other")
	@Config.Name("Incompatible Enchantment Groups")
	public static String[] incompatibleGroups = {};

	@Config.Comment("Override vanilla rarity weights (COMMON = 10, UNCOMMON = 5, RARE = 2, VERY_RARE = 1) \n" +
			"or define your own rarities with their own weights here.\n" +
			"Pattern: I:YOUR_RARITY_NAME=weight\n" +
			"Example:\n" +
			"  I:COMMON=20\n" +
			"  I:UNCOMMON=10\n" +
			"  I:RARE=4\n" +
			"  I:VERY_RARE=2\n" +
			"  I:LEGENDARY=1\n")
	@Config.Name(EarlyConfigReader.RARITY_CONFIG_NAME)
	@Config.RequiresMcRestart
	public static Map<String, Integer> rarityWeights = new HashMap<String, Integer>(){};

	@Config.Comment("Option to blacklist enchants to appear from various sources (or entirely)")
	@Config.Name("Blacklists")
	public static BlacklistConfig blacklists = new BlacklistConfig();

	@Config.Comment("Options for custom item type matchers to use in enchantment applicability")
	@Config.Name("Item Types")
	public static ItemTypeConfig itemTypes = new ItemTypeConfig();

	@Config.Comment("Define custom creature attributes and how to match them to entities.\n" +
			"Pattern: S:MY_ATTR_NAME=type, string1, string2, string3, ...\n" +
			"Available types: \n" +
			"  modid: only check modid(s) \n" +
			"  mob: check against 1 or more mob registry names \n" +
			"  class: check if given class is in java class hierarchy of the mob\n" +
			"Examples:\n" +
			" S:LYCANITE=modid, lycanitesmobs\n" +
			"  S:DRAGON=mob, minecraft:ender_dragon, iceandfire:firedragon, iceandfire:icedragon\n" +
			"  S:ANIMAL=class, net.minecraft.entity.EntityAgeable")
	@Config.Name(EarlyConfigReader.CREAT_ATTR_CONFIG_NAME)
	public static Map<String, String> creatureAttributes = new HashMap<String, String>(){};

	@Config.Comment("If you're a modpack dev just starting to set up this mod, you probably want to start here.")
	@Config.Name("First Setup")
	public static FirstSetupConfig dev = new FirstSetupConfig();

	@Config.Comment(" ")
	@Config.Name("Debug")
	public static DebugConfig debug = new DebugConfig();

	@Mod.EventBusSubscriber(modid = EnchantmentControl.MODID)
	private static class EventHandler {
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if(event.getModID().equals(EnchantmentControl.MODID)) {
				ConfigManager.sync(EnchantmentControl.MODID, Config.Type.INSTANCE);

				ItemTypeConfigProvider.resetCanApply();
				BlacklistConfigProvider.resetBlacklists();
				IncompatibleConfigProvider.applyIncompatsFromConfig();
			}
		}
	}
}