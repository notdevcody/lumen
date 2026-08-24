package org.lwjgl.opengl;

import java.io.Serial;

@SuppressWarnings("unused")
public class OpenGLException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public OpenGLException(int gl_error_code) {
        this(createErrorMessage(gl_error_code));
    }

    private static String createErrorMessage(int gl_error_code) {
        String error_string = Util.translateGLErrorString(gl_error_code);
        return error_string + " (" + gl_error_code + ")";
    }

    public OpenGLException() {
        super();
    }

    public OpenGLException(String message) {
        super(message);
    }

    public OpenGLException(String message, Throwable cause) {
        super(message, cause);
    }

    public OpenGLException(Throwable cause) {
        super(cause);
    }
}
