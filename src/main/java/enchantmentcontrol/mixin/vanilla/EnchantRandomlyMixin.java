package enchantmentcontrol.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import enchantmentcontrol.core.EnchantmentDummy;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.world.storage.loot.functions.EnchantRandomly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(EnchantRandomly.Serializer.class)
public abstract class EnchantRandomlyMixin {
    @ModifyVariable(
            method = "deserialize(Lcom/google/gson/JsonObject;Lcom/google/gson/JsonDeserializationContext;[Lnet/minecraft/world/storage/loot/conditions/LootCondition;)Lnet/minecraft/world/storage/loot/functions/EnchantRandomly;",
            at = @At(value = "LOAD", ordinal = 0),
            name = "enchantment"
    )
    private Enchantment ec_dontThrow(Enchantment enchantment) {
        if(enchantment != null) return enchantment;

        //first injection point -> fail check, don't throw
        return EnchantmentDummy.dummy;
    }

    @WrapOperation(
            method = "deserialize(Lcom/google/gson/JsonObject;Lcom/google/gson/JsonDeserializationContext;[Lnet/minecraft/world/storage/loot/conditions/LootCondition;)Lnet/minecraft/world/storage/loot/functions/EnchantRandomly;",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z")
    )
    private boolean ec_dontAddNull(List<Object> instance, Object e, Operation<Boolean> original){
        if(e == null) return false; //skip null enchantments
        return original.call(instance, e);
    }
}
