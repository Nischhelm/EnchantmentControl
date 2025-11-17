package enchantmentcontrol.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.util.Annotations;

import java.util.ArrayList;
import java.util.Arrays;

//visits and modifies EnchantmentMixin.class
public class ModifyMixinAnnotationClassNode extends ClassNode {
	public ModifyMixinAnnotationClassNode() {
		super(Opcodes.ASM5);
	}

	@Override
	//Visits all annotations of the class
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		if (desc.equals("Lorg/spongepowered/asm/mixin/Mixin;")){
			MixinAnnotationNode node = new MixinAnnotationNode();

			if (invisibleAnnotations == null) invisibleAnnotations = new ArrayList<>(1);
			invisibleAnnotations.add(node);

			return node;
		}
		return super.visitAnnotation(desc, visible);
	}

	//modifies @Mixin Annotation
	private static class MixinAnnotationNode extends AnnotationNode {
		public MixinAnnotationNode() {
			super(Opcodes.ASM5, "Lorg/spongepowered/asm/mixin/Mixin;");
		}

		@Override
		public void visitEnd() {
			//TODO: read from config
			Annotations.setValue(this, "targets", Arrays.asList("com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase", "com.Shultrea.Rin.Ench0_4_5.EnchantmentFrenzy"));
		}
	}
}