package enchantmentcontrol.mixin.vanilla.accessor;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Enchantment.class)
public interface EnchantmentAccessor {
    @Accessor("applicableEquipmentTypes")
    EntityEquipmentSlot[] getSlots();
}
