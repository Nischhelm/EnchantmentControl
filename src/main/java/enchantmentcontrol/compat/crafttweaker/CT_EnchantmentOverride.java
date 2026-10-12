package enchantmentcontrol.compat.crafttweaker;

import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import enchantmentcontrol.Tags;
import enchantmentcontrol.event.EnchantmentOverride;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

import javax.annotation.Nullable;

@ZenRegister
@ZenDoc("Represents per-enchantment overrides used during EnchantmentHelper.buildEnchantmentList, to use in RollEnchantmentsEvent.Pre")
@ZenClass("mods." + Tags.MODID + ".EnchantmentOverride")
public class CT_EnchantmentOverride {

    EnchantmentOverride internal;

    public CT_EnchantmentOverride(){
        this.internal = new EnchantmentOverride();
    }

    public CT_EnchantmentOverride(EnchantmentOverride override) {
        this.internal = override;
    }

    @ZenGetter("minLvl")
    @Nullable
    public Integer getMinLvl() {
        return internal.getMinLvl();
    }

    @ZenDoc("The minimum level to use for this enchantment in the current enchanting process")
    @ZenSetter("minLvl")
    public void setMinLvl(@Nullable Integer minLvl) {
        this.internal.setMinLvl(minLvl);
    }

    @ZenGetter("maxLvl")
    @Nullable
    public Integer getMaxLvl() {
        return internal.getMaxLvl();
    }

    @ZenDoc("The maximum level to use for this enchantment in the current enchanting process")
    @ZenSetter("maxLvl")
    public void setMaxLvl(@Nullable Integer maxLvl) {
        this.internal.setMaxLvl(maxLvl);
    }

    @ZenGetter("skipCanApply")
    @Nullable
    public Boolean getSkipCanApply() {
        return internal.getSkipCanApply();
    }

    @ZenDoc("Whether to skip the item x enchantment applicability check for this enchantment in the current enchanting process")
    @ZenSetter("skipCanApply")
    public void setSkipCanApply(@Nullable Boolean skipCanApply) {
        this.internal.setSkipCanApply(skipCanApply);
    }

    @ZenMethod
    public CT_EnchantmentOverride minLvl(@Nullable Integer minLvl) {
        this.internal.setMinLvl(minLvl);
        return this;
    }

    @ZenMethod
    public CT_EnchantmentOverride maxLvl(@Nullable Integer maxLvl) {
        this.internal.setMaxLvl(maxLvl);
        return this;
    }

    @ZenMethod
    public CT_EnchantmentOverride skipCanApply(@Nullable Boolean skipCanApply) {
        this.internal.setSkipCanApply(skipCanApply);
        return this;
    }

    public EnchantmentOverride getInternal() {
        return this.internal;
    }
}
