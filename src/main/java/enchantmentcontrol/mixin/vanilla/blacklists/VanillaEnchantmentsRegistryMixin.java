package enchantmentcontrol.mixin.vanilla.blacklists;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import enchantmentcontrol.core.EnchantmentDummy;
import net.minecraft.init.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Enchantments.class)
public abstract class VanillaEnchantmentsRegistryMixin {

    @ModifyExpressionValue(method = "getRegisteredEnchantment", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/RegistryNamespaced;getObject(Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object onRegister(Object original) {
        if(original == null) return EnchantmentDummy.dummy;
        return original;
    }
}

