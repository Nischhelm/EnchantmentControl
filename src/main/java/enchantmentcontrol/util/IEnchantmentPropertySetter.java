package enchantmentcontrol.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;

public interface IEnchantmentPropertySetter {
    void ec_setSlots(EntityEquipmentSlot[] newSlots);
    void ec_setRarity(Enchantment.Rarity newRarity);
}
