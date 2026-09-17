package enchantmentcontrol.config.folders;

import meldexun.betterconfig.api.Order;
import net.minecraftforge.common.config.Config;

import java.util.ArrayList;
import java.util.List;

public class BlacklistConfig {
    @Config.Comment("Librarians will not be able to generate enchantments in this list")
    @Config.Name("Librarian Enchantment Blacklist")
    @Order(0)
    public List<String> blacklistedLibrarianEnchants = new ArrayList<>();

    @Config.Comment("Enchantment blacklist will be treated as a Whitelist")
    @Config.Name("Librarian Enchantment Whitelist Toggle")
    @Order(1)
    public boolean blacklistedLibrarianEnchantsIsWhitelist = false;

    @Config.Comment("Loot enchanted with levels (enchant_with_levels) will not be able to generate enchantments in this list")
    @Config.Name("Level Enchantment Blacklist")
    @Order(2)
    public List<String> blacklistedRandomLevelEnchants = new ArrayList<>();

    @Config.Comment("Level Enchantment blacklist will be treated as a Whitelist")
    @Config.Name("Level Enchantment Whitelist Toggle")
    @Order(3)
    public boolean blacklistedRandomLevelEnchantsIsWhitelist = false;

    @Config.Comment("Enchanting table will not be able to generate enchantments in this list")
    @Config.Name("Enchanting Table Blacklist")
    @Order(4)
    public List<String> blacklistedEnchTableEnchants = new ArrayList<>();

    @Config.Comment("Enchantment Table blacklist will be treated as a Whitelist")
    @Config.Name("Enchantment Table Whitelist Toggle")
    @Order(5)
    public boolean blacklistedEnchTableEnchantsIsWhitelist = false;

    @Config.Comment("Fully random books (enchant_randomly) will not be able to generate enchantments in this list")
    @Config.Name("Random Enchantment Blacklist")
    @Order(6)
    public List<String> blacklistedRandomEnchants = new ArrayList<>();

    @Config.Comment("Random Enchantment blacklist will be treated as a Whitelist")
    @Config.Name("Random Enchantment Whitelist Toggle")
    @Order(7)
    public boolean blacklistedRandomEnchantsIsWhitelist = false;

    @Config.Comment("Enchants in this list will be prevented from being registered in the game. There will be no way to access them at all.")
    @Config.Name("Registered Enchantment Blacklist")
    @Config.RequiresMcRestart
    @Order(8)
    public List<String> blacklistedRegistryEnchants = new ArrayList<>();
}
