package enchantmentcontrol.core;

import fermiumbooter.util.FermiumJarScanner;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Re-Enchant uses Expanded Events' GenerateEnchantmentListEvent if it's installed, and its own mixins otherwise.
 */
public class ReEnchantMixinPlugin implements IMixinConfigPlugin {
    private static Boolean expandedEvents = null;

    private static boolean hasExpandedEvents() {
        if(expandedEvents == null) expandedEvents = FermiumJarScanner.isModPresent("expandedevents");
        return expandedEvents;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if(mixinClassName.endsWith("_ReEnchantRoll")) return !hasExpandedEvents();
        return true;
    }

    @Override public void onLoad(String mixinPackage) {}
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
