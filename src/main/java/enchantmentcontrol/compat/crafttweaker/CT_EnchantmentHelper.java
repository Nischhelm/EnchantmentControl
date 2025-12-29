package enchantmentcontrol.compat.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.enchantments.IEnchantment;
import crafttweaker.api.enchantments.IEnchantmentDefinition;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.util.IRandom;
import crafttweaker.mc1120.enchantments.MCEnchantment;
import crafttweaker.mc1120.util.MCRandom;
import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.mixin.modded.crafttweaker.MCRandomAccessor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.EnchantRandomly;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@ZenRegister
@ZenClass("mods."+ EnchantmentControl.MODID +".EnchantmentHelper")
@SuppressWarnings("unused")
public class CT_EnchantmentHelper {
    @ZenMethod
    @ZenDoc("Returns the level of the given enchantment on the given stack, or 0 if not present.")
    public static int getEnchantmentLevel(IEnchantmentDefinition enchID, IItemStack stack){
        return EnchantmentHelper.getEnchantmentLevel((Enchantment) enchID.getInternal(), (ItemStack) stack.getInternal());
    }

    @ZenMethod
    @ZenDoc("Returns a list of all enchantments on the given stack.")
    public static List<IEnchantment> getEnchantments(IItemStack stack){
        return EnchantmentHelper.getEnchantments((ItemStack) stack.getInternal())
                    .entrySet().stream()
                    .map(e -> new MCEnchantment(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
    }

    @ZenMethod
    @ZenDoc("Sets the enchantments on the given stack to the given list. Overwrites existing enchantments.")
    public static void setEnchantments(List<IEnchantment> enchList, IItemStack stack){
        Map<Enchantment, Integer> enchMap = enchList.stream()
                .collect(Collectors.toMap(e -> (Enchantment) e.getDefinition().getInternal(), IEnchantment::getLevel));

        EnchantmentHelper.setEnchantments(enchMap, (ItemStack) stack.getInternal());
    }

    @ZenMethod
    @ZenDoc("Adds the given enchantment to the given stack with the given level, with no safety brackes. Overwrites if already applied.")
    public static void addEnchantment(IItemStack stack, IEnchantment ench){
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments((ItemStack) stack.getInternal());
        map.put((Enchantment) ench.getDefinition().getInternal(), ench.getLevel());
        EnchantmentHelper.setEnchantments(map, (ItemStack) stack.getInternal());
    }

    @ZenMethod
    @ZenDoc("Removes the given enchantment from the given stack, if present.")
    public static void removeEnchantment(IItemStack stack, IEnchantmentDefinition ench){
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments((ItemStack) stack.getInternal());
        map.remove((Enchantment) ench.getInternal());
        EnchantmentHelper.setEnchantments(map, (ItemStack) stack.getInternal());
    }

    @ZenMethod
    @ZenDoc("Returns from all the allowed slots on the given entity the lvl of the given enchant that is the highest on any of the stacks in those slots.")
    public static int getMaxEnchantmentLevel(IEnchantmentDefinition ench, IEntityLivingBase entity){
        return EnchantmentHelper.getMaxEnchantmentLevel((Enchantment) ench.getInternal(), (EntityLivingBase) entity.getInternal());
    }

    @ZenMethod
    @ZenDoc("Returns a random enchanted item in the allowed slots on the given entity that has the given enchantment")
    @Nullable //EnchantmentHelper returns EMPTY but CT counts that as null
    public static IItemStack getRandomEnchantedItem(IEnchantmentDefinition ench, IEntityLivingBase entity){
        return CraftTweakerMC.getIItemStack(
                EnchantmentHelper.getEnchantedItem((Enchantment) ench.getInternal(), (EntityLivingBase) entity.getInternal())
        );
    }

    @ZenMethod
    @ZenDoc("Enchants a given stack with lvls (same lvls as the ones on enchanting table). \n" +
            "Use rand() if you dont have access to an IRandom.\n" +
            "stack, level, allowTreasure, rand")
    public static IItemStack enchantWithLevels(IItemStack stack, int level, boolean allowTreasure, IRandom rand){
        if(!(rand instanceof MCRandomAccessor)){
            CraftTweakerAPI.logInfo("IEnchantmentHelper: Given random not normal, cant enchantWithLevels");
            return stack;
        }
        return CraftTweakerMC.getIItemStack(
                EnchantmentHelper.addRandomEnchantment(
                        ((MCRandomAccessor) rand).getRandom(),
                        (ItemStack) stack.getInternal(),
                        level,
                        allowTreasure
                )
        );
    }

    @ZenMethod
    @ZenDoc("Enchants the stack randomly with one single enchantment with any lvl. This system is used by librarians and random books in some loot tables. Needs IWorld for loot context")
    public static IItemStack enchantRandomly(IItemStack stack, IRandom rand){
        if(!(rand instanceof MCRandomAccessor)){
            CraftTweakerAPI.logInfo("IEnchantmentHelper: Given random not normal, cant enchantRandomly");
            return stack;
        }
        try {
            ItemStack out = new EnchantRandomly(new LootCondition[0], null).apply((ItemStack) stack.getInternal(), ((MCRandomAccessor) rand).getRandom(), null);
            return CraftTweakerMC.getIItemStack(out);
        } catch (Exception e){
            CraftTweakerAPI.logError("Error while enchanting randomly: " + e.getMessage());
        }
        return stack;
    }

    private static final IRandom RAND = new MCRandom(new Random());

    @ZenMethod
    @ZenDoc("Returns a default Random instance for ease of use since seeding usually doesn't matter anyway")
    public static IRandom rand(){
        return RAND;
    }
}
