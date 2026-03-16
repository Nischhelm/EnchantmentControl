package enchantmentcontrol.config.folders;

import net.minecraftforge.common.config.Config;

public class BloodAnvilConfig {
    @Config.Comment("Enables the Blood Anvil, a Block similar to the anvil which allows to move enchants from one item to another.")
    @Config.Name("Enabled")
    public boolean enabled = true;

    @Config.Comment("How many XP Levels using the Blood Anvil will cost, flat")
    @Config.Name("XP Level Cost")
    public int cost = 100;

    @Config.Comment("If enabled, will calculate the total cost of the enchantment move depending on the config defined anvil formulas for calculating xp costs")
    @Config.Name("XP Level Cost Is Dynamic")
    public boolean costIsDynamic = false;

    @Config.Comment("Allows to move enchants to a book or enchanted book")
    @Config.Name("Allow Books")
    public boolean allowBooks = false;

    @Config.Comment({
            "Allows to move enchants to an already enchanted item. ",
            "If a moved enchant is already on the item, the one with the bigger level is kept, ignoring combination rules like 4+4=5"
    })
    @Config.Name("Allow Already Enchanted")
    public boolean allowEnchanted = true;

    @Config.Comment("If enabled, will combine the items repair costs too, the same way anvils do according to \"Anvil Use Cost Combination Type\", but without increasing the cost")
    @Config.Name("Also move Repair Cost")
    public boolean moveAnvilCost = true;

    @Config.Comment("Items from this list cannot receive enchantments from the blood anvil")
    @Config.Name("Target Item Blacklist")
    public String[] blacklist = new String[0];

    @Config.Comment("Treat the blacklist as whitelist")
    @Config.Name("Target Item Blacklist is Whitelist")
    public boolean asWhitelist = false;
}
