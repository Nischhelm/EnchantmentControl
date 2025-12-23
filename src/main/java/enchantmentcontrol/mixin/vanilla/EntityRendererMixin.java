package enchantmentcontrol.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    @ModifyExpressionValue(
            method = "setupFog",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getRespirationModifier(Lnet/minecraft/entity/EntityLivingBase;)I")
    )
    private int ec_entityRenderer_setupFog_capRespirationModifier(int original) {
        return Math.min(3, original); //cap to max 3 otherwise it looks shit
    }
}
