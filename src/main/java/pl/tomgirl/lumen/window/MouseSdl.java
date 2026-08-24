package pl.tomgirl.lumen.window;

import java.util.Arrays;

import org.lwjgl.sdl.SDL_Event;
import org.lwjgl.sdl.SDL_MouseButtonEvent;
import org.lwjgl.sdl.SDL_MouseMotionEvent;
import org.lwjgl.sdl.SDL_MouseWheelEvent;

import static org.lwjgl.sdl.SDLEvents.*;
import static org.lwjgl.sdl.SDLMouse.*;

public class MouseSdl {
    private static final int EVENT_QUEUE_SIZE = 100;
    private static final int BUTTON_COUNT = Integer.SIZE;

    private static final MouseSdl INSTANCE = new MouseSdl();
    private final SDL_MouseButtonEvent buttonEvent = DisplaySdl.instance().getEvent().button();
    private final SDL_MouseMotionEvent motionEvent = DisplaySdl.instance().getEvent().motion();
    private final SDL_MouseWheelEvent wheelEvent = DisplaySdl.instance().getEvent().wheel();
    private final BoundedQueue<Event> events = new BoundedQueue<>(EVENT_QUEUE_SIZE, Event::new);

    private long handle;
    private boolean grabbed;
    private boolean insideWindow;
    private double lastX;
    private double lastY;
    private double accumDx;
    private double accumDy;
    private double accumDz;
    private final boolean[] buttonStates = new boolean[getButtonCount()];

    public void createMouse() {
        handle = DisplaySdl.instance().getHandle();
        clearState();
    }

    private void putMouseEvent(byte button, boolean state, int deltaWheel, long nanos) {
        if (grabbed) {
            putMouseEvent(button, state, 0, 0, deltaWheel, nanos);
        } else {
            putMouseEvent(button, state, lastX, lastY, deltaWheel, nanos);
        }
    }

    private void putMouseEvent(byte button, boolean state, double x, double y, int deltaWheel, long nanos) {
        Event event = events.claim();
        if (event != null) {
            event.set(button, state, x, y, deltaWheel, nanos);
        }
    }

    private void putMouseMotionEvent(double x, double y, long nanos) {
        Event last = events.peekLast();
        if (last != null && last.button == -1) {
            if (grabbed) {
                last.x += x;
                last.y += y;
            } else {
                last.x = x;
                last.y = y;
            }

            last.nanos = nanos;
            return;
        }

        putMouseEvent((byte) -1, false, x, y, 0, nanos);
    }

    public void destroyMouse() {
        clearState();
        handle = 0;
    }

    private void reset() {
        events.clear();
        accumDx = accumDy = 0;
    }

    private void clearState() {
        events.clear();
        Arrays.fill(buttonStates, false);
        accumDx = accumDy = accumDz = 0;
        lastX = lastY = 0;
        insideWindow = false;
    }

    void releaseAll(long nanos) {
        for (int button = 0; button < buttonStates.length; button++) {
            if (buttonStates[button]) {
                buttonStates[button] = false;
                putMouseEvent((byte) button, false, 0, nanos);
            }
        }
        insideWindow = false;
    }

    public void pollMouse(PollState state) {
        if (grabbed) {
            state.dx += accumDx;
            state.dy += accumDy;
            state.x += accumDx;
            state.y += accumDy;
        } else {
            state.dx = accumDx;
            state.dy = accumDy;
            state.x = lastX;
            state.y = lastY;
        }
        state.wheelDelta += accumDz;
        accumDx = accumDy = accumDz = 0;
        System.arraycopy(buttonStates, 0, state.buttons, 0, buttonStates.length);
    }

    public Event nextMouseEvent() {
        return events.poll();
    }

    public void setCursorPosition(double x, double y) {
        lastX = x;
        lastY = y;
        accumDx = accumDy = 0;
        SDL_WarpMouseInWindow(handle, (float) x, toSdlY(y));
    }

    public void grabMouse(boolean grab) {
        if (!grab) {
            SDL_WarpMouseInWindow(handle, (float) lastX, toSdlY(lastY));
        }
        SDL_SetWindowRelativeMouseMode(handle, grab);
        grabbed = grab;
        reset();
    }

    public boolean hasWheel() {
        return true;
    }

    public int getButtonCount() {
        return BUTTON_COUNT;
    }

    public boolean isInsideWindow() {
        return insideWindow;
    }

    public void processMouseEvent(SDL_Event event) {
        switch (event.type()) {
            case SDL_EVENT_MOUSE_BUTTON_UP, SDL_EVENT_MOUSE_BUTTON_DOWN -> {
                boolean down = buttonEvent.down();
                long timestamp = buttonEvent.timestamp();
                byte button = switch (buttonEvent.button()) {
                    case SDL_BUTTON_RIGHT -> 1;
                    case SDL_BUTTON_MIDDLE -> 2;
                    default -> (byte) (buttonEvent.button() - 1);
                };

                if (grabbed) {
                    putMouseEvent(button, down, 0, timestamp);
                } else {
                    putMouseEvent(
                        button,
                            down,
                        buttonEvent.x(),
                        toLwjglY(buttonEvent.y()),
                        0,
                            timestamp
                    );
                }
                if (button >= 0 && button < buttonStates.length) {
                    buttonStates[button] = down;
                }
            }
            case SDL_EVENT_MOUSE_WHEEL -> {
                int yOffset = wheelEvent.integer_y();
                if (yOffset == 0) {
                    break;
                }
                accumDz += yOffset;
                if (grabbed) {
                    putMouseEvent((byte) -1, false, yOffset, wheelEvent.timestamp());
                } else {
                    putMouseEvent(
                        (byte) -1,
                        false,
                        wheelEvent.mouse_x(),
                        toLwjglY(wheelEvent.mouse_y()),
                        yOffset,
                        wheelEvent.timestamp()
                    );
                }
            }
            case SDL_EVENT_MOUSE_MOTION -> {
                int x = (int) (motionEvent.x());
                int y = (int) toLwjglY(motionEvent.y());
                double dx = motionEvent.xrel();
                double dy = -motionEvent.yrel();
                if (dx != 0 || dy != 0) {
                    accumDx += dx;
                    accumDy += dy;
                    lastX = x;
                    lastY = y;
                    long nanos = motionEvent.timestamp();
                    if (grabbed) {
                        putMouseMotionEvent(dx, dy, nanos);
                    } else {
                        putMouseMotionEvent(x, y, nanos);
                    }
                }
            }
            case SDL_EVENT_WINDOW_MOUSE_ENTER -> insideWindow = true;
            case SDL_EVENT_WINDOW_MOUSE_LEAVE -> insideWindow = false;
        }
    }

    private float toSdlY(double lwjglY) {
        return (float) (DisplaySdl.instance().getHeight() - 1 - lwjglY);
    }

    private double toLwjglY(double sdlY) {
        return DisplaySdl.instance().getHeight() - 1 - sdlY;
    }

    public static MouseSdl instance() {
        return INSTANCE;
    }

    public static final class PollState {
        public double x;
        public double y;
        public double dx;
        public double dy;
        public double wheelDelta;
        public final boolean[] buttons;

        public PollState() {
            buttons = new boolean[BUTTON_COUNT];
        }
    }

    public static final class Event {
        public byte button;
        public boolean state;
        public double x;
        public double y;
        public double wheelDelta;
        public long nanos;

        private Event() {}

        private void set(byte button, boolean state, double x, double y, double wheelDelta, long nanos) {
            this.button = button;
            this.state = state;
            this.x = x;
            this.y = y;
            this.wheelDelta = wheelDelta;
            this.nanos = nanos;
        }
    }
}
