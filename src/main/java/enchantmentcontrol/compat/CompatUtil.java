package enchantmentcontrol.compat;

import fermiumbooter.util.FermiumJarScanner;

public class CompatUtil {

    public static final LoadedContainer somanyenchantments = new LoadedContainer("somanyenchantments");
    public static final LoadedContainer crafttweaker = new LoadedContainer("crafttweaker");
    public static final LoadedContainer contenttweaker = new LoadedContainer("contenttweaker");
    public static final LoadedContainer enchdesc = new LoadedContainer("enchdesc");
    public static final LoadedContainer expandedevents = new LoadedContainer("expandedevents");

    public static boolean versionInRange(LoadedContainer container, String versionRange) {
        return FermiumJarScanner.isModVersionInRange(container.key, versionRange);
    }

    public static class LoadedContainer{
        private final String key;
        private LoadedContainer(String key){
            this.key = key;
        }
        public boolean isLoaded(){
            return FermiumJarScanner.isModPresent(this.key);
        }
    }
}
