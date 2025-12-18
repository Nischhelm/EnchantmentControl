package enchantmentcontrol.config;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.folders.BlacklistConfig;
import enchantmentcontrol.config.folders.ItemTypeConfig;
import enchantmentcontrol.config.provider.BlacklistConfigProvider;
import enchantmentcontrol.config.provider.ItemTypeConfigProvider;
import fermiumbooter.annotations.MixinConfig;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

@Config(modid = EnchantmentControl.MODID)
@MixinConfig(name = EnchantmentControl.MODID)
public class ConfigHandler {
	@Config.Comment("Enable")
	@Config.Name("Enable")
	@Config.RequiresMcRestart
	@MixinConfig.MixinToggle(
			earlyMixin = "mixins.enchantmentcontrol.vanilla.json",
			lateMixin = "mixins.enchantmentcontrol.modded.json",
			defaultValue = true
	)
	public static boolean enable = true; //TODO: needs way more config

	@Config.Comment("List groupings of enchantments that should be incompatible with each other")
	@Config.Name("Incompatible Enchantment Groups")
	public static String[] incompatibleGroups = {
	};

	@Config.Comment("Enchantment classes that should not be modified at all by this mod. \n" +
			"Use this if there are crashes when this mod tries to automatically modify some mods enchantments. \n" +
			"You find the class name in config/enchantmentcontrol/tmp/enchclasses.dump\n" +
			"Classes noted here need to look like net.minecraft.enchantment.EnchantmentDamage\n" +
			"Vanilla enchants will always be targeted, so putting their classes in here won't do anything")
	@Config.Name(EarlyConfigReader.BLACKLIST_CONFIG_NAME)
	public static String[] disabledClasses = {};

	@Config.Comment("Override vanilla rarity weights (COMMON = 10, UNCOMMON = 5, RARE = 2, VERY_RARE = 1) or define your own rarities with their own weights here." +
			"Pattern: ") //TODO
	@Config.Name("Defined Rarities")
	public static Map<String, Integer> rarityWeights = new HashMap<String, Integer>(){{
	}};

	@Config.Comment("TODO")
	@Config.Name("Blacklists")
	public static BlacklistConfig blacklist = new BlacklistConfig();

	@Config.Comment("TODO")
	@Config.Name("Item Types")
	public static ItemTypeConfig itemTypes = new ItemTypeConfig();

	@Mod.EventBusSubscriber(modid = EnchantmentControl.MODID)
	private static class EventHandler {
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if(event.getModID().equals(EnchantmentControl.MODID)) {
				ConfigManager.sync(EnchantmentControl.MODID, Config.Type.INSTANCE);

				ItemTypeConfigProvider.resetCanApply();
				BlacklistConfigProvider.resetBlacklists();
				//TODO: reset incompat
			}
		}
	}
}