package enchantmentcontrol.core;

import enchantmentcontrol.annotation.MixinAllSubClasses;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.util.Annotations;

import java.util.Arrays;

public class EnchantmentControlClassTransformer implements IClassTransformer {
    private static final String mixinAllSubClassesDesc = Type.getDescriptor(MixinAllSubClasses.class);
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
                if (desc.equals(mixinAllSubClassesDesc)) { //replace @MixinAllSubClasses with @Mixin
                    this.node = (AnnotationNode) node;
                    this.node.desc = mixinDesc;
                }
                else if(desc.equals(mixinDesc)){ //remove old @Mixin
                    this.invisibleAnnotations.remove(node);
                }
                return node;
            }

            @Override
            public void visitEnd() {
                Annotations.setValue(this.node, "targets", Arrays.asList(
                        //TODO: config list
                        "com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase",
                        "com.Shultrea.Rin.Ench0_4_5.EnchantmentFrenzy"
                ));
            }
        };
        new ClassReader(basicClass).accept(classNode, 0);

        //Write back
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);
        return cw.toByteArray();
    }
}
