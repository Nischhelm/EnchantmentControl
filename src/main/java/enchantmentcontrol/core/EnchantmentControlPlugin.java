package enchantmentcontrol.core;

import fermiumbooter.FermiumRegistryAPI;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@IFMLLoadingPlugin.MCVersion("1.12.2")
public class EnchantmentControlPlugin implements IFMLLoadingPlugin {
	public static final Set<String> actuallyEarlyEnchants = new HashSet<>();
	public static final Set<String> enchantmentClasses = new HashSet<>();

	public EnchantmentControlPlugin() {
		MixinBootstrap.init();

		FermiumRegistryAPI.enqueueMixin(false, "mixins.enchantmentcontrol.vanilla.json", () -> {
			graphClasses(); //this is a good position in the loading process, so we do it here, right during MC init while early jsons are enqueued
			return true;
		});
		FermiumRegistryAPI.enqueueMixin(true, "mixins.enchantmentcontrol.contenttweaker.json", () -> Loader.isModLoaded("contenttweaker"));
		FermiumRegistryAPI.enqueueMixin(true, "mixins.enchantmentcontrol.crafttweaker.json", () -> Loader.isModLoaded("crafttweaker"));
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{
				ModCompatClassTransformer.class.getName(),
				EarlyEnchantmentClassTransformer.class.getName(),
				LateEnchantmentClassTransformer.class.getName()
		};
	}
	
	@Override public String getModContainerClass() {return null;}
	@Override public String getSetupClass() {return null;}
	@Override public void injectData(Map<String, Object> data) { }
	@Override public String getAccessTransformerClass() {return null;}

	public static void graphClasses(){
		try (ScanResult scanResult = new ClassGraph()
				//.verbose()               // Log to stderr
				.enableClassInfo()
				.rejectPackages("java.*")
				.rejectPackages("enchantmentcontrol.*")
				.rejectPackages("org.spongepowered.*")
				.rejectPackages("net.minecraftforge.*")
				.rejectPackages("com.google.common.*")
				.rejectPackages("com.mojang.*")
				.rejectPackages("org.objectweb.asm.*")
				.rejectPackages("nonapi.io.github.classgraph.classpath.*")
				.rejectPackages("com.llamalad7.mixinextras.*")
				.scan()
		) {
			for (ClassInfo routeClassInfo : scanResult.getSubclasses("net.minecraft.enchantment.Enchantment")) {
				enchantmentClasses.add(routeClassInfo.getName());
			}
		}
	}
}