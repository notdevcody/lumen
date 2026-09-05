package pl.tomgirl.lenis.bakery.patch;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL20;

@SuppressWarnings("unused")
public abstract class GL20Patch {
    @CompatStub
    private static void glShaderSource(int shader, java.nio.ByteBuffer string) {
        byte[] data = new byte[string.limit()];
        string.position(0);
        string.get(data);
        string.position(0);
        GL20.glShaderSource(shader, new String(data));
    }

    @CompatStub("glUniformMatrix4fv") private static void glUniformMatrix4(int location, boolean transpose, FloatBuffer value) {}
    @CompatStub("glUniform2fv") private static void glUniform2(int location, FloatBuffer value) {}
    @CompatStub("glUniform1iv") private static void glUniform1(int location, IntBuffer value) {}
    @CompatStub("glUniform1fv") private static void glUniform1(int location, FloatBuffer value) {}
    @CompatStub("glUniform2iv") private static void glUniform2i(int location, IntBuffer value) {}
    @CompatStub("glUniform3iv") private static void glUniform3(int location, IntBuffer value) {}
    @CompatStub("glUniform3fv") private static void glUniform3(int location, FloatBuffer value) {}
    @CompatStub("glUniform4iv") private static void glUniform4(int location, IntBuffer value) {}
    @CompatStub("glUniform4fv") private static void glUniform4(int location, FloatBuffer value) {}
    @CompatStub("glUniformMatrix2fv") private static void glUniformMatrix2(int location, boolean transpose, FloatBuffer value) {}
    @CompatStub("glUniformMatrix3fv") private static void glUniformMatrix3(int location, boolean transpose, FloatBuffer value) {}
}
