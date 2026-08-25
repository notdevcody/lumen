package pl.tomgirl.lenis.plugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.util.Annotations;

final class CompatibilityStubBuilder {
    private CompatibilityStubBuilder() {
    }

    static void build(ClassNode targetClass) {
        for (MethodNode method : targetClass.methods) {
            AnnotationNode annotation = Annotations.getInvisible(method, CompatStub.class);
            if (annotation == null) {
                continue;
            }
            if ((method.access & Opcodes.ACC_STATIC) == 0) {
                throw new IllegalStateException("CompatStub must be static: " + targetClass.name + "::" + method.name + method.desc);
            }
            method.access &= ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED);
            method.access |= Opcodes.ACC_PUBLIC;

            String delegate = Annotations.getValue(annotation, "value", "");
            if (!delegate.isEmpty()) {
                buildDelegate(targetClass, method, delegate);
            }
        }
    }

    private static void buildDelegate(ClassNode targetClass, MethodNode stub, String delegate) {
        MethodNode target = targetClass.methods.stream()
            .filter(method -> method.name.equals(delegate) && method.desc.equals(stub.desc))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(
                "CompatStub target does not exist: " + targetClass.name + "::" + delegate + stub.desc
            ));
        if ((target.access & Opcodes.ACC_STATIC) == 0) {
            throw new IllegalStateException("CompatStub target must be static: " + targetClass.name + "::" + delegate + stub.desc);
        }

        stub.instructions.clear();
        stub.tryCatchBlocks.clear();
        if (stub.localVariables != null) {
            stub.localVariables.clear();
        }

        int local = 0;
        int stack = 0;
        for (Type argument : Type.getArgumentTypes(stub.desc)) {
            stub.instructions.add(new VarInsnNode(argument.getOpcode(Opcodes.ILOAD), local));
            local += argument.getSize();
            stack += argument.getSize();
        }
        stub.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, targetClass.name, delegate, stub.desc, false));
        stub.instructions.add(new InsnNode(Type.getReturnType(stub.desc).getOpcode(Opcodes.IRETURN)));
        stub.maxLocals = local;
        stub.maxStack = Math.max(stack, Type.getReturnType(stub.desc).getSize());
    }
}
