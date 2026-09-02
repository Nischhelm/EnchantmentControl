package enchantmentcontrol.util.enchantmenttypes;

import enchantmentcontrol.config.matcherregistry.CustomItemTypeCreator;
import enchantmentcontrol.config.matcherregistry.DefaultCanApplyTypes;
import enchantmentcontrol.config.provider.ItemTypeConfigProvider;
import enchantmentcontrol.util.matcher.context.ItemTypeContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.*;

public class EnumEnchantmentTypeMatcher extends CanApplyMatcher {
    private static final Map<EnumEnchantmentType, EnumEnchantmentTypeMatcher> enchantToTypeMatchers = new HashMap<>();
    public static List<CanApplyMatcher> byEnum(EnumEnchantmentType type){
        if(type.ordinal() > 11){ //not vanilla enum
            switch (type.name()) {
                //some SME 0.x types are just lists of types or renames of existing vanilla Enums
                case "Combat Shield": return single(DefaultCanApplyTypes.getMatcher(DefaultCanApplyTypes.Types.SHIELD));
                case "Tool Pickaxe": return single(DefaultCanApplyTypes.getMatcher(DefaultCanApplyTypes.Types.PICKAXE));
                case "Tool Hoe": return single(DefaultCanApplyTypes.getMatcher(DefaultCanApplyTypes.Types.HOE));
                case "Damageable": return byEnum(EnumEnchantmentType.BREAKABLE);
                case "Combat Tool": return byEnum(EnumEnchantmentType.DIGGER);
                case "Combat Sword": return single(enchantToTypeMatchers.get(EnumEnchantmentType.WEAPON));
                case "Combat Axe": return single(DefaultCanApplyTypes.getMatcher(DefaultCanApplyTypes.Types.AXE));
                case "None": return single(DefaultCanApplyTypes.getMatcher(DefaultCanApplyTypes.Types.NONE));
                case "All": return single(DefaultCanApplyTypes.getMatcher(DefaultCanApplyTypes.Types.ANY));
                case "All Tools": return Arrays.asList(enchantToTypeMatchers.get(EnumEnchantmentType.DIGGER), enchantToTypeMatchers.get(EnumEnchantmentType.WEAPON));
                case "Combat": return Arrays.asList(enchantToTypeMatchers.get(EnumEnchantmentType.WEAPON), DefaultCanApplyTypes.getMatcher(DefaultCanApplyTypes.Types.AXE));
                case "Combat Weapon": return Arrays.asList(enchantToTypeMatchers.get(EnumEnchantmentType.BOW), enchantToTypeMatchers.get(EnumEnchantmentType.WEAPON), DefaultCanApplyTypes.getMatcher(DefaultCanApplyTypes.Types.AXE));
                case "Golden Apple": return Collections.singletonList(CustomItemTypeCreator.ITEMID.createMatcher("GOLD_APPLE", Collections.singleton("minecraft:golden_apple")));
            }
        }
        return single(enchantToTypeMatchers.getOrDefault(type, new EnumEnchantmentTypeMatcher(type)));
    }

    private static List<CanApplyMatcher> single(CanApplyMatcher matcher){
        return Collections.singletonList(matcher);
    }

    private final EnumEnchantmentType type;
    private final String name;

    public EnumEnchantmentTypeMatcher(String name, EnumEnchantmentType type){
        super(name, null);
        this.name = name;
        this.type = type;
        enchantToTypeMatchers.put(type, this);
    }

    public EnumEnchantmentTypeMatcher(EnumEnchantmentType type){
        this(type.toString(), type);
    }

    @Override
    public boolean matches(ItemTypeContext context) {
        Enchantment ench = context.getEnchantment();
        Item item = context.getItem();

        // This tries to catch all items that pretend to be normal MC items without inheriting from them
        // which then try to get the correct enchantments by overriding item.canApplyAtEnchantingTable(enchantment) using
        // enchantment.type == myPretended_vanillaEnumEnchantment_type

        // The main issue why we cant use the normal system is that vanilla only allows one type per enchant
        if(ItemTypeConfigProvider.shouldYieldToModdedBehavior(item)) {
            EnumEnchantmentType tmpType = ench.type;
            ench.type = this.type;
            boolean doesMatch = item.canApplyAtEnchantingTable(context.getStack(), ench);
            ench.type = tmpType;

            return doesMatch;
        }
        else return type.canEnchantItem(item);
    }

    @Override
    public ItemStack getFakeStack(){
        Item item = null;
        switch (this.type) {
            case ARMOR_HEAD: item = Items.IRON_HELMET; break;
            case ARMOR_CHEST: item = Items.IRON_CHESTPLATE; break;
            case ARMOR_LEGS: item = Items.IRON_LEGGINGS; break;
            case ARMOR_FEET: item = Items.IRON_BOOTS; break;
            case FISHING_ROD: item = Items.FISHING_ROD; break;
            case WEAPON: item = Items.IRON_SWORD; break;
            case DIGGER: item = Items.IRON_PICKAXE; break;
            case BOW: item = Items.BOW; break;
        }
        if(item != null) return new ItemStack(item);
        return null;
    }

    public EnumEnchantmentType getType() {
        return this.type;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
