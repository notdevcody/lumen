package pl.tomgirl.lenis.mixin;

import pl.tomgirl.lenis.plugin.CompatStub;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.openal.AL10;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings("unused")
@Mixin(value = AL10.class, remap = false)
public abstract class AL10Mixin {
    @Unique @CompatStub("alListenerfv")
    private static void alListener(int paramName, FloatBuffer values) {}

    @Unique @CompatStub("alSourcefv")
    private static void alSource(int source, int param, FloatBuffer values) {}

    @Unique @CompatStub("alSourceStopv")
    private static void alSourceStop(IntBuffer sources) {}
}
