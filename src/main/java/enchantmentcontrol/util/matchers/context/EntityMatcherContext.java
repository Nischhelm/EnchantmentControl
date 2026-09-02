package enchantmentcontrol.util.matchers.context;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class EntityMatcherContext implements IContext {
    private final Entity entity;
    private final ResourceLocation location;

    public EntityMatcherContext(Entity entity, ResourceLocation location) {
        this.entity = entity;
        this.location = location;
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public ResourceLocation getLocation() {
        return location;
    }

    @Override
    public Object getObject() {
        return entity;
    }
}
