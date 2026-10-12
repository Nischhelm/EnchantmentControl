package enchantmentcontrol.compat.crafttweaker;

import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.container.IContainer;
import crafttweaker.api.enchantments.IEnchantment;
import crafttweaker.api.enchantments.IEnchantmentDefinition;
import crafttweaker.api.event.IEventCancelable;
import crafttweaker.api.event.IPlayerEvent;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.mc1120.enchantments.MCEnchantmentDefinition;
import enchantmentcontrol.Tags;
import enchantmentcontrol.event.EnchantmentOverride;
import enchantmentcontrol.event.RollEnchantmentsEvent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CT_RollEnchantmentsEvent implements IPlayerEvent, IEventCancelable {

    private final RollEnchantmentsEvent internal;

    CT_RollEnchantmentsEvent(RollEnchantmentsEvent internal) {
        this.internal = internal;
    }

    // --- Properties ---

    @ZenGetter("level")
    public int getLevel() {
        return internal.getLevel();
    }

    @ZenGetter("allowTreasure")
    public boolean getAllowTreasure() {
        return internal.allowsTreasure();
    }

    // --- Context Getters ---

    @ZenGetter("player")
    @Nullable
    public IPlayer getPlayer() {
        if (internal.getContext() == null) return null;
        return CraftTweakerMC.getIPlayer(internal.getContext().getPlayer());
    }

    @ZenGetter("itemStack")
    public IItemStack getItemStack() {
        return CraftTweakerMC.getIItemStack(internal.getStack());
    }

    @ZenGetter("fromEnchantingTable")
    public boolean getIsFromEnchantingTable() {
        return internal.getContext() != null && internal.getContext().isFromEnchantingTable();
    }

    @ZenDoc("Slot of the enchanting table (1-3). -1 if not from enchanting table.")
    @ZenGetter("enchantingTableSlot")
    public int getEnchantingTableSlot() {
        return internal.getContext() != null ? internal.getContext().getEnchantingTableSlot() : -1;
    }

    @ZenDoc("Position of the enchanting table where the enchanting happens, or null if not an enchanting table.")
    @ZenGetter("position")
    public IBlockPos getPosition() {
        return internal.getContext() != null ? CraftTweakerMC.getIBlockPos(internal.getContext().getPosition()) : null;
    }

    // --- Cancel ---

    @ZenDoc("Cancel the event to prevent any enchantments from being applied.")
    @ZenSetter("canceled")
    @Override
    public void setCanceled(boolean canceled) {
       this.internal.setCanceled(true);
    }

    @ZenDoc("Check if the event is cancelled.")
    @ZenGetter("canceled")
    public boolean isCanceled() {
        return internal.isCanceled();
    }
    
    // --- Pre Event ---
    
    @ZenRegister
    @ZenDoc("Wrapper for RollEnchantmentsEvent.Pre.")
    @ZenClass("mods." + Tags.MODID + ".RollEnchantmentsEvent_Pre")
    public static class Pre extends CT_RollEnchantmentsEvent {

        private final RollEnchantmentsEvent.Pre internal;

        Pre(RollEnchantmentsEvent.Pre internal) {
            super(internal);
            this.internal = internal;
        }

        // --- Properties ---

        @ZenDoc("This is the adjusted level, so not the max 30 on enchantment table but the 26-37 (or similar) it turns into. This is compared with min and max enchantability of the Enchantment (inclusive).")
        @ZenSetter("level")
        public void setLevel(int level) {
            this.internal.setLevel(level);
        }

        @ZenSetter("allowTreasure")
        public void setAllowTreasure(boolean allowTreasure) {
            this.internal.setAllowsTreasure(allowTreasure);
        }

        // --- Enchantment Override Methods ---
        
        @ZenDoc("Gets or creates & registers an override for the given enchantment")
        @ZenMethod
        public CT_EnchantmentOverride getOverride(IEnchantmentDefinition enchantment) {
            EnchantmentOverride override = this.internal.getOverride((Enchantment) enchantment.getInternal());
            if(override != null) return new CT_EnchantmentOverride(override);
            else {
                CT_EnchantmentOverride wrapper = new CT_EnchantmentOverride();
                this.internal.setOverride((Enchantment) enchantment.getInternal(), wrapper.getInternal());
                return wrapper;
            }
        }

        @ZenDoc("Remove an enchantment override")
        @ZenMethod
        public void removeOverride(IEnchantmentDefinition enchantment) {
            this.internal.removeOverride((Enchantment) enchantment.getInternal());
        }
    }

    // --- Post Event ---
    
    @ZenRegister
    @ZenDoc("Wrapper for RollEnchantmentsEvent.Post")
    @ZenClass("mods." + Tags.MODID + ".RollEnchantmentsEvent_Post")
    public static class Post extends CT_RollEnchantmentsEvent {
    
        private final RollEnchantmentsEvent.Post internal;
    
        Post(RollEnchantmentsEvent.Post internal) {
            super(internal);
            this.internal = internal;
        }
    
        // --- Enchantment List Methods ---
    
        @ZenDoc("Get all rolled applicable enchantments as a list.")
        @ZenGetter("enchantments")
        public List<IEnchantmentDefinition> getEnchantments() {
            List<IEnchantmentDefinition> result = new ArrayList<>();
            for (EnchantmentData data : this.internal.getEnchantments()) {
                result.add(new MCEnchantmentDefinition(data.enchantment));
            }
            return result;
        }
    
        @ZenDoc("Remove all instances of this enchantment from the list")
        @ZenMethod
        public void removeEnchantment(IEnchantmentDefinition enchantment) {
            Enchantment ench = (Enchantment) enchantment.getInternal();
            this.internal.getEnchantments().removeIf(data -> data.enchantment == ench);
        }
    
        @ZenDoc("Add a pickable enchantment to the list")
        @ZenMethod
        public void addEnchantment(IEnchantment enchantment) {
            Enchantment ench = (Enchantment) enchantment.getDefinition().getInternal();
            this.internal.getEnchantments().add(new EnchantmentData(ench, enchantment.getLevel()));
        }
    
        @ZenDoc("Clear all enchantments from the list")
        @ZenMethod
        public void clearEnchantments() {
            this.internal.getEnchantments().clear();
        }
    
        // --- Min/Max Count Properties ---
    
        @ZenGetter("minEnchCount")
        @Nullable
        public Integer getMinEnchCount() {
            return this.internal.getMinEnchCount();
        }
    
        @ZenSetter("minEnchCount")
        public void setMinEnchCount(@Nullable Integer minEnchCount) {
           this.internal.setMinEnchCount(minEnchCount);
        }
    
        @ZenGetter("maxEnchCount")
        @Nullable
        public Integer getMaxEnchCount() {
            return internal.getMaxEnchCount();
        }
    
        @ZenSetter("maxEnchCount")
        public void setMaxEnchCount(@Nullable Integer maxEnchCount) {
           this.internal.setMaxEnchCount(maxEnchCount);
        }
    }
}
