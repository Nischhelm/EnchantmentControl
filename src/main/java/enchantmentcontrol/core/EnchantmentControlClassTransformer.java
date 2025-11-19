package enchantmentcontrol.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.util.Annotations;

import java.util.Arrays;

public class EnchantmentControlClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!name.equals("enchantmentcontrol.mixin.modded.EnchantmentMixin")) return basicClass;

        //Read
        ClassReader classReader = new ClassReader(basicClass);

        //Modify
        ClassNode classNode = new ClassNode(Opcodes.ASM5) {
            private AnnotationNode node;

            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                AnnotationVisitor node = super.visitAnnotation(desc, visible);
                if (desc.equals("Lorg/spongepowered/asm/mixin/Mixin;"))
                    this.node = (AnnotationNode) node;
                return node;
            }

            @Override
            public void visitEnd(){
                Annotations.setValue(node, "targets", Arrays.asList(
                        "com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase",
                        "com.Shultrea.Rin.Ench0_4_5.EnchantmentFrenzy"
                ));
            }
        };
        classReader.accept(classNode, 0);

        //Write back
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);
        return cw.toByteArray();
    }
}
