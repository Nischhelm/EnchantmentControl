package enchantmentcontrol;

import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.config.EarlyConfigReader;
import enchantmentcontrol.config.EnchantmentInfoConfigHandler;
import enchantmentcontrol.config.EnchantmentInfoWriter;
import enchantmentcontrol.config.classdump.EnchantmentClassWriter;
import enchantmentcontrol.util.EnchantmentInfoInferrer;
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

	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        EnchantmentInfoConfigHandler.preInit(); //read EnchantmentInfo's from /enchantments
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        EnchantmentClassWriter.postInit(); //write /tmp/enchclasses.dump for next startup (sad that this is after late mixin load. classgraph could fix that if i could get it to work. then i wouldn't even need a custom file)

        EnchantmentInfoConfigHandler.postInit(); //apply manual overrides for rarity and slots, TODO this could move into a different class im not sure where it fits

        //infer info from existing enchantment objects (can be used for testing and development, it creates the best fitting approximation of an enchant)
        if(ConfigHandler.dev.printInferred)
            EnchantmentInfoInferrer.postInit();

        if (ConfigHandler.debug.printLoaded)
            EnchantmentInfoWriter.postInit();

        //this just as cache clear
        EarlyConfigReader.clearLines();
    }
}