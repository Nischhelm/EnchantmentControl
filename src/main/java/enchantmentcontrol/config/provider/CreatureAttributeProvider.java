package enchantmentcontrol.config.provider;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.ConfigHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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

    public interface IEntityMatcher { boolean matches(Entity entity, ResourceLocation loc);}

    public static class ClassMatcher implements IEntityMatcher {
        private final Set<Class<? extends Entity>> entityClasses = new HashSet<>();

        @SuppressWarnings("unchecked")
        public ClassMatcher(Set<String> classNames) {
            classNames.forEach(className -> {
                try {
                    this.entityClasses.add((Class<? extends Entity>) Class.forName(className));
                } catch (ClassNotFoundException e) {
                    EnchantmentControl.LOGGER.warn("Could not find entity class {} for custom creature attribute", className);
                }
            });
        }

        @Override
        public boolean matches(Entity entity, ResourceLocation loc) {
            return !entityClasses.isEmpty() && entityClasses.stream().anyMatch(entityClass -> entityClass.isInstance(entity));
        }
    }

    public static class ListMatcher implements IEntityMatcher {
        protected final Set<String> ids;
        public ListMatcher(Set<String> ids) {this.ids = ids;}

        @Override
        public boolean matches(Entity entity, ResourceLocation loc) {
            return ids.contains(loc.toString());
        }
    }

    public static class ModIdListMatcher extends ListMatcher {
        public ModIdListMatcher(Set<String> ids) {super(ids);}

        @Override
        public boolean matches(Entity entity, ResourceLocation loc) {
            return ids.contains(loc.getNamespace());
        }
    }
}
