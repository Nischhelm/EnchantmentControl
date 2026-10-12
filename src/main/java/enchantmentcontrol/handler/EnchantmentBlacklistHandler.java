package enchantmentcontrol.handler;

import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.config.provider.BlacklistConfigProvider;
import enchantmentcontrol.event.RollEnchantmentsEvent;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EnchantmentBlacklistHandler {

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onBuildEnchantmentListPost(RollEnchantmentsEvent.Post event) {
        boolean fromEnchantingTable = event.getContext() != null && event.getContext().isFromEnchantingTable();
        event.getEnchantments().removeIf(data -> !isEnchantmentAllowed(data.enchantment, fromEnchantingTable));
    }

    private static boolean isEnchantmentAllowed(Enchantment enchantment, boolean fromEnchantingTable) {
        if (fromEnchantingTable)
            return BlacklistConfigProvider.getEnchantTableEnchantsBlacklist().contains(enchantment) == ConfigHandler.blacklists.blacklistedEnchTableEnchantsIsWhitelist;
        else
            return BlacklistConfigProvider.getRandomLevelEnchantsBlacklist().contains(enchantment) == ConfigHandler.blacklists.blacklistedRandomLevelEnchantsIsWhitelist;
    }
}
