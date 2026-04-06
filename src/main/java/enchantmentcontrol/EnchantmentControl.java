package enchantmentcontrol;

import enchantmentcontrol.bloodanvil.FeatureBloodAnvil;
import enchantmentcontrol.compat.CompatUtil;
import enchantmentcontrol.compat.crafttweaker.CT_EnchantmentInfo;
import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.config.EarlyConfigReader;
import enchantmentcontrol.config.classdump.EnchantmentClassWriter;
import enchantmentcontrol.config.descriptions.DescriptionReader;
import enchantmentcontrol.config.descriptions.EmptyEnchantmentWriter;
import enchantmentcontrol.config.descriptions.NamesReader;
import enchantmentcontrol.config.enchantmentinfojsons.EnchantmentInfoConfigReader;
import enchantmentcontrol.config.enchantmentinfojsons.EnchantmentInfoInferrerWriter;
import enchantmentcontrol.config.enchantmentinfojsons.EnchantmentInfoWriter;
import enchantmentcontrol.config.provider.IncompatibleConfigProvider;
import enchantmentcontrol.config.provider.ItemTypeConfigProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

@Mod(
        modid = EnchantmentControl.MODID,
        version = EnchantmentControl.VERSION,
        name = EnchantmentControl.NAME,
        dependencies =
                "required-after:fermiumbooter@[1.3.2,);" +
                "before:contenttweaker;" +
                "after:somanyenchantments;"
)
public class EnchantmentControl {
    public static final String MODID = "enchantmentcontrol";
    public static final String VERSION = "1.0.8.4";
    public static final String NAME = "EnchantmentControl";
    public static final Logger LOGGER = LogManager.getLogger(EnchantmentControl.NAME);
    public static final String SEP = ",";
    public static Configuration CONFIG = null;
    public static boolean configNeedsSaving = false;
    public static boolean loadingComplete = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        try {
            Field field = ConfigManager.class.getDeclaredField("CONFIGS");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Configuration> map = (Map<String, Configuration>) field.get(null);
            CONFIG = map.get(new File(Loader.instance().getConfigDir(), MODID + ".cfg").getAbsolutePath());
        } catch (Exception e){
            CONFIG = new Configuration(new File(Loader.instance().getConfigDir(), MODID + ".cfg"));
        }

        if(ConfigHandler.anvil.bloodAnvil.enabled) {
            MinecraftForge.EVENT_BUS.register(FeatureBloodAnvil.class);
            FeatureBloodAnvil.onPreInit();
        }

        EnchantmentInfoConfigReader.preInit(); //read EnchantmentInfo's from /enchantments
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        EnchantmentClassWriter.postInit(); //write /tmp/enchclasses.dump for next startup (sad that this is after late mixin load. classgraph could fix that if i could get it to work. then i wouldn't even need a custom file)
        if(event.getSide() == Side.CLIENT) {
            DescriptionReader.init();
            EmptyEnchantmentWriter.write(DescriptionReader.PATH);
            NamesReader.init();
            EmptyEnchantmentWriter.write(NamesReader.PATH);
        }

        EnchantmentInfoConfigReader.applyManualOverrides(); //apply manual overrides for rarity, slots and json-sourced type

        //infer info from existing enchantment objects (can be used for testing and development, it creates the best fitting approximation of an enchant). these are not loaded
        if(ConfigHandler.dev.printInferred) EnchantmentInfoInferrerWriter.printInferred();
        if (ConfigHandler.debug.printLoaded) EnchantmentInfoWriter.printLoaded();

        //Incompatibilities
        if(ConfigHandler.dev.printIncompats) IncompatibleConfigProvider.printDefaultIncompatibilities();
        IncompatibleConfigProvider.onResetConfig();

        //Item Types
        ItemTypeConfigProvider.initRegisteredItemTypesFromConfig();
        if(ConfigHandler.dev.printTypes) ItemTypeConfigProvider.printDefaultItemTypes();
        ItemTypeConfigProvider.initItemTypeConfig();

        if(CompatUtil.contenttweaker.isLoaded()) CT_EnchantmentInfo.postInit();

        if(configNeedsSaving) ConfigManager.sync(MODID, Config.Type.INSTANCE);

        //this just as cache clear
        EarlyConfigReader.clearLines();

        loadingComplete = true;
    }
}