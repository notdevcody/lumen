package pl.tomgirl.lenis.window;

public interface GpuSurface {
    long createWindow(String title, int width, int height, boolean resizable);
    void makeCurrent();
    void releaseCurrent();
    void swapBuffers();
    void setVSyncEnabled(boolean enabled);
    void destroy();
}
