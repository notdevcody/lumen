package org.lwjgl.util.vector;

import java.io.Serializable;
import java.nio.FloatBuffer;

@SuppressWarnings("unused")
public abstract class Vector implements Serializable, ReadableVector {
    protected Vector() {
        super();
    }

    public abstract float lengthSquared();
    public abstract Vector load(FloatBuffer buf);
    public abstract Vector negate();
    public abstract Vector store(FloatBuffer buf);
    public abstract Vector scale(float scale);

    public final float length() {
        return (float) Math.sqrt(lengthSquared());
    }

    public final Vector normalise() {
        float len = length();
        if (len != 0.0f) {
            float l = 1.0f / len;
            return scale(l);
        } else {
            throw new IllegalStateException("Zero length vector");
        }
    }
}
