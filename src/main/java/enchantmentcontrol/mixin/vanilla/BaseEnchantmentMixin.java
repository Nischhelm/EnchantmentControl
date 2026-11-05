package enchantmentcontrol.mixin.vanilla;

import enchantmentcontrol.util.IEnchantmentPropertySetter;
import enchantmentcontrol.util.IOriginalEnchantmentProperties;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Enchantment.class)
public abstract class BaseEnchantmentMixin implements IEnchantmentPropertySetter, IOriginalEnchantmentProperties {
    @Shadow @Final @Mutable private EntityEquipmentSlot[] applicableEquipmentTypes;
    @Shadow @Final @Mutable private Enchantment.Rarity rarity;
    @Shadow public EnumEnchantmentType type;

    @Unique private Enchantment.Rarity ec$originalRarity;
    @Unique private EnumEnchantmentType ec$originalType;
    @Unique private boolean ec$originalTypeCaptured = false;
    @Unique private EntityEquipmentSlot[] ec$originalSlots;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void ec$storeOriginals(Enchantment.Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots, CallbackInfo ci) {
        // rarity and slots are private final, so subclasses can't change them after this. type is public and gets captured on registration instead
        this.ec$originalRarity = this.rarity;
        this.ec$originalSlots = this.applicableEquipmentTypes;
    }

    @Override
    public void ec$setSlots(EntityEquipmentSlot... newSlots) {
        this.applicableEquipmentTypes = newSlots;
    }

    @Override
    public void ec$setRarity(Enchantment.Rarity newRarity) {
        this.rarity = newRarity;
    }

    @Override
    public Enchantment.Rarity ec$getOriginalRarity() {
        return this.ec$originalRarity;
    }

    @Override
    public EnumEnchantmentType ec$getOriginalType() {
        return this.ec$originalTypeCaptured ? this.ec$originalType : this.type;
    }

    @Override
    public void ec$captureOriginalType() {
        this.ec$originalType = this.type;
        this.ec$originalTypeCaptured = true;
    }

    @Override
    public EntityEquipmentSlot[] ec$getOriginalSlots() {
        return this.ec$originalSlots;
    }
}
