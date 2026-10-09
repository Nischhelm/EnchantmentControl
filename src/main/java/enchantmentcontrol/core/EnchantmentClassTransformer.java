package enchantmentcontrol.core;

import enchantmentcontrol.config.ConfigHandler;
import meldexun.asmutil2.ClassNodeClassTransformer;
import meldexun.asmutil2.ClassNodeTransformer;
import meldexun.asmutil2.NonLoadingClassWriter;
import meldexun.betterconfig.asm.BetterConfigClassTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnchantmentClassTransformer extends ClassNodeClassTransformer implements IClassTransformer {
    private static final Set<String> enchantmentClasses = new HashSet<>(Arrays.asList(
            "net.minecraft.enchantment.Enchantment",
            "alk" //obfuscated Enchantment
    ));

    @Override
    @SuppressWarnings("deprecation")
    protected ClassWriter createClassWriter(int flags) {
        return new NonLoadingClassWriter(flags, ReflectionHelper.getPrivateValue(BetterConfigClassTransformer.class, null, "REMAPPING_CLASS_UTIL"));
    }

    @Override
    protected List<ClassNodeTransformer> getClassNodeTransformers(String transformedName) {
        return Arrays.asList(
                ClassNodeTransformer.create(0, classNode -> {
                    // Keep a list of all enchantment classes
                    String superClassName = classNode.superName.replace('/', '.');
                    if (enchantmentClasses.contains(superClassName)) {
                        enchantmentClasses.add(transformedName);
                        enchantmentClasses.add(classNode.name.replace('/', '.'));
                    }
                    // Not transforming anything
                    return false;
                }),
                ClassNodeTransformer.create(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, classNode -> {
                    if(!enchantmentClasses.contains(transformedName)) return false;
                    if(ConfigHandler.debug.disabledClasses.contains(transformedName)) return false;

                    // Transform all Enchantment methods with hooks
                    for (MethodNode method : classNode.methods) {
                        // Skip abstract, static, and synthetic methods
                        if((method.access & Opcodes.ACC_ABSTRACT) != 0) continue;
                        if((method.access & Opcodes.ACC_STATIC) != 0) continue;
                        if((method.access & Opcodes.ACC_SYNTHETIC) != 0) continue;

                        switch (method.name) {
                            // Basic properties
                            case "getMinLevel": case "func_77319_d": if(method.desc.equals("()I")) injectAtHeadAndMaybeCancel(method, "getMinLevel", Type.INT_TYPE, Opcodes.IRETURN); break;
                            case "getMaxLevel": case "func_77325_b": if(method.desc.equals("()I")) injectAtHeadAndMaybeCancel(method, "getMaxLevel", Type.INT_TYPE, Opcodes.IRETURN); break;
                            case "getMinEnchantability": case "func_77321_a": if(method.desc.equals("(I)I")) injectAtHeadAndMaybeCancel(method, "getMinEnchantability", Type.INT_TYPE, Opcodes.IRETURN); break;
                            case "getMaxEnchantability": case "func_77317_b": if(method.desc.equals("(I)I")) injectAtHeadAndMaybeCancel(method, "getMaxEnchantability", Type.INT_TYPE, Opcodes.IRETURN); break;
                            case "getTranslatedName": case "func_77316_c": if(method.desc.equals("(I)Ljava/lang/String;")) injectAtHeadAndMaybeCancel(method, "getTranslatedName", Type.getType(String.class), Opcodes.ARETURN); break;
                            case "isTreasureEnchantment": case "func_185261_e": if(method.desc.equals("()Z")) injectAtHeadAndMaybeCancel(method, "isTreasureEnchantment", Type.BOOLEAN_TYPE, Opcodes.IRETURN); break;
                            case "isCurse": case "func_190936_d": if(method.desc.equals("()Z")) injectAtHeadAndMaybeCancel(method, "isCurse", Type.BOOLEAN_TYPE, Opcodes.IRETURN); break;
                            case "isAllowedOnBooks": if(method.desc.equals("()Z")) injectAtHeadAndMaybeCancel(method, "isAllowedOnBooks", Type.BOOLEAN_TYPE, Opcodes.IRETURN); break;

                            // Applicability
                            case "canApplyTogether": case "func_77326_a": if(method.desc.equals("(Lnet/minecraft/enchantment/Enchantment;)Z")) injectAtHeadAndMaybeCancel(method, "isCompatibleWith", Type.BOOLEAN_TYPE, Opcodes.IRETURN); break;
                            case "canApply": case "func_92089_a": if(method.desc.equals("(Lnet/minecraft/item/ItemStack;)Z")) injectAtHeadAndMaybeCancel(method, "canApplyAtAnvil", Type.BOOLEAN_TYPE, Opcodes.IRETURN); break;
                            case "canApplyAtEnchantingTable": if(method.desc.equals("(Lnet/minecraft/item/ItemStack;)Z")) injectAtHeadAndMaybeCancel(method, "canApplyInGeneral", Type.BOOLEAN_TYPE, Opcodes.IRETURN); break;

                            // Vanilla behaviors
                            case "calcModifierDamage": case "func_77318_a": if(method.desc.equals("(ILnet/minecraft/util/DamageSource;)I")) injectAtHeadAndMaybeCancel(method, "protectionBehavior", Type.INT_TYPE, Opcodes.IRETURN); break;
                            case "calcDamageByCreature": case "func_152376_a": if(method.desc.equals("(ILnet/minecraft/entity/EnumCreatureAttribute;)F")) injectAtHeadAndMaybeCancel(method, "sharpnessBehavior", Type.FLOAT_TYPE, Opcodes.FRETURN); break;
                            case "onEntityDamaged": case "func_151368_a": if(method.desc.equals("(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/entity/Entity;I)V")) injectAtHeadAndMaybeCancel(method, "arthropodBehavior", Type.VOID_TYPE, Opcodes.RETURN); break;
                            case "onUserHurt": case "func_151367_b": if(method.desc.equals("(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/entity/Entity;I)V")) injectAtHeadAndMaybeCancel(method, "thornsBehavior", Type.VOID_TYPE, Opcodes.RETURN); break;
                        }
                    }

                    return true;
                })
        );
    }

    private void injectAtHeadAndMaybeCancel(MethodNode method, String hookMethod, Type returnType, int returnOpcode) {
        String callbackInfoClassName = CallbackInfoAccessor.getCallbackInfoClassName(returnType);
        String constructorDesc = "(Ljava/lang/String;Z)V"; // Simple constructor without initial return value
        String hookSig = buildHookMethodDescriptor(method.desc, callbackInfoClassName);

        int ciLVTSlot = getFirstOpenLVTSlot(method);
        InsnList injectedInstructions = new InsnList();

        // CallbackInfo ci = new CallbackInfo("enchantmentcontrol_asm", true); --- or CallbackInfoReturnable
        injectedInstructions.add(new TypeInsnNode(Opcodes.NEW, callbackInfoClassName));
        injectedInstructions.add(new InsnNode(Opcodes.DUP));
        injectedInstructions.add(new LdcInsnNode("enchantmentcontrol_asm"));
        injectedInstructions.add(new InsnNode(Opcodes.ICONST_1));
        injectedInstructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, callbackInfoClassName, "<init>", constructorDesc, false));
        injectedInstructions.add(new VarInsnNode(Opcodes.ASTORE, ciLVTSlot));

        // hookMethod(this, params, ci);
        loadParameters(injectedInstructions, method);
        injectedInstructions.add(new VarInsnNode(Opcodes.ALOAD, ciLVTSlot)); // callback info
        injectedInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "enchantmentcontrol/core/EnchantmentHooks", hookMethod, hookSig, false));

        // Check isCancelled
        // if(ci.isCancelled() == 0) [jump to labelToOriginal]
        injectedInstructions.add(new VarInsnNode(Opcodes.ALOAD, ciLVTSlot));
        injectedInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, callbackInfoClassName, "isCancelled", "()Z", false));
        LabelNode labelToOriginal = new LabelNode();
        injectedInstructions.add(new JumpInsnNode(Opcodes.IFEQ, labelToOriginal));

        // else ...
        if (returnType.equals(Type.VOID_TYPE)) {
            // return;
            injectedInstructions.add(new InsnNode(Opcodes.RETURN));
        } else {
            // return ci.getReturnValue(); --- or any of the primitive variants
            injectedInstructions.add(new VarInsnNode(Opcodes.ALOAD, ciLVTSlot));
            String returnAccessor = CallbackInfoAccessor.getReturnAccessor(returnType);
            String returnDescriptor = CallbackInfoAccessor.getReturnDescriptor(returnType);
            injectedInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, callbackInfoClassName, returnAccessor, returnDescriptor, false));
            if (returnType.getSort() >= Type.ARRAY) injectedInstructions.add(new TypeInsnNode(Opcodes.CHECKCAST, returnType.getInternalName())); //objects need to check type
            injectedInstructions.add(new InsnNode(returnOpcode));
        }

        // original code
        injectedInstructions.add(labelToOriginal);

        method.instructions.insert(injectedInstructions);
    }

    private void loadParameters(InsnList insnList, MethodNode method) {
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));

        int slot = 1;

        for (Type type : Type.getArgumentTypes(method.desc)) {
            switch (type.getSort()) {
                case Type.BOOLEAN: case Type.BYTE: case Type.CHAR: case Type.SHORT: case Type.INT:
                    insnList.add(new VarInsnNode(Opcodes.ILOAD, slot)); break;
                case Type.FLOAT: insnList.add(new VarInsnNode(Opcodes.FLOAD, slot)); break;
                case Type.LONG: insnList.add(new VarInsnNode(Opcodes.LLOAD, slot)); break;
                case Type.DOUBLE: insnList.add(new VarInsnNode(Opcodes.DLOAD, slot)); break;
                case Type.ARRAY: case Type.OBJECT:
                    insnList.add(new VarInsnNode(Opcodes.ALOAD, slot)); break;
            }
            slot += type.getSize();
        }
    }

    // Transform target method descriptor (X)Y
    // to hook method descriptor (Lnet/minecraft/enchantment/Enchantment;X;L..callbackInfo..;)V
    private String buildHookMethodDescriptor(String originalDesc, String callbackInfoClass) {
        String targetMethodParams = originalDesc.substring(1, originalDesc.indexOf(')'));
        return "(Lnet/minecraft/enchantment/Enchantment;" + targetMethodParams + "L" + callbackInfoClass + ";)V";
    }

    // Calculate first available local variable slot for CallbackInfo
    private int getFirstOpenLVTSlot(MethodNode method) {
        Type[] argTypes = Type.getArgumentTypes(method.desc);
        int slot = 1; // Start after 'this'
        for (Type type : argTypes)
            slot += type.getSize();
        return slot;
    }
}