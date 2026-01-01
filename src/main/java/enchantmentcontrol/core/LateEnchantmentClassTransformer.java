package enchantmentcontrol.core;

import enchantmentcontrol.config.EarlyConfigReader;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.util.Annotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class LateEnchantmentClassTransformer implements IClassTransformer {
    private static final String mixinDesc = Type.getDescriptor(Mixin.class);

    public static boolean isLate = false;

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        //This class modifies the @Mixin annotation of modded.EnchantmentMixin to include all late enchantments
        if(!isLate && EnchantmentControlPlugin.enchantmentClasses.contains(name))
            EnchantmentControlPlugin.actuallyEarlyEnchants.add(name);

        if (!name.equals("enchantmentcontrol.mixin.modded.EnchantmentMixin")) return basicClass;
        isLate = true; //we declare it being late when mixins loads the mixin class

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
                Set<String> modifiedEnchClasses = new HashSet<>(EnchantmentControlPlugin.enchantmentClasses);
                modifiedEnchClasses.removeAll(EnchantmentControlPlugin.actuallyEarlyEnchants);
                EarlyConfigReader.getClassBlacklistConfig().forEach(modifiedEnchClasses::remove);
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
