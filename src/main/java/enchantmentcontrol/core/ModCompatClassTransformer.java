package enchantmentcontrol.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.*;

public class ModCompatClassTransformer implements IClassTransformer {

    public static final Set<String> targetMethods = new HashSet<>(Arrays.asList(
//            "getMinLevel",
//            "getMaxLevel",
//            "getMinEnchantability",
//            "getMaxEnchantability",
//            "getTranslatedName",
//            "isTreasureEnchantment",
//            "isCurse",
//            "canApplyTogether",
//            "canApply",
//            "calcModifierDamage",
//            "calcDamageByCreature",
//            "onEntityDamaged",
//            "onUserHurt",
            "func_77319_d",
            "func_77325_b",
            "func_77321_a",
            "func_77317_b",
            "func_77316_c",
            "func_185261_e",
            "func_190936_d",
            "func_77326_a",
            "func_92089_a",
            "func_77318_a",
            "func_152376_a",
            "func_151368_a",
            "func_151367_b",
            "isAllowedOnBooks",
            "canApplyAtEnchantingTable"
    ));

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (name.equals("com.Shultrea.Rin.Ench0_4_0.EnchantmentFreezing")) {

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
            basicClass = cw.toByteArray();
        }

        //removing abstract overriding methods
        if(EnchantmentControlPlugin.enchantmentClasses.contains(name)){
            ClassNode classNode = new ClassNode(Opcodes.ASM5);
            new ClassReader(basicClass).accept(classNode, 0);

            List<MethodNode> toRemove = new ArrayList<>();
            classNode.methods.stream()
                    .filter(method -> targetMethods.contains(method.name))
                    .filter(method -> (method.access & Opcodes.ACC_ABSTRACT) > 0)
                    .forEach(toRemove::add);

            if(!toRemove.isEmpty()) {
                classNode.methods.removeAll(toRemove);

                //Write back
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                classNode.accept(cw);
                return cw.toByteArray();
            }
        }
        return basicClass;
    }

}
