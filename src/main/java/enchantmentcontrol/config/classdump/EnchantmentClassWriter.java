package enchantmentcontrol.config.classdump;

import enchantmentcontrol.core.EnchantmentControlPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class EnchantmentClassWriter {
    public static void postInit(){
        //Write current mappings ench_id; org.some.mod.has.EnchClass
        List<String> lines = new ArrayList<>();

        lines.add("!! This file is used by EnchantmentControl internally and automatically. Not a config !!");
        lines.addAll(EnchantmentControlPlugin.actuallyEarlyEnchants);

        Path configPath = Paths.get(EnchantmentClassReader.DUMP_PATH);
        try {
            Files.createDirectories(configPath.getParent());
            Files.write(configPath, lines);
        }
        catch(IOException ignored) {}
    }
}
