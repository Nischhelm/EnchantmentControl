package enchantmentcontrol.core;

import fermiumbooter.FermiumRegistryAPI;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
public class EnchantmentControlPlugin implements IFMLLoadingPlugin {
	public EnchantmentControlPlugin() {
		MixinBootstrap.init();

		FermiumRegistryAPI.enqueueMixin(false, "mixins.enchantmentcontrol.vanilla.json");
		FermiumRegistryAPI.enqueueMixin(true, "mixins.enchantmentcontrol.contenttweaker.json", () -> Loader.isModLoaded("contenttweaker"));
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{
				EnchantmentControlClassTransformer.class.getName(),
				ModCompatClassTransformer.class.getName()
		};
	}
	
	@Override public String getModContainerClass() {return null;}
	@Override public String getSetupClass() {return null;}
	@Override public void injectData(Map<String, Object> data) { }
	@Override public String getAccessTransformerClass() {return null;}
}