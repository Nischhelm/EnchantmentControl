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

    @Shadow
    public static EnumCreatureAttribute[] values() {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Invoker("<init>") private static EnumCreatureAttribute ec_invokeInit(String internalName, int internalID){ throw new AssertionError("EnchantmentControl couldn't find constructor for EnumCreatureAttribute");}

    static {
        // basically injected into <clinit> = first classload of EnumCreatureAttribute
        CreatureAttributeProvider.registerAttributes(EnumCreatureAttributeMixin::ec$addEnumCreatureAttribute);
    }

    @Unique
    private static EnumCreatureAttribute ec$addEnumCreatureAttribute(String name){
        for(EnumCreatureAttribute attr : EnumCreatureAttribute.values()) //existing attributes don't need to be constructed
            if(attr.name().equals(name))
                return attr;

        List<EnumCreatureAttribute> variants = new ArrayList<>(Arrays.asList($VALUES));
        EnumCreatureAttribute newAttr = ec_invokeInit(name, ec$nextId(variants));
        variants.add(newAttr);
        $VALUES = variants.toArray(new EnumCreatureAttribute[0]);
        return newAttr;
    }

    @Unique
    private static int ec$nextId(List<EnumCreatureAttribute> variants){
        return variants.get(variants.size()-1).ordinal()+1;
    }
}
