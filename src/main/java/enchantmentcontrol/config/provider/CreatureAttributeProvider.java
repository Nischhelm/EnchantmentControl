package enchantmentcontrol.config.provider;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.EarlyConfigReader;
import enchantmentcontrol.util.ConfigRef;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.util.ResourceLocation;

import java.util.*;
import java.util.function.Function;

public class CreatureAttributeProvider {
    private static Map<EnumCreatureAttribute, IEntityMatcher> attributes = new HashMap<>(); //gets overwritten during EnumCreatureAttribute.<clinit>

    public static void registerAttributes(Function<String, EnumCreatureAttribute> constructor) {
        attributes = EarlyConfigReader.readConfigMap(ConfigRef.CREAT_ATTR_CONFIG_NAME, constructor, value -> {
            String[] parts = value.split(",");
            if (parts.length < 2){
                EnchantmentControl.LOGGER.warn("Invalid creature attribute definition, this creature attribute will not work: {}", value);
                return new ModIdListMatcher(Collections.emptySet());
            }

            String type = parts[0].trim().toLowerCase();

            switch (type) {
                case "modid":
                    Set<String> modIds = new HashSet<>();
                    for (int i = 1; i < parts.length; i++)
                        modIds.add(parts[i].trim());
                    return new ModIdListMatcher(modIds);
                case "mob":
                    Set<String> registryNames = new HashSet<>();
                    for (int i = 1; i < parts.length; i++)
                        registryNames.add(parts[i].trim());
                    return new ListMatcher(registryNames);
                case "class":
                    return new ClassMatcher(parts[1].trim());
            }

            EnchantmentControl.LOGGER.warn("Invalid creature attribute definition, needs to start with \"mob\", \"modid\", or \"class\": {}", value);
            return new ModIdListMatcher(Collections.emptySet());
        });
    }

    public static EnumCreatureAttribute getAttribute(EntityLivingBase entity) {
        ResourceLocation loc = EntityList.getKey(entity);
        for (Map.Entry<EnumCreatureAttribute, IEntityMatcher> entry : attributes.entrySet())
            if (entry.getValue().matches(entity, loc))
                return entry.getKey();
        return EnumCreatureAttribute.UNDEFINED;
    }

    public interface IEntityMatcher { boolean matches(Entity entity, ResourceLocation loc);}

    public static class ClassMatcher implements IEntityMatcher {
        private Class<? extends Entity> entityClass;

        @SuppressWarnings("unchecked")
        public ClassMatcher(String className) {
            try {
                this.entityClass = (Class<? extends Entity>) Class.forName(className);
            } catch (ClassNotFoundException e) {
                EnchantmentControl.LOGGER.warn("Could not find entity class {} for custom creature attribute", className);
                this.entityClass = null;
            }
        }

        @Override
        public boolean matches(Entity entity, ResourceLocation loc) {
            return entityClass != null && entityClass.isInstance(entity);
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
