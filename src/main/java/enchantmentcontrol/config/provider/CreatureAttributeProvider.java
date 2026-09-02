package enchantmentcontrol.config.provider;

import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.util.matchers.IMatcher;
import enchantmentcontrol.util.matchers.MatcherCreator;
import enchantmentcontrol.util.matchers.context.EntityMatcherContext;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CreatureAttributeProvider {
    private static Map<EnumCreatureAttribute, IMatcher<EntityMatcherContext>> attributes = new HashMap<>(); //gets overwritten during EnumCreatureAttribute.<clinit>

    public static void registerAttributes(Function<String, EnumCreatureAttribute> constructor) {
        attributes = new HashMap<>();
        ConfigHandler.advanced.creatureAttributes.forEach((attributeName, attr) -> {
            if(attributeName.isEmpty()) return;
            if(attr.values.isEmpty()) return;

            IMatcher<EntityMatcherContext> matcher = MatcherCreator.ENTITY.createMatcher(attr.values, attr.type);
            EnumCreatureAttribute attribute = constructor.apply(attributeName);
            attributes.put(attribute, matcher);
        });
    }

    public static EnumCreatureAttribute getAttribute(EntityLivingBase entity) {
        ResourceLocation loc = EntityList.getKey(entity);
        if(loc == null) return EnumCreatureAttribute.UNDEFINED;
        EntityMatcherContext context = new EntityMatcherContext(entity, loc);
        for (Map.Entry<EnumCreatureAttribute, IMatcher<EntityMatcherContext>> entry : attributes.entrySet())
            if (entry.getValue().matches(context))
                return entry.getKey();
        return EnumCreatureAttribute.UNDEFINED;
    }

}
