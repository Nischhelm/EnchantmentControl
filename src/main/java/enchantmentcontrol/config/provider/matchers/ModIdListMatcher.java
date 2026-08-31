package enchantmentcontrol.config.provider.matchers;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import java.util.Set;

public class ModIdListMatcher extends ListMatcher {
    public ModIdListMatcher(Set<String> ids) {
        super(ids);
    }

    @Override
    public boolean matches(Entity entity, ResourceLocation loc) {
        return ids.contains(loc.getNamespace());
    }
}
