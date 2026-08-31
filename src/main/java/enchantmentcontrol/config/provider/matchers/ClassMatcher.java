package enchantmentcontrol.config.provider.matchers;

import enchantmentcontrol.EnchantmentControl;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class ClassMatcher implements IEntityMatcher {
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
