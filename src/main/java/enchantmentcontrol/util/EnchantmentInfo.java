package enchantmentcontrol.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.util.TriConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class EnchantmentInfo {
    // -------- STATIC LOOKUP --------

    private static final Map<String, EnchantmentInfo> byEnchId = new HashMap<>();
    private static final Map<Enchantment, EnchantmentInfo> byEnchObj = new HashMap<>();
    private static final Map<EnchantmentInfo, Enchantment> toEnchObj = new HashMap<>();

    public static @Nullable EnchantmentInfo get(String enchid){
        return byEnchId.get(enchid);
    }

    public static @Nullable EnchantmentInfo get(Enchantment ench) {
        EnchantmentInfo info = byEnchObj.get(ench);
        if(info == null){
            info = ench.getRegistryName() == null ? null : get(ench.getRegistryName().toString());
            if(info != null){
                byEnchObj.put(ench, info);
                toEnchObj.put(info, ench);
            }
        }
        return info;
    }

    public static @Nullable Enchantment getEnchantmentObject(EnchantmentInfo info){
        return toEnchObj.get(info);
    }
    public static String getEnchantmentId(EnchantmentInfo info){
        return info.enchId;
    }

    public static Collection<EnchantmentInfo> getAll(){
        return byEnchId.values();
    }

    // -------- VANILLA SYSTEM OVERRIDES --------

    private static final Map<String, List<EnchantmentInfo>> vanillaSystemOverriders = new HashMap<String, List<EnchantmentInfo>>(){{
        put("sweeping", new ArrayList<>());
        put("knockback", new ArrayList<>());
        put("fireAspect", new ArrayList<>());
        put("respiration", new ArrayList<>());
        put("depthStrider", new ArrayList<>());
        put("efficiency", new ArrayList<>());
        put("luckOfTheSea", new ArrayList<>());
        put("lure", new ArrayList<>());
        put("looting", new ArrayList<>());
        put("aquaAffinity", new ArrayList<>());
        put("frostWalker", new ArrayList<>());
        put("bindingCurse", new ArrayList<>());
        put("vanishingCurse", new ArrayList<>());
    }};

    public static List<EnchantmentInfo> getOverriders(String system){
        return vanillaSystemOverriders.get(system);
    }

    // -------- PROPERTIES --------

    private final String enchId;

    public boolean overwritesIsTreasure;
    public boolean isTreasure;

    public boolean overwritesIsCurse;
    public boolean isCurse;

    public boolean overwritesIsAllowedOnBooks;
    public boolean isAllowedOnBooks;

    public Enchantment.Rarity rarity = null;

    public TextFormatting displayColor = null;

    public boolean overwritesMinLvl;
    public int minLvl;
    public boolean overwritesMaxLvl;
    public int maxLvl;

    public EnchantabilityCalc ench;
    public static class EnchantabilityCalc {
        private int minEnchLvl;
        private int enchLvlSpan;
        private int enchLvlRange;
        private MaxEnchantabilityMode enchMode;

        public EnchantabilityCalc(int minEnch, int lvlSpan, int range, MaxEnchantabilityMode mode){
            this.minEnchLvl = minEnch;
            this.enchLvlSpan = lvlSpan;
            this.enchLvlRange = range;
            this.enchMode = mode;
        }

        public int getMinEnch(int lvl) {
            return minEnchLvl + enchLvlSpan * (lvl-1);
        }
        public int getMaxEnch(int lvl) {
            return enchMode.getMaxEnch(lvl, getMinEnch(lvl), enchLvlRange);
        }
    }

    //TODO: price change on villagers?
    //TODO: can be on villagers?

    public Set<Enchantment> incompats;
    public Set<String> typesAnvil;
    public Set<String> typesEnchTable;
    public List<EntityEquipmentSlot> slots;

    public BiFunction<Integer, EnumCreatureAttribute, Float> sharpnessBehavior;
    public TriConsumer<EntityLivingBase, Entity, Integer> arthropodBehavior;
    public BiFunction<Integer, DamageSource, Integer> protectionBehavior;
    public TriConsumer<EntityLivingBase, Entity, Integer> thornsBehavior;

                    //Input, Ench Lvl, Output
    public BiFunction<Float, Integer, Float> sweepingStrength;
    public BiFunction<Float, Integer, Float> knockbackStrength;
    public BiFunction<Float, Integer, Float> fireAspectStrength;
    public BiFunction<Float, Integer, Float> respirationStrength;
    public BiFunction<Float, Integer, Float> depthStriderStrength;
    public BiFunction<Float, Integer, Float> efficiencyStrength;
    public BiFunction<Float, Integer, Float> luckOfTheSeaStrength;
    public BiFunction<Float, Integer, Float> lureStrength;
    public BiFunction<Float, Integer, Float> lootingStrength;
    public Function<Integer, Boolean> hasAquaAffinity;
    public Function<Integer, Boolean> hasFrostWalker;
    public Function<Integer, Boolean> hasBindingCurse;
    public Function<Integer, Boolean> hasVanishingCurse;

    //-------- CONSTRUCTOR --------

    public EnchantmentInfo(@Nonnull String id) {
        this.enchId = id;

        byEnchId.put(id, this);
    }

    //-------- SETTERS --------

    public void setMinLvl(int minLvl) {
        this.overwritesMinLvl = true;
        this.minLvl = minLvl;
    }

    public void setMaxLvl(int maxLvl) {
        this.overwritesMaxLvl = true;
        this.maxLvl = maxLvl;
    }

    public void setEnchantabilities(int minEnch, int span, int range, @Nullable MaxEnchantabilityMode mode) {
        if (mode == null) mode = MaxEnchantabilityMode.NORMAL;

        this.ench = new EnchantabilityCalc(minEnch, span, range, mode);
    }

    public void setTextDisplayColor(TextFormatting displayColor) {
        this.displayColor = displayColor;
    }

    public void setTreasure(boolean isTreasure) {
        this.isTreasure = isTreasure;
        this.overwritesIsTreasure = true;
    }

    public void setCurse(boolean isCurse) {
        this.isCurse = isCurse;
        this.overwritesIsCurse = true;
    }

    public void setAllowedOnBooks(boolean isAllowedOnBooks) {
        this.isAllowedOnBooks = isAllowedOnBooks;
        this.overwritesIsAllowedOnBooks = true;
    }

    public void setRarity(Enchantment.Rarity rarity) {
        this.rarity = rarity;
    }

    public void setEnchTableTypes(Set<String> types) {
        this.typesEnchTable = types;
    }

    public void setAnvilTypes(Set<String> types) {
        this.typesAnvil = types;
    }

    public void setSlots(List<EntityEquipmentSlot> slots) {
        this.slots = slots;
    }

    public void registerVanillaSystemOverride(String type, float perLevel){
        vanillaSystemOverriders.get(type).add(this);
        switch (type){
            case "sweeping": this.sweepingStrength = (valIn, lvl) -> valIn + lvl * perLevel; break;
            case "knockback": this.knockbackStrength = (valIn, lvl) -> valIn + lvl * perLevel; break;
            case "fireAspect": this.fireAspectStrength = (valIn, lvl) -> valIn + lvl * perLevel; break;
            case "respiration": this.respirationStrength = (valIn, lvl) -> valIn + lvl * perLevel; break;
            case "depthStrider": this.depthStriderStrength = (valIn, lvl) -> valIn + lvl * perLevel; break;
            case "efficiency": this.efficiencyStrength = (valIn, lvl) -> valIn + lvl * perLevel; break;
            case "luckOfTheSea": this.luckOfTheSeaStrength = (valIn, lvl) -> valIn + lvl * perLevel; break;
            case "lure": this.lureStrength = (valIn, lvl) -> valIn + lvl * perLevel; break;
            case "looting": this.lootingStrength = (valIn, lvl) -> valIn + lvl * perLevel; break;
            case "aquaAffinity": this.hasAquaAffinity = lvl -> lvl * perLevel >= 1; break;
            case "frostWalker": this.hasFrostWalker = lvl -> lvl * perLevel >= 1; break;
            case "bindingCurse": this.hasBindingCurse = lvl -> lvl * perLevel >= 1; break;
            case "vanishingCurse": this.hasVanishingCurse = lvl -> lvl * perLevel >= 1; break;
        }
    }

    // -------- GETTERS --------

    @SideOnly(Side.CLIENT) @SuppressWarnings("deprecation")
    public String getTranslatedName(Enchantment ench, int lvl){
        String s = I18n.translateToLocal(ench.getName());

        if (this.displayColor != null){
            s = this.displayColor + s;
        } else if (this.isCurse) {
            s = TextFormatting.RED + s;
        }

        return lvl == 1 && this.maxLvl == 1 ? s : s + " " + I18n.translateToLocal("enchantment.level." + lvl);
    }
}
