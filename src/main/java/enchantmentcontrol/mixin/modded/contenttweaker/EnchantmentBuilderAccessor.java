package enchantmentcontrol.mixin.modded.contenttweaker;

import com.teamacronymcoders.contenttweaker.modules.vanilla.enchantments.EnchantmentBuilder;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EnchantmentBuilder.class)
public interface EnchantmentBuilderAccessor {
    @Accessor("rarity")
    Enchantment.Rarity getRarity();
    @Accessor("type")
    EnumEnchantmentType getType();
}
