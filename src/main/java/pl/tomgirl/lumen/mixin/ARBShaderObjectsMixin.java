package pl.tomgirl.lumen.mixin;

import pl.tomgirl.lumen.plugin.CompatStub;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.ARBShaderObjects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings("unused")
@Mixin(value = ARBShaderObjects.class, remap = false)
public abstract class ARBShaderObjectsMixin {
    @Unique @CompatStub("glGetObjectParameterivARB")
    private static void glGetObjectParameterARB(int obj, int pname, IntBuffer params) {}

    @Unique @CompatStub("glUniformMatrix4fvARB")
    private static void glUniformMatrix4ARB(int location, boolean transpose, FloatBuffer value) {}

    @Unique @CompatStub("glUniform1ivARB")
    private static void glUniform1ARB(int location, IntBuffer value) {}
}
