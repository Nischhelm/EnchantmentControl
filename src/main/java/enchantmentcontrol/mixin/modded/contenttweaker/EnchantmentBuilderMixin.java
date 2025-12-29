package enchantmentcontrol.mixin.modded.contenttweaker;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamacronymcoders.contenttweaker.modules.vanilla.enchantments.CoTEnchantment;
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
    private static EnchantmentBuilder ec_createExtension(EnchantmentBuilder createdBuilder, String name) {
        CT_EnchantmentInfo.onBuilderCreate(createdBuilder, name);
        return createdBuilder;
    }

    @Inject(
            method = "register",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/registries/IForgeRegistry;register(Lnet/minecraftforge/registries/IForgeRegistryEntry;)V"),
            remap = false
    )
    private void ec_registerExtension(CallbackInfoReturnable<IEnchantmentDefinition> cir, @Local(name = "enchantment") CoTEnchantment enchant) {
        CT_EnchantmentInfo.onBuilderRegister((EnchantmentBuilder) (Object) this, enchant);
    }
}
