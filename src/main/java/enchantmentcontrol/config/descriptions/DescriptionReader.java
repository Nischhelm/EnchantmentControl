package enchantmentcontrol.config.descriptions;

import enchantmentcontrol.mixin.vanilla.accessor.I18nAccessor;
import enchantmentcontrol.mixin.vanilla.accessor.I18nAccessor_translation;
import enchantmentcontrol.mixin.vanilla.accessor.LanguageMapAccessor;
import enchantmentcontrol.mixin.vanilla.accessor.LocaleAccessor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class DescriptionReader {
    public static final String PATH = "config/enchantmentcontrol/descriptions.txt";

    public static void init(){
        Map<String,String> injectTarget = ((LocaleAccessor)I18nAccessor.getLocale()).getProperties();
        Map<String,String> injectTarget2 = ((LanguageMapAccessor)I18nAccessor_translation.getLocalizedName()).getLanguageList();

        readDescriptions().forEach((enchid, desc) -> {
            String key = "enchantment."+enchid.getNamespace()+"."+enchid.getPath()+".desc";
            injectTarget.put(key, desc);
            injectTarget2.put(key, desc);
        });
    }

    private static Map<ResourceLocation, String> readDescriptions(){
        Map<ResourceLocation, String> enchToDesc = new HashMap<>();
        Path path = Paths.get(PATH);
        try {
            Files.createDirectories(path.getParent());

            for(String line : Files.readAllLines(path)){
                String[] split = line.split("=", 2); //descriptions can contain =
                if(split.length < 2) continue;
                String enchid = split[0].trim();
                String desc = split[1].trim();
                enchToDesc.put(new ResourceLocation(enchid), desc);
            }
        }
        catch(IOException ignored) {}
        return enchToDesc;
    }
}
