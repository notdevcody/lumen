package pl.tomgirl.lenis.game.patch.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import pl.tomgirl.lenis.game.GameHooks;
import pl.tomgirl.lenis.game.patch.Patch;

public final class MinecraftPatch extends Patch {
    private static final String DISPLAY = "org/lwjgl/opengl/Display";
    private final List<Method> screenCandidates = new ArrayList<>();
    private String owner;
    private Method openScreen;
    private boolean displayCreate;
    private boolean displayTitle;

    public MinecraftPatch(ClassVisitor next) { super(next); }

    @Override
    public void visit(int version, int access, String name, String signature, String parent, String[] interfaces) {
        owner = name;
        super.visit(version, access, name, signature, parent, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor next = super.visitMethod(access, name, descriptor, signature, exceptions);
        Type[] arguments = Type.getArgumentTypes(descriptor);
        boolean candidate = (access & Opcodes.ACC_STATIC) == 0
            && Type.getReturnType(descriptor).equals(Type.VOID_TYPE)
            && arguments.length == 1 && arguments[0].getSort() == Type.OBJECT;

        return new MethodVisitor(Opcodes.ASM9, next) {
            private final Set<String> reads = new HashSet<>();
            private final Set<String> writes = new HashSet<>();

            @Override
            public void visitFieldInsn(int opcode, String fieldOwner, String field, String fieldDescriptor) {
                if (candidate && fieldOwner.equals(owner) && fieldDescriptor.equals(arguments[0].getDescriptor())) {
                    if (opcode == Opcodes.GETFIELD) reads.add(field);
                    if (opcode == Opcodes.PUTFIELD) writes.add(field);
                }
                super.visitFieldInsn(opcode, fieldOwner, field, fieldDescriptor);
            }

            @Override
            public void visitMethodInsn(int opcode, String calledOwner, String called, String calledDescriptor, boolean itf) {
                if (calledOwner.equals(DISPLAY)) {
                    displayCreate |= called.equals("create") && calledDescriptor.endsWith(")V");
                    displayTitle |= called.equals("setTitle") && calledDescriptor.equals("(Ljava/lang/String;)V");
                }
                super.visitMethodInsn(opcode, calledOwner, called, calledDescriptor, itf);
            }

            @Override
            public void visitEnd() {
                if (writes.stream().anyMatch(reads::contains)) {
                    screenCandidates.add(new Method(name, descriptor));
                }
                super.visitEnd();
            }
        };
    }

    @Override
    public boolean matches() {
        if (!displayCreate || !displayTitle) {
            return false;
        }

        if (screenCandidates.size() == 1) {
            openScreen = screenCandidates.getFirst();
        }

        return true;
    }

    @Override
    public void applied() {
        if (openScreen != null) {
            GameHooks.screenPatchAvailable = true;
        }
    }

    @Override
    public ClassVisitor apply(ClassVisitor writer) {
        return new ClassVisitor(Opcodes.ASM9, writer) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor method = super.visitMethod(access, name, descriptor, signature, exceptions);
                boolean patchesScreen = new Method(name, descriptor).equals(openScreen);
                return new MethodVisitor(Opcodes.ASM9, method) {
                    @Override
                    public void visitMethodInsn(int opcode, String calledOwner, String called, String calledDescriptor, boolean itf) {
                        super.visitMethodInsn(opcode, calledOwner, called, calledDescriptor, itf);
                        if (calledOwner.equals(DISPLAY) && called.equals("create") && calledDescriptor.endsWith(")V")) {
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, HOOKS, "setIcon", "()V", false);
                        }
                    }

                    @Override
                    public void visitInsn(int opcode) {
                        if (patchesScreen && opcode == Opcodes.RETURN) {
                            super.visitVarInsn(Opcodes.ALOAD, 1);
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, HOOKS, "screenOpened", "(Ljava/lang/Object;)V", false);
                        }
                        super.visitInsn(opcode);
                    }

                    @Override
                    public void visitMaxs(int maxStack, int maxLocals) {
                        super.visitMaxs(Math.max(maxStack, 1), maxLocals);
                    }
                };
            }
        };
    }
}
