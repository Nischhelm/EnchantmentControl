package enchantmentcontrol.compat.jei;

import enchantmentcontrol.config.provider.IncompatibleConfigProvider;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JEIPlugin
@SuppressWarnings("unused")
public class IncompatibleJeiPlugin implements IModPlugin {

    @Override
    public void register(IModRegistry registry) {
        for(Enchantment enchantment : Enchantment.REGISTRY) {
            for(int lvl = enchantment.getMinLevel(); lvl <= enchantment.getMaxLevel(); lvl++) {
                addDescription(registry, enchantment, lvl);
            }
        }
    }

    private static void addDescription(IModRegistry registry, Enchantment enchantment, int lvl) {
        ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantedBook.addEnchantment(stack, new EnchantmentData(enchantment, lvl));
        if(!IncompatibleConfigProvider.incompatibleEnchantments.get(enchantment).isEmpty()) {
            List<String> desc = new ArrayList<>();
            desc.add(I18n.translateToLocal("jei.enchantmentcontrol.incompatible.desc"));
            desc.add("");
            desc.addAll(IncompatibleConfigProvider.incompatibleEnchantments.get(enchantment).stream().map(e -> TextFormatting.DARK_GRAY + I18n.translateToLocal(e.getName())).collect(Collectors.toList()));
            registry.addIngredientInfo(stack, ItemStack.class, desc.toArray(new String[0]));
        }
    }
}
