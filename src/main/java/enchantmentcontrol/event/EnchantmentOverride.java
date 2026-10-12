package enchantmentcontrol.event;

import javax.annotation.Nullable;

// Represents per-enchantment overrides used during EnchantmentHelper.buildEnchantmentList
public class EnchantmentOverride {
    @Nullable private Integer minLvl;
    @Nullable private Integer maxLvl;
    @Nullable private Boolean skipCanApply;
    //TODO: list of actually mutually compatible

    // null if no overridden minLvl
    @Nullable
    public Integer getMinLvl() {
        return minLvl;
    }

    @Nullable
    public Integer getMaxLvl() {
        return maxLvl;
    }

    @Nullable
    public Boolean getSkipCanApply() {
        return skipCanApply;
    }

    // Minimum enchantment level to use
    public EnchantmentOverride setMinLvl(@Nullable Integer minLvl) {
        this.minLvl = minLvl;
        return this;
    }

    // Maximum enchantment level to use
    public EnchantmentOverride setMaxLvl(@Nullable Integer maxLvl) {
        this.maxLvl = maxLvl;
        return this;
    }

    // Allows to skip the canApplyAtEnchantingTable. This allows usually unapplicable enchantments to be used.
    public EnchantmentOverride setSkipCanApply(@Nullable Boolean skipCanApply) {
        this.skipCanApply = skipCanApply;
        return this;
    }
}
