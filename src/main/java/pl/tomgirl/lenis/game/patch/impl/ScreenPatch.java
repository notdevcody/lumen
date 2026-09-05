package pl.tomgirl.lenis.game.patch.impl;

import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import pl.tomgirl.lenis.game.patch.Patch;

public final class ScreenPatch extends Patch {
    private static final String CLIPBOARD = "java/awt/datatransfer/Clipboard";
    private final Map<Method, ClipboardMethod> methods = new HashMap<>();

    public ScreenPatch(ClassVisitor next) { super(next); }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor next = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new MethodVisitor(Opcodes.ASM9, next) {
            private boolean gets;
            private boolean sets;

            @Override
            public void visitMethodInsn(int opcode, String owner, String called, String calledDescriptor, boolean itf) {
                if (owner.equals(CLIPBOARD)) {
                    gets |= called.equals("getContents");
                    sets |= called.equals("setContents");
                }
                super.visitMethodInsn(opcode, owner, called, calledDescriptor, itf);
            }

            @Override
            public void visitEnd() {
                if (gets && descriptor.equals("()Ljava/lang/String;")) methods.put(new Method(name, descriptor), ClipboardMethod.GET);
                if (sets && descriptor.equals("(Ljava/lang/String;)V")) methods.put(new Method(name, descriptor), ClipboardMethod.SET);
                super.visitEnd();
            }
        };
    }

    @Override
    public boolean matches() {
        return !methods.isEmpty();
    }

    @Override
    public ClassVisitor apply(ClassVisitor writer) {
        return new ClassVisitor(Opcodes.ASM9, writer) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                ClipboardMethod patch = methods.get(new Method(name, descriptor));
                if (patch == null) return super.visitMethod(access, name, descriptor, signature, exceptions);
                MethodVisitor method = super.visitMethod(access, name, descriptor, signature, exceptions);
                method.visitCode();
                if (patch == ClipboardMethod.GET) {
                    method.visitMethodInsn(Opcodes.INVOKESTATIC, HOOKS, "getClipboard", "()Ljava/lang/String;", false);
                    method.visitInsn(Opcodes.ARETURN);
                } else {
                    method.visitVarInsn(Opcodes.ALOAD, 0);
                    method.visitMethodInsn(Opcodes.INVOKESTATIC, HOOKS, "setClipboard", "(Ljava/lang/String;)V", false);
                    method.visitInsn(Opcodes.RETURN);
                }
                method.visitMaxs(1, patch == ClipboardMethod.GET ? 0 : 1);
                method.visitEnd();
                return null;
            }
        };
    }

    private enum ClipboardMethod { GET, SET }
}
