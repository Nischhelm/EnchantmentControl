package enchantmentcontrol.config.folders;

import net.minecraftforge.common.config.Config;

public class CompatConfig {

    public JeiCompatConfig jei = new JeiCompatConfig();

    public static class JeiCompatConfig {
        @Config.Comment("Global toggle for enchantment info JEI Plugin")
        @Config.Name("Enabled Enchantment Info Plugin")
        @Config.RequiresMcRestart
        public boolean jeiInfoEnabled = true;

        @Config.Comment("Adds information about rarity, treasure and curse status to enchanted books in JEI")
        @Config.Name("Add JEI Rarity, Trasure, Curse Info")
        @Config.RequiresMcRestart
        public boolean jeiRarityTreasureInfo = true;

        @Config.Comment("Adds information about incompatible enchantments to enchanted books in JEI")
        @Config.Name("Add JEI Incompat Info")
        @Config.RequiresMcRestart
        public boolean jeiIncompatInfo = true;

        @Config.Comment("Adds information about item types to enchanted books in JEI")
        @Config.Name("Add JEI Item Types Info")
        @Config.RequiresMcRestart
        public boolean jeiItemTypesInfo = true;

        @Config.Comment("Adds information about which equipment slots enchantments are searched at to enchanted books in JEI")
        @Config.Name("Add JEI Applicable Slot Id Info")
        @Config.RequiresMcRestart
        public boolean jeiSlotInfo = false;

        @Config.Comment("Adds information about enchantability levels to enchanted books in JEI")
        @Config.Name("Add JEI Enchantability Info")
        @Config.RequiresMcRestart
        public boolean jeiEnchantabilityInfo = true;

        @Config.Comment("Adds information about xp price (when on book/when on item) on anvil and potential emerald price range on Librarians to enchanted books in JEI")
        @Config.Name("Add JEI XP & Emerald Price Info")
        @Config.RequiresMcRestart
        public boolean jeiXPEmeraldPriceInfo = false;

        @Config.Comment("Adds information about enchantment id (modid:enchid) to enchanted books in JEI")
        @Config.Name("Add JEI Enchantment Id Info")
        @Config.RequiresMcRestart
        public boolean jeiEnchIdInfo = true;
    }
}
