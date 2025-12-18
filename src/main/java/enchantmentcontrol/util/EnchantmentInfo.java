package enchantmentcontrol.util;

import com.sun.jna.platform.unix.X11;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.util.TriConsumer;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class EnchantmentInfo {
    // -------- STATIC LOOKUP --------

    private static final Map<String, EnchantmentInfo> byEnchId = new HashMap<>();
    private static final Map<Enchantment, EnchantmentInfo> byEnchObj = new HashMap<>();

    public static @Nullable EnchantmentInfo get(String enchid){
        return byEnchId.get(enchid);
    }

    public static @Nullable EnchantmentInfo get(Enchantment ench) {
        return byEnchObj.computeIfAbsent(ench, enchant -> enchant.getRegistryName() == null ? null : get(enchant.getRegistryName().toString()));
    }

    public static Collection<EnchantmentInfo> getAll(){
        return byEnchId.values();
    }

    // -------- PROPERTIES --------

    private final String enchId;
    private final boolean isDisabled;

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

    private int minEnchLvl, enchLvlSpan, enchLvlRange;
    public MaxEnchantabilityMode enchMode = null;

    //TODO: price change on villagers?
    //TODO: can be on villagers?

    public Set<Enchantment> incompats;
    public Set<String> typesAnvil;
    public Set<String> typesEnchTable;

    public BiFunction<Integer, EnumCreatureAttribute, Float> sharpnessBehavior;
    public TriConsumer<EntityLivingBase, Entity, Integer> arthropodBehavior;
    public BiFunction<Integer, DamageSource, Integer> protectionBehavior;
    public TriConsumer<EntityLivingBase, Entity, Integer> thornsBehavior;

    private int sweepingStrength;
    private int knockbackStrength;
    private int fireAspectStrength;
    private int respirationStrength;
    private int depthStriderStrength;
    private int efficiencyStrength;
    private int luckOfTheSeaStrength;
    private int lureStrength;
    private int lootingStrength;

    private int hasAquaAffinity;
    private int ignoresMagmaDmg; //frost walker
    private int hasBindingCurse;
    private int hasVanishingCurse;

    //-------- CONSTRUCTOR --------

    public EnchantmentInfo(String id, boolean isDisabled) {
        this.enchId = id;
        this.isDisabled = isDisabled;

        byEnchId.put(id, this);
    }

    //-------- SETTERS --------

    public void setMinLvl(int minLvl) {
        this.minLvl = minLvl;
    }

    public void setMaxLvl(int maxLvl) {
        this.maxLvl = maxLvl;
    }

    public void setEnchantabilities(int minEnch, int range, int span, @Nullable MaxEnchantabilityMode mode) {
        if (mode != null) this.enchMode = mode;
        else this.enchMode = MaxEnchantabilityMode.NORMAL;

        this.minEnchLvl = minEnch;
        this.enchLvlSpan = span;
        this.enchLvlRange = range;
    }

    public void setTextDisplayColor(TextFormatting displayColor) {
        this.displayColor = displayColor;
    }

    public void setTreasure(boolean isTreasure) {
        this.isTreasure = isTreasure;
    }

    public void setCurse(boolean isCurse) {
        this.isCurse = isCurse;
    }

    public void setAllowedOnBooks(boolean isAllowedOnBooks) {
        this.isAllowedOnBooks = isAllowedOnBooks;
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

    // -------- GETTERS --------

    public int getMinEnchLvl(int lvl){
        return this.minEnchLvl + this.enchLvlSpan * (lvl - 1);
    }

    public int getMaxEnchLvl(int lvl){
        return this.enchMode.getMaxEnch(lvl, this.getMinEnchLvl(lvl), this.enchLvlRange);
    }

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
