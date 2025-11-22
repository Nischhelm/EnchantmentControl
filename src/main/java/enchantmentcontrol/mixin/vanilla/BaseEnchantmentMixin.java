package enchantmentcontrol.mixin.vanilla;

import enchantmentcontrol.util.IEnchantmentPropertySetter;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Enchantment.class)
public abstract class BaseEnchantmentMixin implements IEnchantmentPropertySetter {
    @Shadow @Final @Mutable private EntityEquipmentSlot[] applicableEquipmentTypes;
    @Shadow @Final @Mutable private Enchantment.Rarity rarity;

    @Override
    public void ec_setSlots(EntityEquipmentSlot... newSlots) {
        this.applicableEquipmentTypes = newSlots;
    }

    @Override
    public void ec_setRarity(Enchantment.Rarity newRarity) {
        this.rarity = newRarity;
    }
}