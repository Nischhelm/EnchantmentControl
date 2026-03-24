package enchantmentcontrol.mixin.vanilla.blacklists;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.EarlyConfigReader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Enchantment.class)
public abstract class VanillaEnchantmentRegistryMixin {

    @WrapWithCondition(method = "registerEnchantments", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/RegistryNamespaced;register(ILjava/lang/Object;Ljava/lang/Object;)V"))
    private static boolean onRegister(RegistryNamespaced<ResourceLocation, Enchantment> instance, int id, Object loc, Object ench) {
        if(EarlyConfigReader.getRegistrationBlacklist().isEmpty()) return true;

        //Prevent registration of config defined enchants
        if (EarlyConfigReader.getRegistrationBlacklist().contains(loc.toString())) {
            EnchantmentControl.LOGGER.info("Preventing registration of enchantment {}", loc.toString());
            return false;
        }
        return true;
    }
}

