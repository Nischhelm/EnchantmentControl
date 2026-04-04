package enchantmentcontrol.compat.jei;

import com.sun.imageio.plugins.common.I18N;
import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.config.folders.ItemTypeConfig;
import enchantmentcontrol.config.provider.IncompatibleConfigProvider;
import enchantmentcontrol.config.provider.ItemTypeConfigProvider;
import enchantmentcontrol.mixin.vanilla.accessor.EnchantmentAccessor;
import enchantmentcontrol.util.AnvilCostUtil;
import enchantmentcontrol.util.EnchantmentInfo;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import java.util.*;
import java.util.stream.Collectors;

@JEIPlugin
@SuppressWarnings("unused")
public class EnchantmentInfoJeiPlugin implements IModPlugin {

    @Override
    public void register(IModRegistry registry) {
        if (ConfigHandler.compat.jei.jeiInfoEnabled) {
            for (Enchantment enchantment : Enchantment.REGISTRY) {
                for (int lvl = enchantment.getMinLevel(); lvl <= enchantment.getMaxLevel(); lvl++) {
                    addDescription(registry, enchantment, lvl);
                }
            }
        }
    }

    private static final List<String> body = Arrays.asList("HEAD", "CHEST", "LEGS", "FEET");
    private static final List<String> hands = Arrays.asList("MAINHAND", "OFFHAND");

    private static void addDescription(IModRegistry registry, Enchantment enchantment, int lvl) {
        ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantedBook.addEnchantment(stack, new EnchantmentData(enchantment, lvl));
        List<String> desc = new ArrayList<>();

        if (ConfigHandler.compat.jei.jeiRarityTreasureInfo) {
            TextFormatting color = TextFormatting.DARK_GRAY;
            if(enchantment.getRarity().ordinal() < EnumRarity.values().length) {
                color = EnumRarity.values()[enchantment.getRarity().ordinal()].color;
            }
            String rarityTreasureCurse = I18n.format("jei.enchantmentcontrol.rarity.desc", color.toString(), enchantment.getRarity());
            rarityTreasureCurse += enchantment.isTreasureEnchantment() ? " " + I18n.format("jei.enchantmentcontrol.treasure.desc") : "";
            if(enchantment.isCurse())
                rarityTreasureCurse += " " + I18n.format("jei.enchantmentcontrol.curse.desc");
            desc.add(rarityTreasureCurse);
            desc.add("");
        }

        if (ConfigHandler.compat.jei.jeiIncompatInfo && !IncompatibleConfigProvider.incompatibleEnchantments.get(enchantment).isEmpty()) {
            desc.add(I18n.format("jei.enchantmentcontrol.incompatible.desc",
                    IncompatibleConfigProvider.incompatibleEnchantments.get(enchantment).stream()
                            .map(e -> I18n.format(e.getName()))
                            .collect(Collectors.joining(", "))));
        }

        if (ConfigHandler.compat.jei.jeiItemTypesInfo) {
            if(ItemTypeConfigProvider.itemTypes.get(enchantment) != null && !ItemTypeConfigProvider.itemTypes.get(enchantment).isEmpty()) {
                desc.add(I18n.format("jei.enchantmentcontrol.itemtypes.desc",
                        ItemTypeConfigProvider.itemTypes.get(enchantment).stream()
                                .map(e -> I18n.format(e.getName()))
                                .collect(Collectors.joining(", "))
                ));
            }
            if(ItemTypeConfigProvider.itemTypesAnvil.get(enchantment) != null && !ItemTypeConfigProvider.itemTypesAnvil.get(enchantment).isEmpty()) {
                desc.add(I18n.format("jei.enchantmentcontrol.itemtypes.anvil.desc",
                        ItemTypeConfigProvider.itemTypesAnvil.getOrDefault(enchantment, new HashSet<>()).stream()
                                .map(e -> I18n.format(e.getName()))
                                .collect(Collectors.joining(", "))
                ));
            }
        }

        if(ConfigHandler.compat.jei.jeiSlotInfo && enchantment.getRegistryName() != null) {
            Set<String> slots = Arrays.stream(((EnchantmentAccessor) enchantment).getSlots()).map(Enum::toString).collect(Collectors.toSet());
            if(slots.containsAll(body)) {
                body.forEach(slots::remove);
                slots.add("BODY");
            }
            if(slots.containsAll(hands)) {
                hands.forEach(slots::remove);
                slots.add("HANDS");
            }
            if(!slots.isEmpty())
                desc.add(I18n.format("jei.enchantmentcontrol.slots.desc", String.join(", ", slots)));
        }

        desc.add("");

        if(ConfigHandler.compat.jei.jeiXPEmeraldPriceInfo){
            int xp = lvl * AnvilCostUtil.getRarityMultiplier(enchantment.getRarity(), true);
            int xpItem = lvl * AnvilCostUtil.getRarityMultiplier(enchantment.getRarity(), false);
            int minEm = 2 + 3 * lvl;
            int maxEm = 6 + 13 * lvl;
            boolean doublePrice = enchantment.isTreasureEnchantment();
            EnchantmentInfo info = EnchantmentInfo.get(enchantment);
            if(info!=null && info.overwritesDoublePrice) doublePrice = info.doublePrice;
            if(doublePrice){
                minEm *= 2;
                maxEm *= 2;
            }
            minEm = MathHelper.clamp(minEm, 1, 64);
            maxEm = MathHelper.clamp(maxEm, 1, 64);

            String xpString = xp == xpItem ? "" + xp : xp + "/" + xpItem;
            desc.add(I18n.format("jei.enchantmentcontrol.price.desc", xpString, minEm, maxEm));
        }

        if (ConfigHandler.compat.jei.jeiEnchantabilityInfo) {
            int maxEnchActual = enchantment.getMaxEnchantability(lvl);
            int maxEnch = lvl >= enchantment.getMaxLevel() ? maxEnchActual : enchantment.getMinEnchantability(lvl + 1) - 1;
            desc.add(I18n.format("jei.enchantmentcontrol.enchantability.desc", enchantment.getMinEnchantability(lvl), maxEnch, maxEnchActual));
        }

        if(ConfigHandler.compat.jei.jeiEnchIdInfo && enchantment.getRegistryName() != null)
            desc.add(I18n.format("jei.enchantmentcontrol.id.desc", I18n.format(enchantment.getRegistryName().toString())));

        if (!desc.stream().allMatch(String::isEmpty)) {
            desc.add(0, I18n.format("jei.enchantmentcontrol.name.desc", I18n.format(enchantment.getTranslatedName(lvl))));
            registry.addIngredientInfo(stack, ItemStack.class, desc.toArray(new String[0]));
        }
    }
}
