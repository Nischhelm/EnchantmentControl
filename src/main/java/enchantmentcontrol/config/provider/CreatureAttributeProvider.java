package enchantmentcontrol.config.provider;

import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.config.provider.matchers.ClassMatcher;
import enchantmentcontrol.config.provider.matchers.IEntityMatcher;
import enchantmentcontrol.config.provider.matchers.ListMatcher;
import enchantmentcontrol.config.provider.matchers.ModIdListMatcher;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CreatureAttributeProvider {
    private static Map<EnumCreatureAttribute, IEntityMatcher> attributes = new HashMap<>(); //gets overwritten during EnumCreatureAttribute.<clinit>

    public static void registerAttributes(Function<String, EnumCreatureAttribute> constructor) {
        attributes = new HashMap<>();
        ConfigHandler.creatureAttributes.forEach((attributeName, attr) -> {
            if(attributeName.isEmpty()) return;
            if(attr.values.isEmpty()) return;
            IEntityMatcher matcher;
            switch (attr.type) {
                case MODID: matcher = new ModIdListMatcher(attr.values); break;
                case CLASS: matcher = new ClassMatcher(attr.values); break;
                case MOB: default: matcher = new ListMatcher(attr.values); break;
            }

            EnumCreatureAttribute attribute = constructor.apply(attributeName);
            attributes.put(attribute, matcher);
        });
    }

    public static EnumCreatureAttribute getAttribute(EntityLivingBase entity) {
        ResourceLocation loc = EntityList.getKey(entity);
        if(loc == null) return EnumCreatureAttribute.UNDEFINED;
        for (Map.Entry<EnumCreatureAttribute, IEntityMatcher> entry : attributes.entrySet())
            if (entry.getValue().matches(entity, loc))
                return entry.getKey();
        return EnumCreatureAttribute.UNDEFINED;
    }

}
