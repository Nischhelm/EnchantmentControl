package enchantmentcontrol;

import enchantmentcontrol.config.EnchantmentInfoConfigHandler;
import enchantmentcontrol.config.classdump.EnchantmentClassWriter;
import enchantmentcontrol.util.EnchantmentInfo;
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
        EnchantmentInfoConfigHandler.preInit();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        EnchantmentClassWriter.postInit();
    }
}