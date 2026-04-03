package enchantmentcontrol.config.folders;

import net.minecraftforge.common.config.Config;

public class EnchTableConfig {
    @Config.Comment({
            "Max level the vanilla enchanting table can roll. ",
            "Only works in steps of 2.",
            "Set to -1 to disable (requires restart)"
    })
    @Config.Name("Enchantment Table Max Lvl")
    @Config.RangeInt(min = -1)
    public int maxLvl = 30;
}
