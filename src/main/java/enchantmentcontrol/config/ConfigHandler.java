package enchantmentcontrol.config;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.folders.*;
import enchantmentcontrol.config.provider.BlacklistConfigProvider;
import enchantmentcontrol.config.provider.IncompatibleConfigProvider;
import enchantmentcontrol.config.provider.ItemTypeConfigProvider;
import enchantmentcontrol.util.ConfigRef;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

@Config(modid = EnchantmentControl.MODID)
public class ConfigHandler {

	@Config.Comment("If you're a modpack dev just starting to set up this mod, you probably want to start here.")
	@Config.Name("First Setup")
	public static FirstSetupConfig dev = new FirstSetupConfig();

	@Config.Comment({
			"Each line is a group of mutually exclusive enchantments",
			"  like Smite, Sharpness and BoA",
			"Can be auto filled using \"First Setup."+ConfigRef.PRINT_INCOMPAT_CONFIG_NAME+"\"",
			"Warning: this mod takes full control of enchantments incompatibilities with each other",
			"  so run the first setup every time you add mods that have enchants, then compare with what you set up to stay up to date"
	})
	@Config.Name(ConfigRef.INCOMPAT_CFG_NAME)
	public static String[] incompatibleGroups = {};

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
	@Config.Name(ConfigRef.RARITY_CONFIG_NAME)
	@Config.RequiresMcRestart
	public static Map<String, Integer> rarityWeights = new HashMap<String, Integer>(){};

	@Config.Comment("Option to blacklist enchants to appear from various sources (or entirely)")
	@Config.Name("Blacklists")
	public static BlacklistConfig blacklists = new BlacklistConfig();

	@Config.Comment("What enchantment goes on which item?")
	@Config.Name("Item Types")
	public static ItemTypeConfig itemTypes = new ItemTypeConfig();

	@Config.Comment({
			"Define custom creature attributes and how to match them to entities.",
			"Pattern: S:MY_ATTR_NAME=type, string1, string2, string3, ...",
			"Available types: ",
			"  modid: only check modid(s) ",
			"  mob: check against 1 or more mob registry names ",
			"  class: check if given class is in java class hierarchy of the mob",
			"Examples:",
			" S:LYCANITE=modid, lycanitesmobs",
			"  S:DRAGON=mob, minecraft:ender_dragon, iceandfire:firedragon, iceandfire:icedragon",
			"  S:ANIMAL=class, net.minecraft.entity.EntityAgeable"
	})
	@Config.Name(ConfigRef.CREAT_ATTR_CONFIG_NAME)
	public static Map<String, String> creatureAttributes = new HashMap<String, String>(){};

	@Config.Comment("Debug Options")
	@Config.Name("Debug")
	public static DebugConfig debug = new DebugConfig();

	@Config.Comment("Options for Anvil Mechanics")
	@Config.Name("Anvil Mechanics")
	public static AnvilConfig anvil = new AnvilConfig();

	@Mod.EventBusSubscriber(modid = EnchantmentControl.MODID)
	private static class EventHandler {
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if(event.getModID().equals(EnchantmentControl.MODID)) {
				ConfigManager.sync(EnchantmentControl.MODID, Config.Type.INSTANCE);

				ItemTypeConfigProvider.onResetConfig();
				BlacklistConfigProvider.onResetConfig();
				IncompatibleConfigProvider.onResetConfig();
			}
		}
	}
}