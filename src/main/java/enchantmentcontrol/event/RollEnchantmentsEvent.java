package enchantmentcontrol.event;

import enchantmentcontrol.util.EnchantmentContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fired before and after enchantments are generated in {@link EnchantmentHelper#buildEnchantmentList}.
 */
public class RollEnchantmentsEvent extends Event {

    @Nullable private final EnchantmentContext context;
    private final ItemStack stack;
    protected int level;
    protected boolean allowTreasure;

    public RollEnchantmentsEvent(@Nullable EnchantmentContext context, ItemStack stack, int level, boolean allowTreasure) {
        this.context = context;
        this.stack = stack;
        this.level = level;
        this.allowTreasure = allowTreasure;
    }

    @Nullable
    public EnchantmentContext getContext() {
        return context;
    }

    public ItemStack getStack() {
        return stack;
    }

    // This is the level that was used to create the enchantment list.
    // Not the same level as the lvl 30 of an enchanting table, but a modified number stemming from it
    // Minecraft uses this number to compare against minEnchantability and maxEnchantability of each applicable enchant (inclusive)
    public int getLevel() {
        return level;
    }

    public boolean allowsTreasure() {
        return allowTreasure;
    }


    /**
     * Fired before calling {@link EnchantmentHelper#buildEnchantmentList} to modify how to create the list
     * If canceled, the item will not be enchanted.
     */
    @Cancelable
    public static class Pre extends RollEnchantmentsEvent {

        private final Map<Enchantment, EnchantmentOverride> overrides;

        public Pre(@Nullable EnchantmentContext context, ItemStack stack, int level, boolean allowTreasure) {
            super(context, stack, level, allowTreasure);
            this.overrides = new HashMap<>();
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public void setAllowsTreasure(boolean allowTreasure) {
            this.allowTreasure = allowTreasure;
        }

        public Map<Enchantment, EnchantmentOverride> getOverrides() {
            return this.overrides;
        }

        public void setOverride(Enchantment enchantment, EnchantmentOverride override) {
            this.overrides.put(enchantment, override);
        }

        public void removeOverride(Enchantment enchantment) {
            this.overrides.remove(enchantment);
        }

        @Nullable
        public EnchantmentOverride getOverride(Enchantment enchantment) {
            return this.overrides.get(enchantment);
        }
    }



    /**
     * Fired after {@link EnchantmentHelper#buildEnchantmentList}, to modify the applicable enchantments and how to pick from that list.
     * If canceled, will clear the list and not apply any enchantments.
     */
    @Cancelable
    public static class Post extends RollEnchantmentsEvent {
        private final List<EnchantmentData> enchantments;

        @Nullable private Integer minEnchCount;
        @Nullable private Integer maxEnchCount;

        public Post(@Nullable EnchantmentContext context, ItemStack stack, int level, boolean allowTreasure, List<EnchantmentData> enchantments) {
            super(context, stack, level, allowTreasure);
            this.enchantments = enchantments;
        }

        public List<EnchantmentData> getEnchantments() {
            return enchantments;
        }

        // null if not set
        @Nullable
        public Integer getMinEnchCount() {
            return minEnchCount;
        }

        @Nullable
        public Integer getMaxEnchCount() {
            return maxEnchCount;
        }

        // Minimum number of enchantments to pick from the rolled list.
        // If the list is smaller than this count, all (mutually compatible) entries will be chosen.
        public void setMinEnchCount(@Nullable Integer minEnchCount) {
            this.minEnchCount = minEnchCount;
        }

        // Maximum number of enchantments to pick from the rolled list.
        public void setMaxEnchCount(@Nullable Integer maxEnchCount) {
            this.maxEnchCount = maxEnchCount;
        }
    }
}
