package pl.tomgirl.lenis.bakery.patch;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@SuppressWarnings("unused")
public abstract class AL10Patch {
    @CompatStub("alListenerfv")
    private static void alListener(int paramName, FloatBuffer values) {}

    @CompatStub("alSourcefv")
    private static void alSource(int source, int param, FloatBuffer values) {}

    @CompatStub("alSourceStopv")
    private static void alSourceStop(IntBuffer sources) {}
}
