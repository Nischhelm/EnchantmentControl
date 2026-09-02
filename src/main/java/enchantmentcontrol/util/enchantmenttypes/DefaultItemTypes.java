package enchantmentcontrol.util.enchantmenttypes;

import enchantmentcontrol.util.matchers.IMatcher;
import enchantmentcontrol.util.matchers.MatcherCreator;
import enchantmentcontrol.util.matchers.context.ItemTypeContext;
import enchantmentcontrol.util.matchers.matcher.BooleanMatcher;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Items;
import net.minecraft.item.*;

import java.util.*;

public class DefaultItemTypes {

    private static final Map<Type, ItemTypeMatcher> registeredDefaultMatchers = new EnumMap<>(Type.class);

    public static ItemTypeMatcher get(Type type) {
        return registeredDefaultMatchers.get(type);
    }

    public enum Type {
        // EnumEnchantmentType backed
        ARMOR, ARMOR_HEAD, ARMOR_CHEST, ARMOR_LEGS, ARMOR_FEET, WEARABLE, FISHING_ROD, SWORD, TOOL, BOW, BREAKABLE,
        // BooleanMatcher backed
        ANY, NONE,
        // ClassMatcher backed
        AXE, PICKAXE, HOE, SHOVEL, SHIELD, SHEARS
    }
    static {
        createDefaultEnumMatcher(Type.ARMOR_HEAD, EnumEnchantmentType.ARMOR);
        createDefaultEnumMatcher(Type.ARMOR_HEAD, EnumEnchantmentType.ARMOR_HEAD, Items.IRON_HELMET);
        createDefaultEnumMatcher(Type.ARMOR_CHEST, EnumEnchantmentType.ARMOR_CHEST, Items.IRON_CHESTPLATE);
        createDefaultEnumMatcher(Type.ARMOR_LEGS, EnumEnchantmentType.ARMOR_LEGS, Items.IRON_LEGGINGS);
        createDefaultEnumMatcher(Type.ARMOR_FEET, EnumEnchantmentType.ARMOR_FEET, Items.IRON_BOOTS);
        createDefaultEnumMatcher(Type.WEARABLE, EnumEnchantmentType.WEARABLE);

        createDefaultEnumMatcher(Type.FISHING_ROD, EnumEnchantmentType.FISHING_ROD, Items.FISHING_ROD);
        createDefaultEnumMatcher(Type.SWORD, EnumEnchantmentType.WEAPON, Items.IRON_SWORD);
        createDefaultEnumMatcher(Type.BOW, EnumEnchantmentType.BOW, Items.BOW);
        createDefaultEnumMatcher(Type.TOOL, EnumEnchantmentType.DIGGER);

        createDefaultBooleanMatcher(Type.ANY, true);
        createDefaultBooleanMatcher(Type.NONE, false);

        createDefaultEnumMatcher(Type.BREAKABLE, EnumEnchantmentType.BREAKABLE);

        createDefaultClassMatcher(Type.AXE, ItemAxe.class, Items.IRON_AXE);
        createDefaultClassMatcher(Type.PICKAXE, ItemPickaxe.class, Items.IRON_PICKAXE);
        createDefaultClassMatcher(Type.HOE, ItemHoe.class, Items.IRON_HOE);
        createDefaultClassMatcher(Type.SHOVEL, ItemSpade.class, Items.IRON_SHOVEL);
        createDefaultClassMatcher(Type.SHIELD, ItemShield.class, Items.SHIELD);
        createDefaultClassMatcher(Type.SHEARS, ItemShears.class, Items.SHEARS);
    }

    private static void createDefaultMatcher(Type type, IMatcher<ItemTypeContext> innerMatcher, Item item) {
        ItemTypeMatcher matcher = new ItemTypeMatcher(type.name(), innerMatcher, item != null ? new ItemStack(item) : null);
        registeredDefaultMatchers.put(type, matcher);
    }

    private static void createDefaultBooleanMatcher(Type type, boolean defValue) {
        createDefaultMatcher(type, new BooleanMatcher<>(defValue), null);
    }

    private static void createDefaultClassMatcher(Type type, Class<? extends Item> clazz, Item item) {
        createDefaultMatcher(type, MatcherCreator.ITEM_TYPE.createClassMatcher(clazz), item);
    }

    private static void createDefaultEnumMatcher(Type type, EnumEnchantmentType enm) {
        createDefaultEnumMatcher(type, enm, null);
    }

    private static void createDefaultEnumMatcher(Type type, EnumEnchantmentType enm, Item item) {
        registeredDefaultMatchers.put(type, new EnumEnchantmentTypeMatcher(enm, item));
    }
}
