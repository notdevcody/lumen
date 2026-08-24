package org.lwjgl.opengl;

import static org.lwjgl.opengl.ARBImaging.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

@SuppressWarnings("unused")
public final class Util {
    private Util() {
    }

    public static void checkGLError() throws OpenGLException {
        if (ContextCapabilities.DEBUG) {
            return;
        }

        int err = glGetError();
        if (err != GL_NO_ERROR) {
            throw new OpenGLException(err);
        }
    }

    public static String translateGLErrorString(int error_code) {
        return switch (error_code) {
            case GL_NO_ERROR -> "No error";
            case GL_INVALID_ENUM -> "Invalid enum";
            case GL_INVALID_VALUE -> "Invalid value";
            case GL_INVALID_OPERATION -> "Invalid operation";
            case GL_STACK_OVERFLOW -> "Stack overflow";
            case GL_STACK_UNDERFLOW -> "Stack underflow";
            case GL_OUT_OF_MEMORY -> "Out of memory";
            case GL_TABLE_TOO_LARGE -> "Table too large";
            case GL_INVALID_FRAMEBUFFER_OPERATION -> "Invalid framebuffer operation";
            default -> null;
        };
    }
}
