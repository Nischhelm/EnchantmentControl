package enchantmentcontrol.asm;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MixinModifier {
    @SuppressWarnings("unchecked")
    //All of this just to have a dynamic @Mixin target in EnchantmentMixin
    public static void modifyMixinAnnotation() {
        File base = new File(".", "mods");
        if (!base.isDirectory() || !base.exists()) return;

        File[] modJars = base.listFiles((dir, name) -> name.endsWith(".jar"));
        if (modJars == null) return;
        for (File modJar : modJars) {
            if (!modJar.isFile() || !modJar.getName().startsWith("EnchantmentControl")) continue; //This is a bit janky. what if ppl rename the jar or put it in a subfolder?
            try (JarFile inputJar = new JarFile(modJar)) {

                Enumeration<JarEntry> entries = inputJar.entries();
                while(entries.hasMoreElements()){
                    JarEntry entry = entries.nextElement();

                    if (entry.getName().equals("enchantmentcontrol/mixin/EnchantmentMixin.class")) {
                        //Read .class file to ClassNode
                        InputStream inputStream = inputJar.getInputStream(entry);
                        ClassReader classReader = new ClassReader(inputStream);
                        ModifyMixinAnnotationClassNode classNode = new ModifyMixinAnnotationClassNode();
                        classReader.accept(classNode, 0);

                        //Write ClassNode to byte array
                        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                        classNode.accept(cw);
                        byte[] data = cw.toByteArray();

                        Field loadedData = LaunchClassLoader.class.getDeclaredField("resourceCache");
                        loadedData.setAccessible(true);
                        ((Map<String,byte[]>) loadedData.get(Launch.classLoader)).put("enchantmentcontrol.mixin.EnchantmentMixin", data);

                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }
    }
}
