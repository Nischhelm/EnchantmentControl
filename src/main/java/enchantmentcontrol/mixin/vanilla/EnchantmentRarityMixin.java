package enchantmentcontrol.mixin.vanilla;

import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.config.provider.RarityProvider;
import net.minecraft.enchantment.Enchantment;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Debug(export = true)
@Mixin(Enchantment.Rarity.class)
public abstract class EnchantmentRarityMixin {
    @SuppressWarnings("target") @Shadow @Final @Mutable private static Enchantment.Rarity[] $VALUES;
    @Shadow @Final @Mutable private int weight;

    @Invoker("<init>") private static Enchantment.Rarity ec_invokeInit(String internalName, int internalId, int weight){ throw new AssertionError("EnchantmentControl couldn't find constructor for Enchantment Rarity enum");}

    static {
        RarityProvider.registerRarities(EnchantmentRarityMixin::ec$addEnchantmentRarity); //TODO: isnt this too early for config?
    }

    @Inject(method = "<init>(Ljava/lang/String;II)V", at = @At("TAIL"))
    private void ec_overrideRarityWeights(String internalName, int internalId, int weight, CallbackInfo ci){
        this.weight = ConfigHandler.rarityWeights.getOrDefault(internalName, weight);
    }

    @Unique
    private static Enchantment.Rarity ec$addEnchantmentRarity(String name){
        switch (name) { //vanilla rarities don't need to be constructed
            case "COMMON": return Enchantment.Rarity.COMMON;
            case "UNCOMMON": return Enchantment.Rarity.UNCOMMON;
            case "RARE": return Enchantment.Rarity.RARE;
            case "VERY_RARE": return Enchantment.Rarity.VERY_RARE;
        }
        List<Enchantment.Rarity> variants = new ArrayList<>(Arrays.asList($VALUES));
        Enchantment.Rarity newRarity = ec_invokeInit(name, ec$nextId(variants), 0); //weight is applied in init
        variants.add(newRarity);
        $VALUES = variants.toArray(new Enchantment.Rarity[0]);
        return newRarity;
    }

    @Unique
    private static int ec$nextId(List<Enchantment.Rarity> variants){
        return variants.get(variants.size()-1).ordinal()+1;
    }
}
