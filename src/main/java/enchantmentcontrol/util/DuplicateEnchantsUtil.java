package enchantmentcontrol.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.LinkedHashMap;
import java.util.Map;

public class DuplicateEnchantsUtil {
    /**
     * Merges multiple entries of the same enchantment id into one entry with the summed up level (Sharpness 3 + Sharpness 4 = Sharpness 7).
     * Keeps the order of first occurrence. Returns the given list itself if there are no duplicates, otherwise a new list.
     * The returned list is only meant to be read from.
     */
    public static NBTTagList mergeDuplicates(NBTTagList list) {
        int count = list.tagCount();
        if (count < 2 || !hasDuplicates(list)) return list;

        Map<Short, Integer> lvls = new LinkedHashMap<>();
        for (int i = 0; i < count; i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            lvls.merge(tag.getShort("id"), (int) tag.getShort("lvl"), Integer::sum);
        }

        NBTTagList merged = new NBTTagList();
        for (Map.Entry<Short, Integer> entry : lvls.entrySet()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setShort("id", entry.getKey());
            tag.setShort("lvl", (short) Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, entry.getValue())));
            merged.appendTag(tag);
        }
        return merged;
    }

    private static boolean hasDuplicates(NBTTagList list) {
        int count = list.tagCount();
        for (int i = 0; i < count; i++) {
            short id = list.getCompoundTagAt(i).getShort("id");
            for (int j = i + 1; j < count; j++)
                if (list.getCompoundTagAt(j).getShort("id") == id) return true;
        }
        return false;
    }
}
