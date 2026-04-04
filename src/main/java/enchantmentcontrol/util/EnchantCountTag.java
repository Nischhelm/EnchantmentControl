package enchantmentcontrol.util;

import enchantmentcontrol.config.ConfigHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnchantCountTag {
    public static final String KEY_ENCHANT_COUNT = "EC_EnchantCount";

    public static int getEnchantCount(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(KEY_ENCHANT_COUNT, 3)
                ? stack.getTagCompound().getInteger(KEY_ENCHANT_COUNT)
                : 0;
    }

    public static void setEnchantCount(ItemStack stack, int count) {
        NBTTagCompound tagCompound = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        tagCompound.setInteger(KEY_ENCHANT_COUNT, count);
        stack.setTagCompound(tagCompound);
    }

    public static List<EnchantmentData> reenchant(List<EnchantmentData> rolledEnchants, ItemStack stack, boolean simulate) {
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
        boolean mapChanged = false;
        List<EnchantmentData> list = new ArrayList<>();
        for(EnchantmentData data : rolledEnchants) {
            if(map.containsKey(data.enchantment)) {
                //already in
                if(ConfigHandler.etable.reEnchantCombineExisting){
                    int existingLvl = map.get(data.enchantment);
                    int newLvl = data.enchantmentLevel;
                    int resultLvl = Math.max(existingLvl, newLvl) + (existingLvl == newLvl ? 1 : 0); //Like anvil
                    map.put(data.enchantment, resultLvl);
                    mapChanged = true;
                }

            } else if (ConfigHandler.etable.reEnchantAllowIncompatible || map.keySet().stream().allMatch(origE -> origE.isCompatibleWith(data.enchantment)))
                list.add(data);
        }
        if(!simulate && mapChanged) EnchantmentHelper.setEnchantments(map, stack);
        return list;
    }
}
