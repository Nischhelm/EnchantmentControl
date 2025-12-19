package enchantmentcontrol.mixin.vanilla;

import enchantmentcontrol.util.EnchantmentInfo;
import enchantmentcontrol.util.IEnchantmentPropertySetter;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Enchantment.class)
public abstract class BaseEnchantmentMixin implements IEnchantmentPropertySetter {
    @Shadow @Final @Mutable private EntityEquipmentSlot[] applicableEquipmentTypes;
    @Shadow @Final @Mutable private Enchantment.Rarity rarity;

    @Override
    public void ec$setSlots(EntityEquipmentSlot... newSlots) {
        this.applicableEquipmentTypes = newSlots;
    }

    @Override
    public void ec$setRarity(Enchantment.Rarity newRarity) {
        this.rarity = newRarity;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void ec_modifyRaritySlots(Enchantment.Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots, CallbackInfo ci){
        EnchantmentInfo info = EnchantmentInfo.get((Enchantment) (Object) this); //TODO check: "this" in constructor works?
        if(info == null) return;
        if(info.slots != null) this.ec$setSlots(info.slots.toArray(new EntityEquipmentSlot[0]));
        if(info.rarity != null) this.ec$setRarity(info.rarity);
    }
}