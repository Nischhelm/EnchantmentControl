package enchantmentcontrol.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import enchantmentcontrol.util.vanillasystem.ISystemOverride;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;


@ZenRegister
@ZenClass("mods.enchantmentcontrol.VanillaSystemOverride")
public interface CT_SystemOverride extends ISystemOverride {
    @ZenMethod
    float handle(float startVal, int enchLvl);

    @Override
    default Float apply(Float valIn, Integer lvl) {
        return handle(valIn, lvl);
    }
}
