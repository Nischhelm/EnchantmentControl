package enchantmentcontrol.util;

import enchantmentcontrol.config.ConfigHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class AnvilCostUtil {
    private static final String key = "AnvilCount";

    public static int getAnvilCount(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(key, 3) ? stack.getTagCompound().getInteger(key) : 0;
    }

    public static void setAnvilCount(ItemStack stack, int count) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger(key, count);
    }

    public static int combineCounts(int useCountLeft, int useCountRight) {
        switch(ConfigHandler.anvil.repairCostCombinationType) {
            case MIN: //prob doesn't make sense to use but whatever
                return Math.min(useCountLeft, useCountRight);
            case SUM:
                return useCountLeft + useCountRight;
            case AVERAGE:
                return (useCountLeft + useCountRight) / 2;
            case MAX: default:
                return Math.max(useCountLeft, useCountRight);
        }
    }

    public static int combineCosts(int repairCostLeft, int repairCostRight) {
        switch(ConfigHandler.anvil.repairCostCombinationType) {
            case MIN: //prob doesn't make sense to use but whatever
                return Math.min(repairCostLeft, repairCostRight);
            case SUM:
                return repairCostLeft + repairCostRight;
            case AVERAGE:
                return (repairCostLeft + repairCostRight) / 2;
            case MAX: default:
                return Math.max(repairCostLeft, repairCostRight);
        }
    }

    public static int getRarityMultiplier(Enchantment.Rarity rarity, boolean isBook) {
        switch (rarity) {
            case COMMON:
                return 1;
            case UNCOMMON:
                return isBook ? 1 : 2;
            case RARE:
                return isBook ? 2 : 4;
            case VERY_RARE: default:
                return isBook ? 4 : 8;
        }
    }
}
