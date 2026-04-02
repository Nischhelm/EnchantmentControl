package enchantmentcontrol.compat;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionRange;

public class CompatUtil {

    public static final LoadedContainer somanyenchantments = new LoadedContainer("somanyenchantments");
    public static final LoadedContainer contenttweaker = new LoadedContainer("contenttweaker");

    public static boolean versionInRange(LoadedContainer container, String version) {
        if (!container.isLoaded()) return false;
        VersionRange range;
        try {
            range = VersionRange.createFromVersionSpec(version);
        } catch (Exception e) {
            return false;
        }
        return range.containsVersion(container.getVersion());
    }

    public static class LoadedContainer{
        private Boolean isLoaded = null;
        private DefaultArtifactVersion version;
        private final String key;
        private LoadedContainer(String key){
            this.key = key;
        }
        public boolean isLoaded(){
            if(this.isLoaded == null) isLoaded = Loader.isModLoaded(key);
            return isLoaded;
        }
        public DefaultArtifactVersion getVersion(){
            if(version == null) version = new DefaultArtifactVersion(Loader.instance().getIndexedModList().get(key).getVersion());
            return version;
        }
    }
}
