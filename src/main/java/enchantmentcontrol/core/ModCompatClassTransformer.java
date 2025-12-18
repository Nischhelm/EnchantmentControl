package enchantmentcontrol.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

public class ModCompatClassTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!name.equals("com.Shultrea.Rin.Ench0_4_0.EnchantmentFreezing")) return basicClass;

        //Modify
        ClassNode classNode = new ClassNode(Opcodes.ASM5) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                if (name.equals("onEntityDamaged")) name = "onLivingDamage"; //rename
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
        };
        new ClassReader(basicClass).accept(classNode, 0);

        //Write back
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);
        return cw.toByteArray();
    }
}
