package enchantmentcontrol.config.provider;

import enchantmentcontrol.config.ConfigHandler;
import net.minecraft.enchantment.Enchantment;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class RarityProvider {
    public static final Map<String, Enchantment.Rarity> rarities = new HashMap<>(); //technically not needed as Enchantment.Rarity.valueOf also works

    public static void registerRarities(Function<String, Enchantment.Rarity> constructor){
        for(String s : ConfigHandler.rarityWeights.keySet())
            rarities.put(s, constructor.apply(s));
    }
}
