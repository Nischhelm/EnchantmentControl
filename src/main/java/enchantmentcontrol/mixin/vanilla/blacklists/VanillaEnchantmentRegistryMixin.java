package enchantmentcontrol.mixin.vanilla.blacklists;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.ConfigHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Enchantment.class)
public abstract class VanillaEnchantmentRegistryMixin {

    @WrapWithCondition(method = "registerEnchantments", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/RegistryNamespaced;register(ILjava/lang/Object;Ljava/lang/Object;)V"))
    private static boolean onRegister(RegistryNamespaced<ResourceLocation, Enchantment> instance, int id, Object loc, Object ench) {
        //Prevent registration of config defined enchants
        if (ConfigHandler.blacklists.blacklistedRegistryEnchants.contains(loc.toString())) {
            EnchantmentControl.LOGGER.info("Preventing registration of enchantment {}", loc.toString());
            return false;
        }
        return true;
    }
}

