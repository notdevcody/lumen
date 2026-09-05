package org.lwjgl;


@SuppressWarnings("unused")
public class LWJGLUtil {
    public static final boolean DEBUG = Boolean.getBoolean("org.lwjgl.util.Debug");
    private static final System.Logger LOG = System.getLogger("LWJGL");

    private LWJGLUtil() {}

    public static void log(CharSequence msg) {
        if (DEBUG) {
            LOG.log(System.Logger.Level.DEBUG, msg.toString());
        }
    }
}
