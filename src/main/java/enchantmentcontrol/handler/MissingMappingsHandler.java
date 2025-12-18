package enchantmentcontrol.handler;

import enchantmentcontrol.config.ConfigHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class MissingMappingsHandler {
    @SubscribeEvent
    public static void onMissingEnchantmentMappings(RegistryEvent.MissingMappings<Enchantment> event){
        //Maps missing original ids to current replacement ids, not other way around
        event.getAllMappings()
                .stream()
                .filter(m -> ConfigHandler.idRemaps.containsKey(m.key.toString()))
                .forEach(m -> {
                    String newLoc = ConfigHandler.idRemaps.get(m.key.toString());
                    Enchantment mappedEnch = Enchantment.getEnchantmentByLocation(newLoc);
                    if (mappedEnch != null) //could be unregistered via config
                        m.remap(mappedEnch);
                });
    }
}
