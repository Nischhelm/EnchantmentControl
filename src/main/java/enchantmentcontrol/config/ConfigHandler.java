package enchantmentcontrol.config;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.folders.BlacklistConfig;
import enchantmentcontrol.config.folders.ItemTypeConfig;
import enchantmentcontrol.config.folders.ModPackDevConfig;
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

	@Config.Comment("Override vanilla rarity weights (COMMON = 10, UNCOMMON = 5, RARE = 2, VERY_RARE = 1) \n" +
			"or define your own rarities with their own weights here.\n" +
			"Pattern: I:YOUR_RARITY_NAME=weight")
	@Config.Name(EarlyConfigReader.RARITY_CONFIG_NAME)
	public static Map<String, Integer> rarityWeights = new HashMap<String, Integer>(){};

	@Config.Comment("TODO")
	@Config.Name("Blacklists")
	public static BlacklistConfig blacklists = new BlacklistConfig();

	@Config.Comment("TODO")
	@Config.Name("Item Types")
	public static ItemTypeConfig itemTypes = new ItemTypeConfig();

	@Config.Comment("TODO")
	@Config.Name("Start here as modpack dev")
	public static ModPackDevConfig dev = new ModPackDevConfig();

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