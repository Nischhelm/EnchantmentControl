package enchantmentcontrol.core;

import enchantmentcontrol.compat.CompatUtil;
import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.config.folders.ItemTypeConfig;
import fermiumbooter.FermiumRegistryAPI;
import fermiumbooter.util.FermiumJarScanner;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(1001)
public class EnchantmentControlPlugin implements IFMLLoadingPlugin {
	public EnchantmentControlPlugin() {
		FermiumRegistryAPI.enqueueMixin(false, "mixins.enchantmentcontrol.vanilla.json");
		FermiumRegistryAPI.enqueueMixin(false, "mixins.enchantmentcontrol.vanilla.creativecanapplyoverride.json", () -> ConfigHandler.itemTypes.creativeOptions != ItemTypeConfig.EnumCreativeAllowed.ANVIL);
		FermiumRegistryAPI.enqueueMixin(false, "mixins.enchantmentcontrol.vanilla.etablemaxlvl.json", () -> !FermiumJarScanner.isModPresent("apotheosis") && ConfigHandler.etable.maxLvl >= 0);

		FermiumRegistryAPI.enqueueMixin(true, "mixins.enchantmentcontrol.contenttweaker.json", CompatUtil.contenttweaker::isLoaded);
		FermiumRegistryAPI.enqueueMixin(true, "mixins.enchantmentcontrol.crafttweaker.json", () -> Loader.isModLoaded("crafttweaker"));
	}

	@Override
	public String[] getASMTransformerClass() {
		if(ConfigHandler.debug.enableEnchantmentInjection)
			return new String[]{ EnchantmentClassTransformer.class.getName() };
		return new String[0];
	}
	
	@Override public String getModContainerClass() {return null;}
	@Override public String getSetupClass() {return null;}
	@Override public void injectData(Map<String, Object> data) {
		if (Boolean.FALSE.equals(data.get("runtimeDeobfuscationEnabled"))) {
			MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
			CoreModManager.getReparseableCoremods().removeIf(s -> StringUtils.containsIgnoreCase(s, "fermiumbooter"));
		}
	}
	@Override public String getAccessTransformerClass() {return null;}
}