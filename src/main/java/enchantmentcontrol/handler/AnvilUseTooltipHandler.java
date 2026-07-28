package enchantmentcontrol.handler;

import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.util.AnvilCostUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AnvilUseTooltipHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onItemTooltip(ItemTooltipEvent event){
        // show on shift or on advanced
        if(!(ConfigHandler.anvil.tooltipOnShiftKey ? GuiScreen.isShiftKeyDown() : event.getFlags().isAdvanced())) return;

        if(event.getEntityPlayer() == null) return;

        if(!ConfigHandler.anvil.tooltipShowEverywhere && !(event.getEntityPlayer().openContainer instanceof ContainerRepair)) return;

        int repairCost = event.getItemStack().getRepairCost();
        if(repairCost <= 0) return;
        int uses = AnvilCostUtil.getAnvilCount(event.getItemStack()); // MathHelper.log2(repairCost + 1); //logarithms baby

        event.getToolTip().add(getLowestEnchantLineIndex(event), TextFormatting.DARK_GRAY + I18n.format((uses == 1) ? "tooltip.anviluses.singular" : "tooltip.anviluses", uses, repairCost) + TextFormatting.RESET);
    }

    public static int getLowestEnchantLineIndex(ItemTooltipEvent event){
        int tooltipIndex = 1;
        if(event.getItemStack().isItemEnchanted()) {
            NBTTagList nbttaglist = event.getItemStack().getEnchantmentTagList();
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(nbttaglist.tagCount() - 1);
            int enchId = nbttagcompound.getShort("id");
            int enchLvl = nbttagcompound.getShort("lvl");
            Enchantment enchantment = Enchantment.getEnchantmentByID(enchId);

            if (enchantment != null) {
                String enchTooltip = enchantment.getTranslatedName(enchLvl);
                tooltipIndex = event.getToolTip().indexOf(enchTooltip) + 1;
                if(tooltipIndex == 0) tooltipIndex = 1;
            }
        }
        return tooltipIndex;
    }
}
