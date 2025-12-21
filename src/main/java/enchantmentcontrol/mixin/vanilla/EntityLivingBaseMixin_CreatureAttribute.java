package enchantmentcontrol.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.provider.CreatureAttributeProvider;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Debug(export = true)
@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin_CreatureAttribute {
    @ModifyReturnValue(method = "getCreatureAttribute", at = @At("RETURN"))
    private EnumCreatureAttribute ec_modifyUndefinedCreatureAttributes(EnumCreatureAttribute original){
        if(original != EnumCreatureAttribute.UNDEFINED) return original; //yield to other modifications
        EnumCreatureAttribute newAttr = CreatureAttributeProvider.getAttribute((EntityLivingBase) (Object) this);
        EnchantmentControl.LOGGER.info("Creature attribute of entity {} is {}", this.getClass().getSimpleName(), newAttr);
        return newAttr;
    }
}
