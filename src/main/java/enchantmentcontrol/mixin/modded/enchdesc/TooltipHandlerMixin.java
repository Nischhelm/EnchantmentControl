package enchantmentcontrol.mixin.modded.enchdesc;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.darkhax.enchdesc.client.TooltipHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Arrays;
import java.util.List;

@Mixin(TooltipHandler.class)
public abstract class TooltipHandlerMixin {
    @WrapOperation(
            method = "onTooltipDisplayed",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 1),
            remap = false
    )
    private boolean ec_addLineBreaks(List<String> tooltips, Object obj, Operation<Boolean> original){
        String desc = (String) obj;
        if(desc.contains("\\n"))
            return tooltips.addAll(Arrays.asList(desc.split("\\\\n")));

        return original.call(tooltips, desc);
    }
}
