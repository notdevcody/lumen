package org.lwjgl.opengl;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.LWJGLException;

@SuppressWarnings("unused")
public class GLContext {
    private static final ThreadLocal<ContextCapabilities> current_capabilities = new ThreadLocal<>();

    public static ContextCapabilities getCapabilities() {
        ContextCapabilities caps = getCapabilitiesImpl();
        if (caps == null) {
            try {
                ContextCapabilities created = new ContextCapabilities(false);
                setCapabilities(created);
                return created;
            } catch (LWJGLException e) {
                throw new RuntimeException("No OpenGL context found", e);
            }
        }

        return caps;
    }

    @Nullable
    private static ContextCapabilities getCapabilitiesImpl() {
        return getThreadLocalCapabilities();
    }

    @Nullable
    private static ContextCapabilities getThreadLocalCapabilities() {
        return current_capabilities.get();
    }

    static void setCapabilities(ContextCapabilities capabilities) {
        current_capabilities.set(capabilities);
    }
}
