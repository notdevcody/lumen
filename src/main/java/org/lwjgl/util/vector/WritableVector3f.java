package org.lwjgl.util.vector;

@SuppressWarnings("unused")
public interface WritableVector3f extends WritableVector2f {
    void setZ(float z);
    void set(float x, float y, float z);
}
