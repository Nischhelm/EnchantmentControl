package enchantmentcontrol.handler;

import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.util.ReEnchantUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ReEnchantTooltipHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onItemTooltip(ItemTooltipEvent event){
        if(!event.getFlags().isAdvanced()) return;
        if(event.getEntityPlayer() == null) return;
        if(!(event.getEntityPlayer().openContainer instanceof ContainerEnchantment)) return;

        int enchCount = ReEnchantUtil.getEnchantCount(event.getItemStack());
        event.getToolTip().add(AnvilUseTooltipHandler.getLowestEnchantLineIndex(event), TextFormatting.DARK_GRAY + ((enchCount == ConfigHandler.etable.reEnchantMaxTimes) ? I18n.format("tooltip.enchcount.full") :  I18n.format("tooltip.enchcount", enchCount, ConfigHandler.etable.reEnchantMaxTimes)) + TextFormatting.RESET);
    }
}
