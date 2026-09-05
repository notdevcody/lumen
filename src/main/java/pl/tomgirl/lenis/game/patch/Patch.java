package pl.tomgirl.lenis.game.patch;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import pl.tomgirl.lenis.game.GameHooks;

public abstract class Patch extends ClassVisitor {
    public static final String HOOKS = Type.getInternalName(GameHooks.class);

    public Patch(ClassVisitor next) { super(Opcodes.ASM9, next); }

    public abstract boolean matches();
    public abstract ClassVisitor apply(ClassVisitor writer);
    public void applied() {}

    @Override
    public final String toString() { return getClass().getSimpleName(); }

    public record Method(String name, String descriptor) {}
}
