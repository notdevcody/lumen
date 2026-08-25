package pl.tomgirl.lenis.mixin;

import pl.tomgirl.lenis.plugin.CompatStub;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings("unused")
@Mixin(value = GL11.class, remap = false)
public abstract class GL11Mixin {
    @Unique @CompatStub
    private static void glDrawElements(int mode, int count, int type, ByteBuffer indices) {
        GL11.nglDrawElements(mode, count, type, MemoryUtil.memAddress(indices));
    }

    @Unique @CompatStub
    private static void glTexCoordPointer(int i, int stride, FloatBuffer pointer) {
        GL11.glTexCoordPointer(i, 0x1406, stride, pointer);
    }

    @Unique @CompatStub
    private static void glTexCoordPointer(int i, int stride, ShortBuffer pointer) {
        GL11.glTexCoordPointer(i, 0x1402, stride, pointer);
    }

    @Unique @CompatStub
    private static void glColorPointer(int i, boolean bl, int i2, ByteBuffer pointer) {
        GL11.glColorPointer(i, 0x1401, i2, pointer);
    }

    @Unique @CompatStub
    private static void glVertexPointer(int i, int i2, FloatBuffer pointer) {
        GL11.glVertexPointer(i, 0x1406, i2, pointer);
    }

    @Unique @CompatStub
    private static void glNormalPointer(int stride, ByteBuffer pointer) {
        GL11.glNormalPointer(0x1400, stride, pointer);
    }

    @Unique @CompatStub("glGetFloatv") private static void glGetFloat(int pname, FloatBuffer params) {}
    @Unique @CompatStub("glGetIntegerv") private static void glGetInteger(int pname, IntBuffer params) {}
    @Unique @CompatStub("glFogfv") private static void glFog(int pname, FloatBuffer params) {}
    @Unique @CompatStub("glLightfv") private static void glLight(int light, int pname, FloatBuffer params) {}
    @Unique @CompatStub("glLightModelfv") private static void glLightModel(int pname, FloatBuffer params) {}
    @Unique @CompatStub("glMultMatrixf") private static void glMultMatrix(FloatBuffer matrix) {}
    @Unique @CompatStub("glTexEnvfv") private static void glTexEnv(int target, int pname, FloatBuffer params) {}
    @Unique @CompatStub("glTexGenfv") private static void glTexGen(int coord, int pname, FloatBuffer params) {}
    @Unique @CompatStub("glLoadMatrixf") private static void glLoadMatrix(FloatBuffer matrix) {}
}
