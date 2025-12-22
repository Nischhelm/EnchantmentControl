package enchantmentcontrol.config.provider;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.util.enchantmenttypes.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Set;

public class ItemTypeConfigProvider {
    private static final HashMap<String, ITypeMatcher> typeMatchers = new HashMap<>();

    public static void resetCanApply(){
        typeMatchers.clear();
        initCanApply();
    }

    public static void initCanApply() {
        //These do have an associated fake enchant
        typeMatchers.put("ALL_TYPES", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.ALL));
        typeMatchers.put("ARMOR", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.ARMOR));
        typeMatchers.put("ARMOR_HEAD", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.ARMOR_HEAD));
        typeMatchers.put("ARMOR_CHEST", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.ARMOR_CHEST));
        typeMatchers.put("ARMOR_LEGS", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.ARMOR_LEGS));
        typeMatchers.put("ARMOR_FEET", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.ARMOR_FEET));
        typeMatchers.put("SWORD", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.WEAPON));
        typeMatchers.put("TOOL", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.DIGGER));
        typeMatchers.put("FISHING_ROD", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.FISHING_ROD));
        typeMatchers.put("BREAKABLE", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.BREAKABLE));
        typeMatchers.put("BOW", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.BOW));
        typeMatchers.put("WEARABLE", new EnumEnchantmentTypeMatcher(EnumEnchantmentType.WEARABLE));

        //These don't have an associated fake enchant
        typeMatchers.put("ANY", new BooleanTypeMatcher("ANY", true));
        typeMatchers.put("AXE", new InstanceofTypeMatcher("AXE", ItemAxe.class));
        typeMatchers.put("PICKAXE", new InstanceofTypeMatcher("PICKAXE", ItemPickaxe.class));
        typeMatchers.put("HOE", new InstanceofTypeMatcher("HOE", ItemHoe.class));
        typeMatchers.put("SHOVEL", new InstanceofTypeMatcher("SHOVEL", ItemSpade.class));
        typeMatchers.put("SHIELD", new InstanceofTypeMatcher("SHIELD", ItemShield.class));
        typeMatchers.put("NONE", new BooleanTypeMatcher("NONE", false));

        for (String s : ConfigHandler.itemTypes.customTypes) {
            CustomTypeMatcher c = new CustomTypeMatcher(s);
            if (c.isValid()) typeMatchers.put(c.getName(), c);
        }
    }

    public static boolean canItemApply(Enchantment enchantment, Set<String> enchantConfig, ItemStack stack){
        Item item = stack.getItem();
        boolean isValid = false;
        boolean invertedMatches = false;
        String itemName = null;

        for(String s: enchantConfig){
            //Configs can list types starting with ! to disable those
            boolean inverted = false;
            if(s.startsWith("!")){
                inverted = true;
                s = s.substring(1);
            }

            ITypeMatcher typeMatcher = typeMatchers.getOrDefault(s, null);
            if(typeMatcher == null) {
                EnchantmentControl.LOGGER.info("Could not find given item type {}", s);
                continue;
            }

            //First time check of a custom type: get item name
            if(typeMatcher instanceof CustomTypeMatcher && itemName == null) {
                ResourceLocation loc = item.getRegistryName();
                if (loc != null) itemName = loc.toString();
                else itemName = ""; //edge case shouldn't match anything
            }

            boolean matches = typeMatcher.matches(enchantment, stack, item, itemName);

            if(!inverted) isValid = isValid || matches;
            else invertedMatches = invertedMatches || matches;
        }
        return isValid && !invertedMatches;
    }

    //TODO: infer types of all current enchants and write to individual jsons during first setup
}
