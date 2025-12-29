package enchantmentcontrol.util;

import com.google.gson.annotations.SerializedName;
import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.util.vanillasystem.ISystemOverride;
import enchantmentcontrol.util.vanillasystem.VanillaSystem;
import enchantmentcontrol.util.vanillasystem.VanillaSystemOverride;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

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

    public static EnchantmentInfo register(EnchantmentInfo info){
        byEnchId.put(info.enchId, info);
        return info;
    }

    public static void registerAll(List<EnchantmentInfo> infos){
        infos.forEach(EnchantmentInfo::register);
    }

    // -------- VANILLA SYSTEM OVERRIDES --------

    @Nullable
    public ISystemOverride getVanillaSystemOverride(VanillaSystem system) {
        return vanillaSystemStrengths.getOrDefault(system, null);
    }

    // -------- PROPERTIES --------

    @SerializedName("id")
    private final String enchId;
    public final String modId;

    public boolean overwritesIsTreasure = false;
    @SerializedName("isTreasure")
    public boolean isTreasure;

    public boolean overwritesIsCurse = false;
    @SerializedName("isCurse")
    public boolean isCurse;

    public boolean overwritesIsAllowedOnBooks = false;
    @SerializedName("isAllowedOnBooks")
    public boolean isAllowedOnBooks;

    @SerializedName("rarity")
    public Enchantment.Rarity rarity = null;

    @SerializedName("displayColor")
    public TextFormatting displayColor = null;

    public boolean overwritesMinLvl = false;
    @SerializedName("minLvl")
    public int minLvl;

    public boolean overwritesMaxLvl = false;
    @SerializedName("maxLvl")
    public int maxLvl;

    @SerializedName("enchantability")
    public EnchantabilityCalc ench;

    public static class EnchantabilityCalc {
        @SerializedName("minEnch")
        public int minEnchLvl;
        @SerializedName("lvlSpan")
        public int enchLvlSpan;
        @SerializedName("range")
        public int enchLvlRange;
        @SerializedName("minEnch")
        public MaxEnchantabilityMode enchMode;

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

    @SerializedName("type")
    public EnumEnchantmentType type;
    @SerializedName("slots")
    public List<EntityEquipmentSlot> slots;

    public boolean overwritesDoublePrice = false;
    @SerializedName("doublePrice")
    public boolean doublePrice;

    public BiFunction<Integer, EnumCreatureAttribute, Float> sharpnessBehavior;
    public BiFunction<Integer, DamageSource, Integer> protectionBehavior;
    public TriConsumer<EntityLivingBase, Entity, Integer> arthropodBehavior;
    public TriConsumer<EntityLivingBase, Entity, Integer> thornsBehavior;

    //-------- CONSTRUCTOR --------

    public EnchantmentInfo(@Nonnull String id) {
        this.enchId = id;
        this.modId = id.substring(0, id.indexOf(':')); //unsafe if ppl change the id to smth else than modid:enchid
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
        setEnchantabilities(new EnchantabilityCalc(minEnch, span, range, mode == null ? MaxEnchantabilityMode.NORMAL : mode));
    }

    public void setEnchantabilities(EnchantabilityCalc calc) {
        this.ench = calc;
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
        //unlike other entries calling this will not automatically apply the rarity change, that happens in EnchantmentInfoConfigHandler.applyManualInfoOverrides
        this.rarity = rarity;
    }

    public void setType(String type) {
        try {
            this.type = EnumEnchantmentType.valueOf(type);
        } catch (Exception e) {
            EnchantmentControl.LOGGER.error("Invalid enchantment type {} when reading json for : {}", type, this.enchId);
        }
    }

    public void setSlots(List<EntityEquipmentSlot> slots) {
        //unlike other entries calling this will not automatically apply the slots change, that happens in EnchantmentInfoConfigHandler.applyManualInfoOverrides
        this.slots = slots;
    }

    public void setDoublePrice(boolean doublePrice) {
        this.doublePrice = doublePrice;
        this.overwritesDoublePrice = true;
    }

    //Input (from other modifiers, otherwise 0), Ench Lvl, Output
    public Map<VanillaSystem, ISystemOverride> vanillaSystemStrengths = new HashMap<>();

    public void registerVanillaSystemOverride(VanillaSystem type, float perLevel){
        VanillaSystemOverride.getOverriders(type).add(this);
        this.vanillaSystemStrengths.put(type, new VanillaSystemOverride(perLevel));
    }
    public void registerVanillaSystemOverride(VanillaSystem type, ISystemOverride override){
        VanillaSystemOverride.getOverriders(type).add(this);
        this.vanillaSystemStrengths.put(type, override);
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
