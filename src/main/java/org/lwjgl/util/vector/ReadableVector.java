package org.lwjgl.util.vector;

import java.nio.FloatBuffer;

@SuppressWarnings("unused")
public interface ReadableVector {
    float length();
    float lengthSquared();
    Vector store(FloatBuffer buf);
}
