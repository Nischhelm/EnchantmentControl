package enchantmentcontrol.config.folders;

import meldexun.betterconfig.api.Order;
import net.minecraftforge.common.config.Config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class IncompatibleConfig {
    @Config.Comment("Global Toggle to disable the entire Incompatible groups override")
    @Config.Name("Incompatible Groups Enabled")
    @Order(0)
    public boolean incompatibleEnabled = true;

    @Config.Comment({
            "Each line is a group of mutually exclusive enchantments",
            "  like Smite, Sharpness and BoA",
            "Can be auto filled using \"First Setup.Print Default Incompatibilities\"",
            "Warning: this mod takes full control of enchantments incompatibilities with each other",
            "  so run the first setup every time you add mods that have enchants, then compare with what you set up to stay up to date"
    })
    @Config.Name("Incompatible Groups")
    @Order(1)
    public Map<String, ArrayList<String>> incompatibleGroups = new LinkedHashMap<>();
}
