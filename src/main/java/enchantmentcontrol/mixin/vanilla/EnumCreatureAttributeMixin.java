package enchantmentcontrol.mixin.vanilla;

import enchantmentcontrol.config.provider.CreatureAttributeProvider;
import net.minecraft.entity.EnumCreatureAttribute;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(EnumCreatureAttribute.class)
public abstract class EnumCreatureAttributeMixin {
    @SuppressWarnings("target") @Shadow @Final @Mutable private static EnumCreatureAttribute[] $VALUES;

    @Invoker("<init>") private static EnumCreatureAttribute ec_invokeInit(String internalName, int internalID){ throw new AssertionError("EnchantmentControl couldn't find constructor for EnumCreatureAttribute");}

    static {
        CreatureAttributeProvider.registerAttributes(EnumCreatureAttributeMixin::ec$addEnumCreatureAttribute);
    }

    @Unique
    private static EnumCreatureAttribute ec$addEnumCreatureAttribute(String name){
        switch (name) { //vanilla attributes don't need to be constructed
            case "UNDEFINED": return EnumCreatureAttribute.UNDEFINED;
            case "UNDEAD": return EnumCreatureAttribute.UNDEAD;
            case "ILLAGER": return EnumCreatureAttribute.ILLAGER;
            case "ARTHROPOD": return EnumCreatureAttribute.ARTHROPOD;
        }
        List<EnumCreatureAttribute> variants = new ArrayList<>(Arrays.asList($VALUES));
        EnumCreatureAttribute newRarity = ec_invokeInit(name, ec$nextId(variants));
        variants.add(newRarity);
        $VALUES = variants.toArray(new EnumCreatureAttribute[0]);
        return newRarity;
    }

    @Unique
    private static int ec$nextId(List<EnumCreatureAttribute> variants){
        return variants.get(variants.size()-1).ordinal()+1;
    }
}
