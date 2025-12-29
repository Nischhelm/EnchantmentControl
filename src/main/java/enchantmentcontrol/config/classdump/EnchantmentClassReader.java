package enchantmentcontrol.config.classdump;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class EnchantmentClassReader {
    public static final Map<String, String> mapIdToClass = new HashMap<>();

    public static final String DUMP_PATH = "config/enchantmentcontrol/data/enchclasses.dat";

    public static List<String> read(){
        Path enchclasses_path = Paths.get(DUMP_PATH);
        try {
            Files.createDirectories(enchclasses_path.getParent());

            for(String line : Files.readAllLines(enchclasses_path)){
                if(line.startsWith("!!")) continue;
                String[] split = line.split(";");
                String enchid = split[0].trim();
                String classPath = split[1].trim();
                mapIdToClass.put(enchid, classPath);
            }
        }
        catch(IOException ignored) {}

        return new ArrayList<>(new HashSet<>(mapIdToClass.values())); //remove duplicate classes
    }
}
