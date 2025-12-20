package enchantmentcontrol.config.provider;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CreatureAttributeProvider {
    private static final Map<EnumCreatureAttribute, IEntityMatcher> attributes = new HashMap<>();

    public static void registerAttributes(Function<String, EnumCreatureAttribute> constructor) {

    }

    public interface IEntityMatcher {
        boolean matches(Entity entity);
    }
}
