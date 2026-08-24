package org.lwjgl;

import pl.tomgirl.lumen.Lumen;

@SuppressWarnings("unused")
public class LWJGLUtil {
    public static final boolean DEBUG = Boolean.getBoolean("org.lwjgl.util.Debug");

    private LWJGLUtil() {}

    public static void log(CharSequence msg) {
        if (DEBUG) {
            Lumen.LOG.debug(msg);
        }
    }
}
