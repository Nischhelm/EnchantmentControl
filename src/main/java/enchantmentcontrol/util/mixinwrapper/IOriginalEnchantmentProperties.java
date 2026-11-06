package enchantmentcontrol.util.mixinwrapper;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

import javax.annotation.Nullable;

/**
 * The rarity and slots an enchantment was constructed with, and the type it had when it was registered
 * (subclasses like EnchantmentProtection set the public type field in their own constructor, after the Enchantment constructor).
 * Those are plain fields that other mods (and this one) overwrite at runtime, e.g. Enchantments Control does so lazily on first use of an enchant,
 * so the import of its config needs these to diff against.
 */
public interface IOriginalEnchantmentProperties {
    Enchantment.Rarity ec$getOriginalRarity();
    @Nullable EnumEnchantmentType ec$getOriginalType();
    EntityEquipmentSlot[] ec$getOriginalSlots();
    /** Called at the end of enchantment registration */
    void ec$captureOriginalType();
}
