package org.lwjgl;

import java.io.Serial;

@SuppressWarnings("unused")
public class LWJGLException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public LWJGLException() {
        super();
    }

    public LWJGLException(String msg) {
        super(msg);
    }

    public LWJGLException(String message, Throwable cause) {
        super(message, cause);
    }

    public LWJGLException(Throwable cause) {
        super(cause);
    }
}
