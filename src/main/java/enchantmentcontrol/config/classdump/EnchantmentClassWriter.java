package enchantmentcontrol.config.classdump;

import enchantmentcontrol.EnchantmentControl;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnchantmentClassWriter {
    private static final String SEP = "; ";
    public static void postInit(){
        //Write current mappings ench_id; org.some.mod.has.EnchClass
        List<String> lines = new ArrayList<>();

        //Class names in use, directly referenced by an Enchantment Object or (at least!) inside the inheritance chain
        Set<String> usedDirectly = new HashSet<>();
        Set<String> usedIndirectly = new HashSet<>();

        Enchantment.REGISTRY.forEach(e -> {
            ResourceLocation enchid = e.getRegistryName();
            String originalEnchId = enchid.toString();

            // Add the concrete class of the registered enchantment
            lines.add(originalEnchId + SEP + e.getClass().getName());
            usedDirectly.add(e.getClass().getName());

            // Walk up the inheritance chain and record all intermediate classes
            // between the concrete enchantment class and net.minecraft.enchantment.Enchantment
            Class<?> cls = e.getClass();
            while (true) {
                cls = cls.getSuperclass();
                if (cls == null) break; // should not happen
                if (cls == Enchantment.class) break; // stop before reaching the base Enchantment class
                if (!Enchantment.class.isAssignableFrom(cls)) break;  // This should never happen

                usedIndirectly.add(cls.getName());
            }
        });

        //just for the classes.dump file we also dump not directly referenced classes in the Enchantment inheritance chain
        // these fake enchantment ids enchantmentcontrol:betweenclassX never get loaded into the game
        int c = 0;
        for(String usedIndirectlyCls : usedIndirectly)
            if(!usedDirectly.contains(usedIndirectlyCls)) //only the ones that are not directly referenced
                lines.add(EnchantmentControl.MODID + ":" + "betweenclass" + (c++) + SEP + usedIndirectlyCls);

        Path configPath = Paths.get(EnchantmentClassReader.DUMP_PATH);
        try {
            Files.createDirectories(configPath.getParent());
            Files.write(configPath, lines);
        }
        catch(IOException ignored) {}
    }
}
