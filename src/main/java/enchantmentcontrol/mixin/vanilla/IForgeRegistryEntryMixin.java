package enchantmentcontrol.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.ConfigHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraftforge.registries.IForgeRegistryEntry$Impl")
public abstract class IForgeRegistryEntryMixin {
    @Shadow(remap = false) public abstract Class<?> getRegistryType();

    @ModifyExpressionValue(
            method = "setRegistryName(Ljava/lang/String;)Lnet/minecraftforge/registries/IForgeRegistryEntry;",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/registries/GameData;checkPrefix(Ljava/lang/String;Z)Lnet/minecraft/util/ResourceLocation;"),
            remap = false
    )
    private ResourceLocation ec_modifyRegistryNames(ResourceLocation original){
        if(this.getRegistryType() != Enchantment.class) return original;

        String remap = ConfigHandler.dev.idRemaps.get(original.toString());
        if(remap != null){
            EnchantmentControl.LOGGER.info("Remapping Enchantment {} to {}", original, remap);
            return new ResourceLocation(remap);
        }
        return original;
    }
}
