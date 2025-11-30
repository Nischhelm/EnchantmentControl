package enchantmentcontrol.config.folders;

import net.minecraftforge.common.config.Config;

public class BlacklistConfig {
    @Config.Comment("Librarians will not be able to generate enchantments in this list")
    @Config.Name("Librarian Enchantment Blacklist")
    public String[] blacklistedLibrarianEnchants = {
    };

    @Config.Comment("Enchantment blacklist will be treated as a Whitelist")
    @Config.Name("Librarian Enchantment Whitelist Toggle")
    public boolean blacklistedLibrarianEnchantsIsWhitelist = false;

    @Config.Comment("Loot enchanted with levels (enchant_with_levels) will not be able to generate enchantments in this list")
    @Config.Name("Level Enchantment Blacklist")
    public String[] blacklistedRandomLevelEnchants = {
    };

    @Config.Comment("Level Enchantment blacklist will be treated as a Whitelist")
    @Config.Name("Level Enchantment Whitelist Toggle")
    public boolean blacklistedRandomLevelEnchantsIsWhitelist = false;

    @Config.Comment("Enchanting table will not be able to generate enchantments in this list")
    @Config.Name("Enchanting Table Blacklist")
    public String[] blacklistedEnchTableEnchants = {
    };

    @Config.Comment("Enchantment Table blacklist will be treated as a Whitelist")
    @Config.Name("Enchantment Table Whitelist Toggle")
    public boolean blacklistedEnchTableEnchantsIsWhitelist = false;

    @Config.Comment("Fully random books (enchant_randomly) will not be able to generate enchantments in this list")
    @Config.Name("Random Enchantment Blacklist")
    public String[] blacklistedRandomEnchants = {
    };

    @Config.Comment("Random Enchantment blacklist will be treated as a Whitelist")
    @Config.Name("Random Enchantment Whitelist Toggle")
    public boolean blacklistedRandomEnchantsIsWhitelist = false;

    @Config.Comment("Enchants in this list will be prevented from being registered in the game. There will be no way to access them at all.")
    @Config.Name("Registered Enchantment Blacklist")
    @Config.RequiresMcRestart
    public String[] blacklistedRegistryEnchants = {
    };
}
