package enchantmentcontrol.core;

import fermiumbooter.util.FermiumJarScanner;
import meldexun.asmutil2.ASMUtil;
import meldexun.asmutil2.HashMapClassNodeClassTransformer;
import meldexun.asmutil2.IClassTransformerRegistry;
import meldexun.betterconfig.asm.util.DeobfuscationUtil;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ModdedClassTransformer extends HashMapClassNodeClassTransformer implements IClassTransformer {

    @Override
    protected void registerTransformers(IClassTransformerRegistry registry) {
        if(FermiumJarScanner.isModPresent("enchanter_tools"))
            registry.add("com.sirsquidly.enchanter_tools.mixin.MixinContainerEnchantment", "enchantertools$overrideEnchantItem", ClassWriter.COMPUTE_MAXS, method -> {
                // Insert after onEnchant: ReEnchantUtil.incrementEnchantCount(player.getRNG(), result);
                LocalVariableNode player = ASMUtil.findLocalVariable(method, "player", "Lnet/minecraft/entity/player/EntityPlayer;");
                LocalVariableNode stack = ASMUtil.findLocalVariable(method, "result", "Lnet/minecraft/item/ItemStack;");

                InsnList toInsert = new InsnList();
                toInsert.add(new VarInsnNode(Opcodes.ALOAD, player.index));
                toInsert.add(DeobfuscationUtil.createObfMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/entity/player/EntityPlayer", "func_70681_au", "()Ljava/util/Random;", false));
                toInsert.add(new VarInsnNode(Opcodes.ALOAD, stack.index));
                toInsert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "enchantmentcontrol/util/ReEnchantUtil", "incrementEnchantCount", "(Ljava/util/Random;Lnet/minecraft/item/ItemStack;)V", false));

                method.instructions.insert(ASMUtil.first(method).methodInsnObf("onEnchant", "func_192024_a").find(), toInsert);
            });
    }
}