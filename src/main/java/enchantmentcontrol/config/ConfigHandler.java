package enchantmentcontrol.config;

import enchantmentcontrol.EnchantmentControl;
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
	@MixinConfig.MixinToggle(lateMixin = "mixins.enchantmentcontrol.modded.json", defaultValue = true)
	public static boolean enableVanillaMixin = true;

	@Mod.EventBusSubscriber(modid = EnchantmentControl.MODID)
	private static class EventHandler{
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if(event.getModID().equals(EnchantmentControl.MODID)) {
				ConfigManager.sync(EnchantmentControl.MODID, Config.Type.INSTANCE);
			}
		}
	}
}