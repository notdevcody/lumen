package org.lwjgl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
public class LWJGLUtil {
    public static final boolean DEBUG = Boolean.getBoolean("org.lwjgl.util.Debug");
    private static final Logger LOG = LogManager.getLogger("LWJGL");

    private LWJGLUtil() {}

    public static void log(CharSequence msg) {
        if (DEBUG) {
            LOG.debug(msg);
        }
    }
}
