package enchantmentcontrol.util.enchantmenttypes;

import enchantmentcontrol.config.ConfigHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EnumEnchantmentTypeMatcher implements ITypeMatcher {
    private final EnumEnchantmentType type;
    private final String name;

    public EnumEnchantmentTypeMatcher(String name, EnumEnchantmentType type){
        this.name = name;
        this.type = type;
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
            boolean doesMatch = item.canApplyAtEnchantingTable(stack, enchantment); //TODO: are there really no weird edge cases here?
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

    @Override
    public String getName() {
        return this.name;
    }
}
