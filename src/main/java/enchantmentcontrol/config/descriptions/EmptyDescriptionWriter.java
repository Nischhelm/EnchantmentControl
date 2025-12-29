package enchantmentcontrol.config.descriptions;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class EmptyDescriptionWriter {
    public static void postInit(){
        Path configPath = Paths.get(DescriptionReader.PATH);
        if(Files.exists(configPath)) return;

        //Write empty line per enchant
        List<String> lines = new ArrayList<>();
        Enchantment.REGISTRY.forEach(e -> {
            ResourceLocation enchid = e.getRegistryName();
            if(enchid == null) return;

            // Add the concrete class of the registered enchantment
            lines.add(enchid.getNamespace() + ":" + enchid.getPath() + "=");
        });

        try {
            Files.createDirectories(configPath.getParent());
            Files.write(configPath, lines);
        }
        catch(IOException ignored) {}
    }
}
