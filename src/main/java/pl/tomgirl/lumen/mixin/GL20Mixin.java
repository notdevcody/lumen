package pl.tomgirl.lumen.mixin;

import pl.tomgirl.lumen.plugin.CompatStub;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL20;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings("unused")
@Mixin(value = GL20.class, remap = false)
public abstract class GL20Mixin {
    @Unique @CompatStub
    private static void glShaderSource(int shader, java.nio.ByteBuffer string) {
        byte[] data = new byte[string.limit()];
        string.position(0);
        string.get(data);
        string.position(0);
        GL20.glShaderSource(shader, new String(data));
    }

    @Unique @CompatStub("glUniformMatrix4fv") private static void glUniformMatrix4(int location, boolean transpose, FloatBuffer value) {}
    @Unique @CompatStub("glUniform2fv") private static void glUniform2(int location, FloatBuffer value) {}
    @Unique @CompatStub("glUniform1iv") private static void glUniform1(int location, IntBuffer value) {}
    @Unique @CompatStub("glUniform1fv") private static void glUniform1(int location, FloatBuffer value) {}
    @Unique @CompatStub("glUniform2iv") private static void glUniform2i(int location, IntBuffer value) {}
    @Unique @CompatStub("glUniform3iv") private static void glUniform3(int location, IntBuffer value) {}
    @Unique @CompatStub("glUniform3fv") private static void glUniform3(int location, FloatBuffer value) {}
    @Unique @CompatStub("glUniform4iv") private static void glUniform4(int location, IntBuffer value) {}
    @Unique @CompatStub("glUniform4fv") private static void glUniform4(int location, FloatBuffer value) {}
    @Unique @CompatStub("glUniformMatrix2fv") private static void glUniformMatrix2(int location, boolean transpose, FloatBuffer value) {}
    @Unique @CompatStub("glUniformMatrix3fv") private static void glUniformMatrix3(int location, boolean transpose, FloatBuffer value) {}
}
