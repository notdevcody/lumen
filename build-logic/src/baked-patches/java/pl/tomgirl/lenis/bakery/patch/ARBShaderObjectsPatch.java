package pl.tomgirl.lenis.bakery.patch;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@SuppressWarnings("unused")
public abstract class ARBShaderObjectsPatch {
    @CompatStub("glGetObjectParameterivARB")
    private static void glGetObjectParameterARB(int obj, int pname, IntBuffer params) {}

    @CompatStub("glUniformMatrix4fvARB")
    private static void glUniformMatrix4ARB(int location, boolean transpose, FloatBuffer value) {}

    @CompatStub("glUniform1ivARB")
    private static void glUniform1ARB(int location, IntBuffer value) {}
}
