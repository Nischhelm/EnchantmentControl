package enchantmentcontrol.config.classdump;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class EnchantmentClassWriter {
    private static final String SEP = "; ";
    public static void postInit(){
        //Write current mappings ench_id; org.some.mod.has.EnchClass
        List<String> lines = new ArrayList<>();
        Enchantment.REGISTRY.forEach(e -> {
            ResourceLocation enchid = e.getRegistryName();
            String originalEnchId = enchid.toString();

            lines.add(originalEnchId + SEP + e.getClass().getName());
        });

        Path configPath = Paths.get(EnchantmentClassReader.DUMP_PATH);
        try {
            Files.createDirectories(configPath.getParent());
            Files.write(configPath, lines);
        }
        catch(IOException ignored) {}
    }
}
