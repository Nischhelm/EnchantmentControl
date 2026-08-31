package enchantmentcontrol.config.provider.matchers;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import java.util.Set;

public class ListMatcher implements IEntityMatcher {
    protected final Set<String> ids;

    public ListMatcher(String name, Set<String> ids) {
        this.ids = ids;
    }

    @Override
    public boolean matches(Entity entity, ResourceLocation loc) {
        return ids.contains(loc.toString());
    }
}
