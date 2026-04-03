package enchantmentcontrol.mixin.vanilla.etable;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.util.EnchantmentNameParts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiEnchantment.class)
public abstract class GuiEnchantmentMixin_AlwaysReroll {
    @WrapWithCondition(
            method = "drawGuiContainerBackgroundLayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/EnchantmentNameParts;reseedRandomGenerator(J)V")
    )
    private boolean ec_alwaysReroll(EnchantmentNameParts instance, long seed){
        return false;
    }
}
