package enchantmentcontrol.util.enchantmenttypes;

import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.config.provider.ItemTypeConfigProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.*;

public class EnumEnchantmentTypeMatcher implements ITypeMatcher {
    private static final Map<EnumEnchantmentType, EnumEnchantmentTypeMatcher> enchantToTypeMatchers = new HashMap<>();
    public static List<ITypeMatcher> byEnum(EnumEnchantmentType type){
        if(type.ordinal() > 11){ //not vanilla enum
            switch (type.name()) {
                //SME 0.5.5 first tries in making lists of types
                case "Combat Shield": return Collections.singletonList(ItemTypeConfigProvider.getMatcher("SHIELD"));
                case "Tool Pickaxe": return Collections.singletonList(ItemTypeConfigProvider.getMatcher("PICKAXE"));
                case "Tool Hoe": return Collections.singletonList(ItemTypeConfigProvider.getMatcher("HOE"));
                case "Damageable": return byEnum(EnumEnchantmentType.BREAKABLE);
                case "Combat Tool": return byEnum(EnumEnchantmentType.DIGGER);
                case "Combat Sword": return Collections.singletonList(enchantToTypeMatchers.get(EnumEnchantmentType.WEAPON));
                case "Combat Axe": return Collections.singletonList(ItemTypeConfigProvider.getMatcher("AXE"));
                case "None": return Collections.singletonList(ItemTypeConfigProvider.getMatcher("NONE"));
                case "All": return Collections.singletonList(ItemTypeConfigProvider.getMatcher("ANY"));
                case "All Tools": return Arrays.asList(enchantToTypeMatchers.get(EnumEnchantmentType.DIGGER), enchantToTypeMatchers.get(EnumEnchantmentType.WEAPON));
                case "Combat": return Arrays.asList(enchantToTypeMatchers.get(EnumEnchantmentType.WEAPON), ItemTypeConfigProvider.getMatcher("AXE"));
                case "Combat Weapon": return Arrays.asList(enchantToTypeMatchers.get(EnumEnchantmentType.BOW), enchantToTypeMatchers.get(EnumEnchantmentType.WEAPON), ItemTypeConfigProvider.getMatcher("AXE"));
            }
        }
        return Collections.singletonList(enchantToTypeMatchers.computeIfAbsent(type, EnumEnchantmentTypeMatcher::new));
    }

    private final EnumEnchantmentType type;
    private final String name;

    public EnumEnchantmentTypeMatcher(String name, EnumEnchantmentType type){
        this.name = name;
        this.type = type;
        enchantToTypeMatchers.put(type, this);
    }

    public EnumEnchantmentTypeMatcher(EnumEnchantmentType type){
        this(type.toString(), type);
    }

    @Override
    public boolean matches(Enchantment enchantment, ItemStack stack, Item item, String itemName){
        // This tries to catch all items that pretend to be normal MC items without inheriting from them
        // which then try to get the correct enchantments by overriding item.canApplyAtEnchantingTable(enchantment) using
        // enchantment.type == myPretended_vanillaEnumEnchantment_type

        // The main issue why we cant use the normal system is that vanilla only allows one type per enchant
        if(ConfigHandler.itemTypes.allowCustomItems) {
            EnumEnchantmentType tmpType = enchantment.type;
            enchantment.type = this.type;
            boolean doesMatch = item.canApplyAtEnchantingTable(stack, enchantment);
            enchantment.type = tmpType;

            return doesMatch;
        }
        else return type.canEnchantItem(item);
    }

    @Override
    public ItemStack getFakeStack(){
        Item item = null;
        switch (this.type) {
            case ARMOR_HEAD: item = Items.IRON_HELMET; break;
            case ARMOR_CHEST: item = Items.IRON_CHESTPLATE; break;
            case ARMOR_LEGS: item = Items.IRON_LEGGINGS; break;
            case ARMOR_FEET: item = Items.IRON_BOOTS; break;
            case FISHING_ROD: item = Items.FISHING_ROD; break;
            case WEAPON: item = Items.IRON_SWORD; break;
            case DIGGER: item = Items.IRON_PICKAXE; break;
            case BOW: item = Items.BOW; break;
        }
        if(item != null) return new ItemStack(item);
        return null;
    }

    public EnumEnchantmentType getType() {
        return this.type;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
