package enchantmentcontrol.config;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.bloodanvil.FeatureBloodAnvil;
import enchantmentcontrol.config.provider.BlacklistConfigProvider;
import enchantmentcontrol.config.provider.IncompatibleConfigProvider;
import enchantmentcontrol.config.provider.ItemTypeConfigProvider;
import meldexun.betterconfig.api.BetterConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = EnchantmentControl.MODID)
public class ConfigChangedHandler {
	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(EnchantmentControl.MODID)) {
			BetterConfigManager.sync(EnchantmentControl.MODID);

			ItemTypeConfigProvider.onResetConfig();
			BlacklistConfigProvider.onResetConfig();
			IncompatibleConfigProvider.onResetConfig();

			FeatureBloodAnvil.resetConfigValues();
		}
	}
}