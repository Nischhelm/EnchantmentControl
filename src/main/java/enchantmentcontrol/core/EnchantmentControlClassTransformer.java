package enchantmentcontrol.core;

import enchantmentcontrol.config.EarlyConfigReader;
import enchantmentcontrol.config.classdump.EnchantmentClassReader;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.util.Annotations;

import java.util.List;

public class EnchantmentControlClassTransformer implements IClassTransformer {
    private static final String mixinDesc = Type.getDescriptor(Mixin.class);

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!name.equals("enchantmentcontrol.mixin.modded.EnchantmentMixin")) return basicClass;

        //Modify
        ClassNode classNode = new ClassNode(Opcodes.ASM5) {
            private AnnotationNode node;

            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                AnnotationVisitor node = super.visitAnnotation(desc, visible);
                if (desc.equals(mixinDesc)) {
                    this.node = (AnnotationNode) node;
                }
                return node;
            }

            @Override
            public void visitEnd() {
                List<String> modifiedEnchClasses = EnchantmentClassReader.read();
                modifiedEnchClasses.removeIf(s -> s.startsWith("net.minecraft.enchantment"));
                EarlyConfigReader.getClassBlacklistConfig().forEach(modifiedEnchClasses::remove);
                Annotations.setValue(this.node, "targets", modifiedEnchClasses);
            }
        };
        new ClassReader(basicClass).accept(classNode, 0);

        //Write back
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);
        return cw.toByteArray();
    }
}
