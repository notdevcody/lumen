package org.lwjgl.util.glu;

import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

@SuppressWarnings("unused")
public class Util {
    protected static int ceil(int a, int b) {
        return a % b == 0 ? a / b : a / b + 1;
    }

    protected static float[] normalize(float[] v) {
        float r = (float)Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        if (r == 0.0) {
            return v;
        }

        r = 1.0F / r;
        v[0] *= r;
        v[1] *= r;
        v[2] *= r;
        return v;
    }

    protected static void cross(float[] v1, float[] v2, float[] result) {
        result[0] = v1[1] * v2[2] - v1[2] * v2[1];
        result[1] = v1[2] * v2[0] - v1[0] * v2[2];
        result[2] = v1[0] * v2[1] - v1[1] * v2[0];
    }

    protected static int compPerPix(int format) {
        return switch (format) {
            case GL_COLOR_INDEX, GL_STENCIL_INDEX, GL_DEPTH_COMPONENT, GL_RED, GL_GREEN, GL_BLUE, GL_ALPHA, GL_LUMINANCE -> 1;
            case GL_LUMINANCE_ALPHA -> 2;
            case GL_RGB, GL_BGR -> 3;
            case GL_RGBA, GL_BGRA -> 4;
            default -> -1;
        };
    }

    protected static int nearestPower(int value) {
        if (value == 0) {
            return -1;
        }

        int i = 1;
        for (;;) {
            if (value == 1) {
                return i;
            } else if (value == 3) {
                return i << 2;
            }
            value >>= 1;
            i <<= 1;
        }
    }

    protected static int bytesPerPixel(int format, int type) {
        int n = switch (format) {
            case GL_COLOR_INDEX, GL_STENCIL_INDEX, GL_DEPTH_COMPONENT, GL_RED, GL_GREEN, GL_BLUE, GL_ALPHA, GL_LUMINANCE -> 1;
            case GL_LUMINANCE_ALPHA -> 2;
            case GL_RGB, GL_BGR -> 3;
            case GL_RGBA, GL_BGRA -> 4;
            default -> 0;
        };

        int m = switch (type) {
            case GL_UNSIGNED_BYTE, GL_BITMAP, GL_BYTE -> 1;
            case GL_UNSIGNED_SHORT, GL_SHORT -> 2;
            case GL_UNSIGNED_INT, GL_FLOAT, GL_INT -> 4;
            default -> 0;
        };

        return n * m;
    }

    protected static int glGetIntegerv(int what) {
        return GL11.glGetInteger(what);
    }
}
