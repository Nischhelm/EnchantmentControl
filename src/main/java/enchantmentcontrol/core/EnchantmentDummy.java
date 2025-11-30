package enchantmentcontrol.core;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

//Only needs to extends Enchantment to fix refmaps of EnchantmentMixin
public class EnchantmentDummy extends Enchantment {
    protected EnchantmentDummy(Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots) {
        super(rarityIn, typeIn, slots);
    }
}