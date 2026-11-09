package enchantmentcontrol.util;

import enchantmentcontrol.config.ConfigHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ReEnchantUtil {
    public static final String KEY_ENCHANT_COUNT = "EC_EnchantCount";

    public static int getEnchantCount(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(KEY_ENCHANT_COUNT, 3)
                ? stack.getTagCompound().getInteger(KEY_ENCHANT_COUNT)
                : 0;
    }

    public static void incrementEnchantCount(Random rand, ItemStack stack) {
        if(rand.nextFloat() > ConfigHandler.etable.reEnchant.skipIncrementChance)
            setEnchantCount(stack, ReEnchantUtil.getEnchantCount(stack) + 1);
    }

    public static void setEnchantCount(ItemStack stack, int count) {
        if(count == 0 && getEnchantCount(stack) == 0) return;
        NBTTagCompound tagCompound = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        tagCompound.setInteger(KEY_ENCHANT_COUNT, count);
        stack.setTagCompound(tagCompound);
    }

    /**
     * Filters rolled enchants for an already enchanted stack
     * and removes enchants incompatible with the existing ones / enchants the stack already has.
     * Exception: If "Combine Existing" is enabled and allowed, existing enchants are instead kept at the level they will have after combining.
     */
    public static List<EnchantmentData> reenchantIfPossible(List<EnchantmentData> rolledEnchants, ItemStack stack, boolean allowCombine) {
        if(!stack.isItemEnchanted()) return rolledEnchants;
        boolean combine = allowCombine && ConfigHandler.etable.reEnchant.combineExisting;
        Map<Enchantment, Integer> existing = EnchantmentHelper.getEnchantments(stack);
        List<EnchantmentData> list = new ArrayList<>();
        NBTTagList tagList = stack.getEnchantmentTagList();
        for(EnchantmentData data : rolledEnchants) {
            if(existing.containsKey(data.enchantment)) {
                if(!combine) continue;
                //Read from the first matching entry (the one that gets combined), the map would give the last one if there are duplicates
                int existingLvl = findExistingEntry(tagList, data.enchantment).getShort("lvl");
                //Anvil logic (capped at max lvl), dropped if it wouldn't raise the existing lvl (lower roll, or already at/above max)
                int combinedLvl = Math.max(existingLvl, data.enchantmentLevel) + (existingLvl == data.enchantmentLevel ? 1 : 0);
                combinedLvl = Math.min(combinedLvl, data.enchantment.getMaxLevel());
                if(combinedLvl > existingLvl)
                    list.add(new EnchantmentData(data.enchantment, combinedLvl));
            } else if(ConfigHandler.etable.reEnchant.allowIncompatible || existing.keySet().stream().allMatch(origE -> origE == null || origE.isCompatibleWith(data.enchantment)))
                list.add(data);
        }
        return list;
    }

    // Happens right when the enchants are actually added to the item
    public static boolean combineNewLevelWithExisting(ItemStack stack, Enchantment enchantment, int newLevel) {
        if(!stack.isItemEnchanted()) return false;
        NBTTagCompound existing = findExistingEntry(stack.getEnchantmentTagList(), enchantment);
        if(existing == null) return false;

        // take the higher one
        if(existing.getShort("lvl") < newLevel) existing.setShort("lvl", (short) newLevel);
        return true; // don't add normally
    }

    private static NBTTagCompound findExistingEntry(NBTTagList tagList, Enchantment enchantment) {
        int id = Enchantment.getEnchantmentID(enchantment);
        for(int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound entry = tagList.getCompoundTagAt(i);
            if(entry.getShort("id") == id) return entry;
        }
        return null;
    }
}
