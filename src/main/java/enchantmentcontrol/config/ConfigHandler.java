package enchantmentcontrol.config;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.provider.BlacklistConfigProvider;
import enchantmentcontrol.config.provider.CanApplyConfigProvider;
import fermiumbooter.annotations.MixinConfig;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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

	@Config.Comment("Pattern: \n" +
			"    Name to use; Regex to match against item ids\n" +
			"Custom types can also be used in canApplyAnvil config")
	@Config.Name("Custom Item Types")
	public static String[] customTypes = {
	};

	@Config.Comment("Some modded items only pretend to be a specific item type without actually being them (wolf armor being SWORD and ARMOR_FEET, better survival items being SWORD etc).\n" +
			"Enabling this toggle will make the given item type match those too.\n" +
			"Disabling will instead only look at the actual checks of each EnumEnchantmentType (like SWORD = has to be instanceof ItemSword) to match.")
	@Config.Name("-Allow Custom Items")
	public static boolean allowCustomItems = true;

	@Mod.EventBusSubscriber(modid = EnchantmentControl.MODID)
	private static class EventHandler {
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if(event.getModID().equals(EnchantmentControl.MODID)) {
				ConfigManager.sync(EnchantmentControl.MODID, Config.Type.INSTANCE);

				CanApplyConfigProvider.resetCanApply();
				BlacklistConfigProvider.resetBlacklists();
				//TODO: reset incompat
			}
		}
	}
}