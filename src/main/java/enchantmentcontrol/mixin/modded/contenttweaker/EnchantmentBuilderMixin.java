package enchantmentcontrol.mixin.modded.contenttweaker;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.teamacronymcoders.contenttweaker.modules.vanilla.enchantments.EnchantmentBuilder;
import crafttweaker.api.enchantments.IEnchantmentDefinition;
import enchantmentcontrol.compat.crafttweaker.CT_EnchantmentInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentBuilder.class)
public abstract class EnchantmentBuilderMixin {
    @ModifyReturnValue(
            method = "create",
            at = @At(value = "RETURN"),
            remap = false
    )
    private static EnchantmentBuilder ec_createExtension(EnchantmentBuilder createdBUilder) {
        CT_EnchantmentInfo.onBuilderCreate(createdBUilder);
        return createdBUilder;
    }

    @Inject(
            method = "register",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/registries/IForgeRegistry;register(Lnet/minecraftforge/registries/IForgeRegistryEntry;)V"),
            remap = false
    )
    private void ec_registerExtension(CallbackInfoReturnable<IEnchantmentDefinition> cir) {
        CT_EnchantmentInfo.onBuilderRegister((EnchantmentBuilder) (Object) this);
    }
}
