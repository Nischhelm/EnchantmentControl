package enchantmentcontrol.config.enchantmentinfojsons;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.mixin.vanilla.EnchantmentAccessor;
import enchantmentcontrol.util.ConfigRef;
import enchantmentcontrol.util.EnchantmentInfo;
import enchantmentcontrol.util.MaxEnchantabilityMode;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builds EnchantmentInfo by inspecting Enchantment objects and writes those to file as an approximate start point.
 */
public class EnchantmentInfoInferrerWriter {
    public static final String MAIN_DIR = "config/enchantmentcontrol/inferred";

    public static void printInferred(){
        EnchantmentInfoWriter.clearDirectoryContents(new File(MAIN_DIR));
        EnchantmentInfoWriter.writeAllCurrentEnchantmentInfos(inferInfoForAllRegisteredEnchantments(), MAIN_DIR);

        EnchantmentControl.CONFIG.get("general.first setup", ConfigRef.DO_INFER_CONFIG_NAME, ConfigHandler.dev.printInferred).set(false);
        ConfigHandler.dev.printInferred = false;
        EnchantmentControl.configNeedsSaving = true;
    }

    public static List<EnchantmentInfo> inferInfoForAllRegisteredEnchantments() {
        List<EnchantmentInfo> out = new ArrayList<>();
        for (Enchantment ench : Enchantment.REGISTRY) {
            EnchantmentInfo info = inferInfoForEnchantment(ench);
            if (info != null) out.add(info);
        }
        return out;
    }

    public static @Nullable EnchantmentInfo inferInfoForEnchantment(Enchantment ench) {
        if (ench == null || ench.getRegistryName() == null) return null;

        EnchantmentInfo info = new EnchantmentInfo(ench.getRegistryName().toString());

        info.rarity = ench.getRarity();
        if(ConfigHandler.dev.printInferredExpanded) info.setMinLvl(ench.getMinLevel());
        info.setMaxLvl(ench.getMaxLevel());
        info.setCurse(ench.isCurse());
        info.setTreasure(ench.isTreasureEnchantment());
        if(ConfigHandler.dev.printInferredExpanded) {
            info.setDoublePrice(ench.isTreasureEnchantment()); //just copying behavior
            info.setAllowedOnBooks(ench.isAllowedOnBooks());
            info.setEnchantabilities(probeEnchantability(ench));
            info.slots = Arrays.asList(((EnchantmentAccessor) ench).getSlots());
        }

        TextFormatting fmt = probeDisplayColor(ench); // Display color (only for unusual, not for default none or RED if curse)
        if (!(info.isCurse && fmt == TextFormatting.RED)) info.setTextDisplayColor(fmt);

        return info;
    }

    private static @Nullable TextFormatting probeDisplayColor(Enchantment ench) {
        try {
            String name = ench.getTranslatedName(1);
            // Search the start of the name for a textformatting flag
            for (TextFormatting fmt : TextFormatting.values()) if (name.startsWith(fmt.toString())) return fmt;
        } catch (Throwable ignored) {
            //This might happen on server?
            EnchantmentControl.LOGGER.warn("Failed to probe display color for {}", ench.getRegistryName());
        }
        return null;
    }

    private static @Nullable EnchantmentInfo.EnchantabilityCalc probeEnchantability(Enchantment ench) {
        try {
            int[] minEnchProbed = {0,0,0};
            int[] maxEnchProbed = {0,0,0};
            for(int i = 0; i <= 2; i++) {
                minEnchProbed[i] = ench.getMinEnchantability(i);
                maxEnchProbed[i] = ench.getMaxEnchantability(i);
            }

            int minEnch = minEnchProbed[1];
            int lvlSpan = minEnchProbed[2] - minEnchProbed[1];
            if(minEnchProbed[1] - minEnchProbed[0] != lvlSpan) {
                EnchantmentControl.LOGGER.warn("Enchantability for {} has inconsistent lvlSpan behavior that can't be copied to EnchantmentInfo, won't overwrite", ench.getRegistryName());
                return null;
            }

            int range = 0;
            MaxEnchantabilityMode mode = null;

            //CONST if all 3 maxEnch the same
            if(maxEnchProbed[2] == maxEnchProbed[1] && maxEnchProbed[1] == maxEnchProbed[0]){
                if(ench.getMaxLevel() != ench.getMinLevel()) {
                    mode = MaxEnchantabilityMode.CONST;
                    range = maxEnchProbed[0];
                } else {
                    //enchants with just 1 lvl might not have a range defined,
                    // we'll denote them as normal,
                    // doesn't make a difference except if someone changes the max lvl,
                    // where its good for it to behave NORMAL
                    mode = MaxEnchantabilityMode.NORMAL;
                    range = maxEnchProbed[1]-minEnchProbed[1]; //best guess
                }
            }
            else if(maxEnchProbed[2] - maxEnchProbed[1] == maxEnchProbed[1] - maxEnchProbed[0]){ //NORMAL, SUPER and LINEAR all have the behavior that they grow linearly with lvl (slope is lvlSpan, 10 and range respectively)

                int d0 = maxEnchProbed[0] - minEnchProbed[0];
                int d1 = maxEnchProbed[1] - minEnchProbed[1];
                int d2 = maxEnchProbed[2] - minEnchProbed[2];
                int dd0 = maxEnchProbed[0] - 1;
                int dd1 = maxEnchProbed[1] - 11;
                int dd2 = maxEnchProbed[2] - 21;

                //NORMAL if distance to actual min is constant
                if(d0 == d1 && d1 == d2){
                    mode = MaxEnchantabilityMode.NORMAL;
                    range = d0;
                }
                //SUPER if distance to super min (1+10*lvl) is constant
                else if(dd0 == dd1 && dd1 == dd2){
                    mode = MaxEnchantabilityMode.SUPER;
                    range = dd0;
                }
            }

            if(mode == null){
                EnchantmentControl.LOGGER.warn("Enchantability for {} has inconsistent maxEnch behavior that can't be copied to EnchantmentInfo, won't overwrite", ench.getRegistryName());
                return null;
            }

            return new EnchantmentInfo.EnchantabilityCalc(minEnch, lvlSpan, range, mode);
        } catch (Throwable t) {
            return null;
        }
    }
}
