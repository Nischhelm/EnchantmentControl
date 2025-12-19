package enchantmentcontrol.config.folders;

import enchantmentcontrol.config.EarlyConfigReader;
import net.minecraftforge.common.config.Config;

import java.util.HashMap;
import java.util.Map;

public class ModPackDevConfig {
    @Config.Comment("If you really dont like one mods enchantment id (modid:enchid), you can add it here with a second entry of what you want to call it instead (modid:enchid). \n" +
            "WARNING: this will cause permanent id remapping issues for old worlds! Only do this for new worlds or before you release a modpack, if you really need to!\n" +
            "pattern: S:\"modid:oldenchid\"=modid:newenchid")
    @Config.Name(EarlyConfigReader.IDREMAP_CONFIG_NAME)
    public Map<String, String> idRemaps = new HashMap<String, String>(){};

    @Config.Comment("Enchantment classes that should not be modified at all by this mod. \n" +
            "Use this if there are crashes when this mod tries to automatically modify some mods enchantments. \n" +
            "You find the class name in config/enchantmentcontrol/tmp/enchclasses.dump\n" +
            "Classes noted here need to look like net.minecraft.enchantment.EnchantmentDamage\n" +
            "Vanilla enchants will always be targeted, so putting their classes in here won't do anything")
    @Config.Name(EarlyConfigReader.BLACKLIST_CONFIG_NAME)
    public String[] disabledClasses = {};

    @Config.Comment("If enabled, writes all currently registered enchantment infos to config/enchantmentcontrol/enchantments_out.json during PostInit. Disabled by default.")
    @Config.Name("Print Default Enchantment Infos")
    public boolean printDefaults = false;

    @Config.Comment("If enabled, infers info about enchantments to print out.")
    @Config.Name("Infer Enchantment Infos")
    public boolean inferEnchantmentInfo = false;
}
