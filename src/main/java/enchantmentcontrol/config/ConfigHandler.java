package enchantmentcontrol.config;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.folders.*;
import meldexun.betterconfig.api.BetterConfig;
import meldexun.betterconfig.api.LoadEarly;
import meldexun.betterconfig.api.Order;
import net.minecraftforge.common.config.Config;

@BetterConfig(
		modid = EnchantmentControl.MODID,
		bigCategoryComments = false,
		lowerCaseCategories = false,
		removeDeprecatedEntries = true,
		version = ConfigHandler.VERSION
)
@LoadEarly
public class ConfigHandler {
	@Config.Ignore
	public static final String VERSION = "1.0.0";

	@BetterConfig.AfterRead
	@SuppressWarnings("unused")
	public static void migrateConfigs(meldexun.betterconfig.Config config){
		ConfigMigrator.handleMigration(config, config.getVersion(ConfigHandler.class.getName()), VERSION);
	}

	@Config.Comment("If you're a modpack dev just starting to set up this mod, you probably want to start here.")
	@Config.Name("First Setup")
	@Order(0)
	public static FirstSetupConfig dev = new FirstSetupConfig();

	@Config.Comment("Which enchantment is incompatible with what other enchantment?")
	@Config.Name("Incompatible Enchantments")
	@Order(1)
	public static IncompatibleConfig incompatible = new IncompatibleConfig();

	@Config.Comment("What enchantment goes on which item?")
	@Config.Name("Item Types")
	@Order(2)
	public static ItemTypeConfig itemTypes = new ItemTypeConfig();

	@Config.Comment("Option to blacklist enchants to appear from various sources (or entirely)")
	@Config.Name("Blacklists")
	@Order(3)
	public static BlacklistConfig blacklists = new BlacklistConfig();

	@Config.Comment("Options for Anvil Mechanics")
	@Config.Name("Anvil Mechanics")
	@Order(4)
	public static AnvilConfig anvil = new AnvilConfig();

	@Config.Comment("Options for Enchantment Table Mechanics")
	@Config.Name("Enchantment Table Mechanics")
	@Order(5)
	public static EnchTableConfig etable = new EnchTableConfig();

	@Config.Comment("Mixin Toggles")
	@Config.Name("Mixin Toggles")
	@Order(6)
	public static MixinToggleConfig mixintoggles = new MixinToggleConfig();

	@Config.Comment("Mod Compatibility")
	@Config.Name("Compat")
	@Order(7)
	public static CompatConfig compat = new CompatConfig();

	@Config.Comment("Advanced ")
	@Config.Name("Advanced")
	@Order(8)
	public static AdvancedConfig advanced = new AdvancedConfig();

	@Config.Comment("Debug Options")
	@Config.Name("Debug")
	@Order(9)
	public static DebugConfig debug = new DebugConfig();
}