package enchantmentcontrol.core;

import enchantmentcontrol.config.EarlyConfigReader;
import enchantmentcontrol.config.classdump.EnchantmentClassReader;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.util.Annotations;

import java.util.ArrayList;
import java.util.Set;

public class EarlyEnchantmentClassTransformer implements IClassTransformer {
    private static final String mixinDesc = Type.getDescriptor(Mixin.class);

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        //This class modifies the @Mixin annotation of VanillaEnchantmentMixin to include all early enchantments
        if(!name.equals("enchantmentcontrol.mixin.vanilla.VanillaEnchantmentMixin")) return basicClass;

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
                Set<String> modifiedEnchClasses = EnchantmentClassReader.getEarlyClasses(); // already doesnt contain early enchants
                EarlyConfigReader.getClassBlacklistConfig().forEach(modifiedEnchClasses::remove);
                System.out.println("EnchantmentControl modifying " + modifiedEnchClasses.size() + " early enchantment classes");
                Annotations.setValue(this.node, "targets", new ArrayList<>(modifiedEnchClasses));
            }
        };
        new ClassReader(basicClass).accept(classNode, 0);

        //Write back
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);
        return cw.toByteArray();
    }
}
