package enchantmentcontrol.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class MixinModifierClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if(!name.equals("enchantmentcontrol.mixin.EnchantmentMixin")) return basicClass;

        //Read
        ClassReader classReader = new ClassReader(basicClass);

        //Modify
        ModifyMixinAnnotationClassNode classNode = new ModifyMixinAnnotationClassNode();
        classReader.accept(classNode, 0);

        //Write back
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);
        return cw.toByteArray();
    }
}
