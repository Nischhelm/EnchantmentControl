package enchantmentcontrol.config.provider;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.ConfigHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlacklistConfigProvider {
    private static List<Enchantment> randomLevelEnchantsBlacklist = null;
    private static List<Enchantment> randomEnchantsBlacklist = null;
    private static List<Enchantment> librarianEnchantsBlacklist = null;
    private static List<Enchantment> enchantTableEnchantsBlacklist = null;
    private static List<String> registryEnchantsBlacklist = null;

    public static void resetBlacklists(){
        randomLevelEnchantsBlacklist = null;
        randomEnchantsBlacklist = null;
        librarianEnchantsBlacklist = null;
        enchantTableEnchantsBlacklist = null;
    }

    public static List<Enchantment> getRandomLevelEnchantsBlacklist() {
        if(randomLevelEnchantsBlacklist == null)
            randomLevelEnchantsBlacklist = populateEnchantmentList(ConfigHandler.blacklistedRandomLevelEnchants);
        return randomLevelEnchantsBlacklist;
    }

    public static List<Enchantment> getRandomEnchantsBlacklist() {
        if(randomEnchantsBlacklist == null)
            randomEnchantsBlacklist = populateEnchantmentList(ConfigHandler.blacklistedRandomEnchants);
        return randomEnchantsBlacklist;
    }

    public static List<Enchantment> getLibrarianEnchantsBlacklist() {
        if(librarianEnchantsBlacklist == null)
            librarianEnchantsBlacklist = populateEnchantmentList(ConfigHandler.blacklistedLibrarianEnchants);
        return librarianEnchantsBlacklist;
    }

    public static List<Enchantment> getEnchantTableEnchantsBlacklist() {
        if(enchantTableEnchantsBlacklist == null)
            enchantTableEnchantsBlacklist = populateEnchantmentList(ConfigHandler.blacklistedEnchTableEnchants);
        return enchantTableEnchantsBlacklist;
    }

    public static List<String> getRegistryEnchantsBlacklist(){
        if(registryEnchantsBlacklist == null)
            registryEnchantsBlacklist = Arrays.asList(ConfigHandler.blacklistedRegistryEnchants);
        return registryEnchantsBlacklist;
    }

    private static List<Enchantment> populateEnchantmentList(String[] names) {
        List<Enchantment> list = new ArrayList<>();
        for(String name : names) {
            name = name.trim();
            if(name.isEmpty()) continue;
            Enchantment enchant = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(name));
            if(enchant == null) {
                EnchantmentControl.LOGGER.warn("Invalid enchantment {} in blacklist", name);
                continue;
            }
            list.add(enchant);
        }
        return list;
    }
}
