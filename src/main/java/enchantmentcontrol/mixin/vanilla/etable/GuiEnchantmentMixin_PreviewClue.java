package enchantmentcontrol.mixin.vanilla.etable;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.util.EnchantmentNameParts;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiEnchantment.class)
public abstract class GuiEnchantmentMixin_PreviewClue extends GuiContainer {
    @Shadow @Final private ContainerEnchantment container;

    public GuiEnchantmentMixin_PreviewClue(Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }

    @WrapWithCondition(
            method = "drawGuiContainerBackgroundLayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/EnchantmentNameParts;reseedRandomGenerator(J)V")
    )
    private boolean ec_previewClue(EnchantmentNameParts instance, long seed){
        return false;
    }

    @WrapOperation(
            method = "drawGuiContainerBackgroundLayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/EnchantmentNameParts;generateNewRandomName(Lnet/minecraft/client/gui/FontRenderer;I)Ljava/lang/String;")
    )
    private String ec_previewClue(EnchantmentNameParts instance, FontRenderer fontRendererIn, int length, Operation<String> original, @Local(name = "l") int l){
        int lvl = this.container.worldClue[l];
        Enchantment enchantment = Enchantment.getEnchantmentByID(this.container.enchantClue[l]);
        if(enchantment == null) return original.call(instance, fontRendererIn, length);
        return I18n.format(enchantment.getTranslatedName(lvl));
    }

    @ModifyReceiver(
            method = "drawGuiContainerBackgroundLayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawSplitString(Ljava/lang/String;IIII)V")
    )
    private FontRenderer ec_previewClue(FontRenderer instance, String str, int x, int y, int wrapWidth, int textColor){
        return mc.fontRenderer;
    }
}
