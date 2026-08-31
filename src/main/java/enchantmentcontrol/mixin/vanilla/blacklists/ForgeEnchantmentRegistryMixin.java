package enchantmentcontrol.mixin.vanilla.blacklists;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.ConfigHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeRegistry.class)
public abstract class ForgeEnchantmentRegistryMixin<V extends IForgeRegistryEntry<V>> {

    @Inject(method = "register", at = @At("HEAD"), cancellable = true, remap = false)
    private void onRegister(V value, CallbackInfo ci) {
        if (!(value instanceof Enchantment)) return;
        if(ConfigHandler.blacklists.blacklistedRegistryEnchants.isEmpty()) return;
        
        ResourceLocation loc = value.getRegistryName();
        if (loc == null) return;

        //Prevent registration of config defined enchants
        if (ConfigHandler.blacklists.blacklistedRegistryEnchants.contains(loc.toString())) {
            EnchantmentControl.LOGGER.info("Preventing registration of enchantment {}", loc.toString());
            ci.cancel();
        }
    }
}

