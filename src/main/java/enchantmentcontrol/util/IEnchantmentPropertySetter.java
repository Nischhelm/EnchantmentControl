package enchantmentcontrol.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;

public interface IEnchantmentPropertySetter {
    void ec$setSlots(EntityEquipmentSlot[] newSlots);
    void ec$setRarity(Enchantment.Rarity newRarity);
}
