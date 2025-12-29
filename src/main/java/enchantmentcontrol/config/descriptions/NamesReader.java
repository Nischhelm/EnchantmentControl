package enchantmentcontrol.config.descriptions;

import enchantmentcontrol.mixin.vanilla.accessor.I18nAccessor;
import enchantmentcontrol.mixin.vanilla.accessor.I18nAccessor_translation;
import enchantmentcontrol.mixin.vanilla.accessor.LanguageMapAccessor;
import enchantmentcontrol.mixin.vanilla.accessor.LocaleAccessor;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class NamesReader {
    public static final String PATH = "config/enchantmentcontrol/names.txt";

    public static void init(){
        Map<String, String> enchToName = readNames();
        ((LocaleAccessor)I18nAccessor.getLocale()).getProperties().putAll(enchToName);
        ((LanguageMapAccessor) I18nAccessor_translation.getLocalizedName()).getLanguageList().putAll(enchToName);
    }

    private static Map<String, String> readNames(){
        Map<String, String> enchToName = new HashMap<>();
        Path path = Paths.get(PATH);
        try {
            Files.createDirectories(path.getParent());

            for(String line : Files.readAllLines(path)){
                String[] split = line.split("=");
                if(split.length < 2) continue;
                String enchid = split[0].trim();
                String translatedName = split[1].trim();
                Enchantment ench = Enchantment.getEnchantmentByLocation(enchid);
                if(ench == null) continue;

                enchToName.put(ench.getName(), translatedName);
            }
        }
        catch(IOException ignored) {}
        return enchToName;
    }
}
