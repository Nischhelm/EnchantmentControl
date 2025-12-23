package enchantmentcontrol;

import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.config.EarlyConfigReader;
import enchantmentcontrol.config.classdump.EnchantmentClassWriter;
import enchantmentcontrol.config.enchantmentinfojsons.EnchantmentInfoConfigReader;
import enchantmentcontrol.config.enchantmentinfojsons.EnchantmentInfoInferrerWriter;
import enchantmentcontrol.config.enchantmentinfojsons.EnchantmentInfoWriter;
import enchantmentcontrol.config.provider.IncompatibleConfigProvider;
import enchantmentcontrol.config.provider.ItemTypeConfigProvider;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = EnchantmentControl.MODID,
        version = EnchantmentControl.VERSION,
        name = EnchantmentControl.NAME,
        dependencies = "required-after:fermiumbooter@[1.3.2,)"
)
public class EnchantmentControl {
    public static final String MODID = "enchantmentcontrol";
    public static final String VERSION = "indev";
    public static final String NAME = "EnchantmentControl";
    public static final Logger LOGGER = LogManager.getLogger(EnchantmentControl.NAME);
    public static final String SEP = ",";
    public static Configuration CONFIG = null;
    public static boolean configNeedsSaving = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        CONFIG = new Configuration(event.getSuggestedConfigurationFile());
        CONFIG.load();

        EnchantmentInfoConfigReader.preInit(); //read EnchantmentInfo's from /enchantments
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        EnchantmentClassWriter.postInit(); //write /tmp/enchclasses.dump for next startup (sad that this is after late mixin load. classgraph could fix that if i could get it to work. then i wouldn't even need a custom file)

        EnchantmentInfoConfigReader.applyManualOverrides(); //apply manual overrides for rarity and slots

        //infer info from existing enchantment objects (can be used for testing and development, it creates the best fitting approximation of an enchant). these are not loaded
        if(ConfigHandler.dev.printInferred) EnchantmentInfoInferrerWriter.printInferred();
        if (ConfigHandler.debug.printLoaded) EnchantmentInfoWriter.printLoaded();

        //Incompatibilities
        if(ConfigHandler.dev.readIncompats) IncompatibleConfigProvider.writeDefaultIncompatibilities();
        IncompatibleConfigProvider.onResetConfig();

        //Item Types
        ItemTypeConfigProvider.readItemTypesFromConfig();
        //TODO: doesnt fill in first setup
        if(ConfigHandler.dev.readTypes) ItemTypeConfigProvider.writeDefaultItemTypes();
        ItemTypeConfigProvider.readItemTypeMappingsFromConfig();

        if(configNeedsSaving) CONFIG.save();

        //this just as cache clear
        EarlyConfigReader.clearLines();
    }
}