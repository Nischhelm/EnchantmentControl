package enchantmentcontrol.core;

import com.google.common.collect.ImmutableMap;
import enchantmentcontrol.config.ConfigHandler;
import fermiumbooter.FermiumRegistryAPI;
import fermiumbooter.FermiumTransformerAPI;
import fermiumbooter.util.FermiumJarScanner;
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

		FermiumRegistryAPI.enqueueMixin(false, "mixins.enchantmentcontrol.contenttweaker.json", () -> FermiumJarScanner.isModPresent("contenttweaker"));
		FermiumRegistryAPI.enqueueMixin(false, "mixins.enchantmentcontrol.crafttweaker.json", () -> FermiumJarScanner.isModPresent("crafttweaker"));

		// Enchantments Control replaces these methods entirely with its own logic (our mixins silently vanish in there),
		// and its per-enchant settings are imported into ours (EnchantmentsControlImporter). Its cosmetic patches (names, tooltips, localization, TE) stay.
		if (FermiumJarScanner.isModPresent("enchcontrol")) {
			String[] blockedClasses = {
					"net.minecraft.enchantment.EnchantmentHelper", // enchanting table + enchant_with_levels
					"net.minecraft.entity.passive.EntityVillager$ListEnchantedBookForEmeralds", // librarians
					"net.minecraft.world.storage.loot.functions.EnchantRandomly", // enchant_randomly
					"net.minecraft.command.CommandEnchant", // /enchant
					"net.minecraft.inventory.ContainerRepair" // anvil (its item lists, incompatibilities and level caps)
			};
			// FML registers coremod transformers wrapped in a generated $wrapper class
			FermiumTransformerAPI.blockTransformerFromClasses("$wrapper.austeretony.enchcontrol.common.core.ECClassTransformer", blockedClasses);
			FermiumTransformerAPI.blockTransformerFromClasses("austeretony.enchcontrol.common.core.ECClassTransformer", blockedClasses);
		}
	}

	@Override
	public String[] getASMTransformerClass() {
		return ImmutableMap.of(
				EnchantmentClassTransformer.class.getName(), ConfigHandler.debug.enableEnchantmentInjection,
				ModdedClassTransformer.class.getName(), true
		)
				.entrySet().stream()
				.filter(Map.Entry::getValue)
				.map(Map.Entry::getKey)
				.toArray(String[]::new);
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