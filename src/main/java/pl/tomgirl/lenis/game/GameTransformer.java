package pl.tomgirl.lenis.game;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.stream.Stream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import pl.tomgirl.lenis.Lenis;
import pl.tomgirl.lenis.game.patch.Patch;
import pl.tomgirl.lenis.game.patch.impl.*;

public final class GameTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String name, Class<?> type, ProtectionDomain domain, byte[] bytecode) {
        return transform(bytecode);
    }

    public static byte[] transform(byte[] bytecode) {
        ScreenPatch screen = new ScreenPatch(null);
        MinecraftPatch minecraft = new MinecraftPatch(screen);
        ClassReader reader = new ClassReader(bytecode);
        reader.accept(minecraft, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

        List<Patch> matches = Stream.of(minecraft, screen).filter(Patch::matches).toList();
        if (matches.isEmpty()) return bytecode;

        ClassWriter writer = new ClassWriter(reader, 0);
        ClassVisitor patcher = writer;
        for (Patch patch : matches) {
            patch.applied();
            patcher = patch.apply(patcher);
        }
        reader.accept(patcher, 0);
        Lenis.LOG.log(System.Logger.Level.DEBUG, "Applied {0} to {1}", matches, reader.getClassName());
        return writer.toByteArray();
    }
}
