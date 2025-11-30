package enchantmentcontrol.config.provider;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.ConfigHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class IncompatibleConfigProvider {
    public static Set<Enchantment> getIncompatibleEnchantmentsFromConfig(Enchantment thisEnch) {
        Set<Enchantment> incompatEnchs = new HashSet<>();

        ResourceLocation regName = thisEnch.getRegistryName();
        if(regName == null) return incompatEnchs;

        for(String configLine : ConfigHandler.incompatibleGroups) {
            if(configLine.contains(regName.getPath())) {
                //Assumes that config lines are enchantments separated by comma
                String[] enchsInList = configLine.split(EnchantmentControl.SEP);
                for(String lineEntry : enchsInList) {
                    lineEntry = lineEntry.trim();
                    if(lineEntry.isEmpty()) continue;
                    Enchantment incompatEnch = Enchantment.getEnchantmentByLocation(lineEntry);
                    if(incompatEnch == null) EnchantmentControl.LOGGER.warn("could not find incompatible enchantment {}", lineEntry);
                    else incompatEnchs.add(incompatEnch);
                }
            }
        }
        // remove the calling enchant
        // every enchantment is incompatible with itself, this is handled by Enchantment class directly though
        // and thus doesn't need to be in this list
        incompatEnchs.remove(thisEnch);

        return incompatEnchs;
    }
}
