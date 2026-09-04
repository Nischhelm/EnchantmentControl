package enchantmentcontrol.mixin.vanilla;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import enchantmentcontrol.EnchantmentControl;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.world.storage.loot.functions.EnchantRandomly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(EnchantRandomly.Serializer.class)
public abstract class EnchantRandomlyMixin {
    @Definition(id = "enchantment", local = @Local(type = Enchantment.class))
    @Expression("enchantment == null")
    @ModifyExpressionValue(
            method = "deserialize(Lcom/google/gson/JsonObject;Lcom/google/gson/JsonDeserializationContext;[Lnet/minecraft/world/storage/loot/conditions/LootCondition;)Lnet/minecraft/world/storage/loot/functions/EnchantRandomly;",
            at = @At(value = "MIXINEXTRAS:EXPRESSION")
    )
    private static boolean ec_skipUnknown(boolean original, @Local(ordinal = 0) String s) {
        if(original)
            EnchantmentControl.LOGGER.debug("EnchantmentControl prevented unknown enchantment {} in a loot function from failing the entire loottable", s);
        return false; //never throw an error
    }

    @WrapOperation(
            method = "deserialize(Lcom/google/gson/JsonObject;Lcom/google/gson/JsonDeserializationContext;[Lnet/minecraft/world/storage/loot/conditions/LootCondition;)Lnet/minecraft/world/storage/loot/functions/EnchantRandomly;",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z")
    )
    private boolean ec_dontAddNullEnchants(List<Enchantment> list, Object enchantment, Operation<Boolean> original){
        if(enchantment != null) return original.call(list, enchantment);
        return false;
    }
}
