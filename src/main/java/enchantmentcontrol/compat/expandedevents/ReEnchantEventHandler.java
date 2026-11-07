package enchantmentcontrol.compat.expandedevents;

import com.expandedevents.api.event.GenerateEnchantmentListEvent;
import enchantmentcontrol.util.ReEnchantUtil;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

/**
 * Replaces ContainerEnchantmentMixin_ReEnchantRoll if Expanded Events is installed.
 * Runs at LOWEST so enchants added by other listeners get filtered as well.
 * Combined enchants stay in the list (with their combined level), Expanded Events caches it for enchantItem,
 * where ContainerEnchantmentMixin_ReEnchant applies them to the existing entries.
 */
public class ReEnchantEventHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGenerateEnchantmentList(GenerateEnchantmentListEvent event) {
        ItemStack stack = event.getStack();
        if(!stack.isItemEnchanted()) return;

        List<EnchantmentData> filtered = ReEnchantUtil.reenchantIfPossible(event.getEnchantments(), stack, true);
        List<EnchantmentData> enchants = event.getModifiableEnchantments();
        enchants.clear();
        enchants.addAll(filtered);
    }
}
