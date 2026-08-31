package enchantmentcontrol.util.matcher.context;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class EntityMatcherContext {
    private final Entity entity;
    private final ResourceLocation location;

    public EntityMatcherContext(Entity entity, ResourceLocation location) {
        this.entity = entity;
        this.location = location;
    }

    public Entity getEntity() {
        return entity;
    }

    public ResourceLocation getLocation() {
        return location;
    }
}
