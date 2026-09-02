package enchantmentcontrol.util.matchers.context;

import net.minecraft.util.ResourceLocation;

public interface IContext {
    ResourceLocation getLocation();
    Object getObject();
}
