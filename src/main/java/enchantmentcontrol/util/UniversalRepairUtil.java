package enchantmentcontrol.util;

import enchantmentcontrol.Tags;
import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.config.folders.AnvilConfig;
import enchantmentcontrol.config.provider.ItemTypeConfigProvider;
import enchantmentcontrol.core.EnchantmentDummy;
import enchantmentcontrol.util.matchers.context.ItemTypeContext;
import enchantmentcontrol.util.matchers.itemtypes.ItemTypeMatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Config parsing and checks for the Universal Repair Item, see ContainerRepairMixin_UniversalRepair */
public class UniversalRepairUtil {
    private static boolean initialised = false;
    private static Item repairItem = null;
    private static int repairMeta = -1; // -1 = any meta
    private static final Set<Item> blacklistedItems = new HashSet<>();
    private static final List<ItemTypeMatcher> blacklistedTypes = new ArrayList<>();

    public static void resetConfigValues() {
        initialised = false;
        repairItem = null;
        repairMeta = -1;
        blacklistedItems.clear();
        blacklistedTypes.clear();
    }

    // Lazy since item types are only registered in postInit
    private static void init() {
        initialised = true;
        AnvilConfig.UniversalRepairConfig cfg = ConfigHandler.anvil.universalRepair;

        String[] split = cfg.itemName.trim().split(":");
        String name = split.length >= 2 ? split[0] + ":" + split[1] : cfg.itemName.trim();
        repairItem = Item.getByNameOrId(name);
        if (repairItem == null) Tags.LOGGER.warn("Could not find universal repair item {}", cfg.itemName);
        if (split.length >= 3) {
            try {
                repairMeta = Integer.parseInt(split[2].trim());
            } catch (NumberFormatException e) {
                Tags.LOGGER.warn("Invalid metadata for universal repair item {}", cfg.itemName);
            }
        }

        for (String entry : cfg.itemBlacklist) {
            entry = entry.trim();
            if (entry.isEmpty()) continue;
            if (entry.contains(":")) {
                Item item = Item.getByNameOrId(entry);
                if (item == null) Tags.LOGGER.warn("Could not find item {} in universal repair blacklist", entry);
                else blacklistedItems.add(item);
            } else {
                ItemTypeMatcher matcher = ItemTypeConfigProvider.getMatcher(entry);
                if (matcher == null) Tags.LOGGER.warn("Could not find item type {} in universal repair blacklist", entry);
                else blacklistedTypes.add(matcher);
            }
        }
    }

    /** @return true if the right stack is the universal repair item and can repair the left stack */
    public static boolean canRepair(ItemStack toRepair, ItemStack repairStack) {
        if (!initialised) init();
        if (repairItem == null || repairStack.getItem() != repairItem) return false;
        if (repairMeta != -1 && repairStack.getMetadata() != repairMeta) return false;
        if (!toRepair.getItem().isRepairable()) return false;
        return !isBlacklisted(toRepair);
    }

    /** Durability repaired per universal repair item */
    public static int getRepairAmount(int maxDamage) {
        return Math.max(1, MathHelper.ceil(maxDamage * ConfigHandler.anvil.universalRepair.repairAmount));
    }

    private static boolean isBlacklisted(ItemStack stack) {
        Item item = stack.getItem();
        if (blacklistedItems.contains(item)) return true;
        if (blacklistedTypes.isEmpty()) return false;
        ResourceLocation loc = item.getRegistryName();
        ItemTypeContext context = new ItemTypeContext(EnchantmentDummy.dummy, stack, item, loc == null ? "" : loc.toString());
        for (ItemTypeMatcher matcher : blacklistedTypes)
            if (matcher.matches(context)) return true;
        return false;
    }
}
