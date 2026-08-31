package enchantmentcontrol.config.provider.matchers;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public interface IEntityMatcher {
    boolean matches(Entity entity, ResourceLocation loc);
}
