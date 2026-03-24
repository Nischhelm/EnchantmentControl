package enchantmentcontrol.core;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

//Only needs to extend Enchantment to fix refmaps of EnchantmentMixin
//also used for replacing unregistered vanilla enchantments
public class EnchantmentDummy extends Enchantment {
    public static final Enchantment dummy = new EnchantmentDummy(Rarity.COMMON, EnumEnchantmentType.ALL, new EntityEquipmentSlot[0]).setRegistryName("enchantmentcontrol:dummy");
    protected EnchantmentDummy(Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots) {
        super(rarityIn, typeIn, slots);
    }
}