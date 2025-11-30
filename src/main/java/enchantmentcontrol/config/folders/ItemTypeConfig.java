package enchantmentcontrol.config.folders;

import net.minecraftforge.common.config.Config;

public class ItemTypeConfig {
    @Config.Comment("Pattern: \n" +
            "    Name to use; Regex to match against item ids\n" +
            "Custom types can also be used in canApplyAnvil config")
    @Config.Name("Custom Item Types")
    public String[] customTypes = {
    };

    @Config.Comment("Some modded items only pretend to be a specific item type without actually being them (wolf armor being SWORD and ARMOR_FEET, better survival items being SWORD etc).\n" +
            "Enabling this toggle will make the given item type match those too.\n" +
            "Disabling will instead only look at the actual checks of each EnumEnchantmentType (like SWORD = has to be instanceof ItemSword) to match.")
    @Config.Name("Allow Custom Items")
    public boolean allowCustomItems = true;
}
