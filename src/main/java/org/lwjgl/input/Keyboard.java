package org.lwjgl.input;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.sdl.SDLKeyboard;
import pl.tomgirl.lenis.window.KeyboardSdl;

@SuppressWarnings("unused")
public class Keyboard {
    public static final int KEYBOARD_SIZE = Optional.ofNullable(SDLKeyboard.SDL_GetKeyboardState()).map(ByteBuffer::limit).orElse(0) + 1;
    public static final int EVENT_SIZE = 4 + 1 + 4 + 8 + 1;

    private static final KeyboardSdl SDL = KeyboardSdl.instance();
    private static final ByteBuffer keyDownBuffer = BufferUtils.createByteBuffer(KEYBOARD_SIZE);

    private static boolean created;
    private static boolean initialized;
    private static boolean repeatEnabled;

    private static int eventKey;
    private static boolean eventKeyState;
    private static int eventCharacter;
    private static long eventNanos;
    private static boolean eventRepeat;

    private Keyboard() {}

    private static void initialize() {
        if (initialized) {
            return;
        }

        Sys.initialize();
        initialized = true;
    }

    public static void create() throws LWJGLException {
        if (!Display.isCreated()) {
            throw new IllegalStateException("Display must be created");
        }

        if (created) {
            return;
        }

        if (!initialized) {
            initialize();
        }

        SDL.createKeyboard();
        created = true;
        reset();
    }

    private static void reset() {
        for (int i = 0; i < keyDownBuffer.remaining(); i++) {
            keyDownBuffer.put(i, (byte) 0);
        }
        eventKey = eventCharacter = 0;
        eventKeyState = eventRepeat = false;
    }

    public static boolean isCreated() {
        return created;
    }

    public static void destroy() {
        if (!created) {
            return;
        }

        created = false;
        SDL.destroyKeyboard();
        reset();
    }

    public static void poll() {
        if (!created) {
            throw new IllegalStateException("Keyboard must be created before polling");
        }

        SDL.pollKeyboard(keyDownBuffer);
    }

    public static boolean isKeyDown(int key) {
        if (!created) {
            throw new IllegalStateException("Keyboard must be created before querying key state");
        }

        return keyDownBuffer.get(key) != 0;
    }

    public static synchronized String getKeyName(int key) {
        return keyNames[key];
    }

    public static synchronized int getKeyIndex(String keyName) {
        return keyMap.getOrDefault(keyName, KEY_NONE);
    }

    public static int getNumKeyboardEvents() {
        if (!created) {
            throw new IllegalStateException("Keyboard must be created before you reading events");
        }

        return SDL.getNumKeyboardEvents(repeatEnabled);
    }

    public static boolean next() {
        if (!created) {
            throw new IllegalStateException("Keyboard must be created before you can read events");
        }

        boolean result;
        while ((result = readNext()) && eventRepeat && !repeatEnabled) {}
        return result;
    }

    public static void enableRepeatEvents(boolean enable) {
        repeatEnabled = enable;
    }

    public static boolean areRepeatEventsEnabled() {
        return repeatEnabled;
    }

    private static boolean readNext() {
        KeyboardSdl.Event event = SDL.nextKeyboardEvent();
        if (event == null) {
            return false;
        }

        eventKey = event.keycode;
        eventKeyState = event.state != 0;
        eventCharacter = event.character;
        eventNanos = event.nanos;
        eventRepeat = event.repeat;
        return true;
    }

    public static int getKeyCount() {
        return keyMap.size();
    }

    public static char getEventCharacter() {
        return (char) eventCharacter;
    }

    public static int getEventKey() {
        return eventKey;
    }

    public static boolean getEventKeyState() {
        return eventKeyState;
    }

    public static long getEventNanoseconds() {
        return eventNanos;
    }

    public static boolean isRepeatEvent() {
        return eventRepeat;
    }

    private static final String[] keyNames = new String[KEYBOARD_SIZE];
    private static final Map<String, Integer> keyMap = new HashMap<>(253);

    private static int register(String name, int lwjglCode) {
        keyNames[lwjglCode] = name;
        keyMap.put(name, lwjglCode);
        return lwjglCode;
    }

    public static final int CHAR_NONE = '\0',
        KEY_NONE = register("NONE", 0x00),
        KEY_ESCAPE = register("ESCAPE", 0x01),
        KEY_1 = register("1", 0x02),
        KEY_2 = register("2", 0x03),
        KEY_3 = register("3", 0x04),
        KEY_4 = register("4", 0x05),
        KEY_5 = register("5", 0x06),
        KEY_6 = register("6", 0x07),
        KEY_7 = register("7", 0x08),
        KEY_8 = register("8", 0x09),
        KEY_9 = register("9", 0x0A),
        KEY_0 = register("0", 0x0B),
        KEY_MINUS = register("MINUS", 0x0C),
        KEY_EQUALS = register("EQUALS", 0x0D),
        KEY_BACK = register("BACK", 0x0E),
        KEY_TAB = register("TAB", 0x0F),
        KEY_Q = register("Q", 0x10),
        KEY_W = register("W", 0x11),
        KEY_E = register("E", 0x12),
        KEY_R = register("R", 0x13),
        KEY_T = register("T", 0x14),
        KEY_Y = register("Y", 0x15),
        KEY_U = register("U", 0x16),
        KEY_I = register("I", 0x17),
        KEY_O = register("O", 0x18),
        KEY_P = register("P", 0x19),
        KEY_LBRACKET = register("LBRACKET", 0x1A),
        KEY_RBRACKET = register("RBRACKET", 0x1B),
        KEY_RETURN = register("RETURN", 0x1C),
        KEY_PRINT_SCREEN = register("PRINT_SCREEN", 0xB7),
        KEY_LCONTROL = register("LCONTROL", 0x1D),
        KEY_A = register("A", 0x1E),
        KEY_S = register("S", 0x1F),
        KEY_D = register("D", 0x20),
        KEY_F = register("F", 0x21),
        KEY_G = register("G", 0x22),
        KEY_H = register("H", 0x23),
        KEY_J = register("J", 0x24),
        KEY_K = register("K", 0x25),
        KEY_L = register("L", 0x26),
        KEY_SEMICOLON = register("SEMICOLON", 0x27),
        KEY_APOSTROPHE = register("APOSTROPHE", 0x28),
        KEY_GRAVE = register("GRAVE", 0x29),
        KEY_LSHIFT = register("LSHIFT", 0x2A),
        KEY_BACKSLASH = register("BACKSLASH", 0x2B),
        KEY_Z = register("Z", 0x2C),
        KEY_X = register("X", 0x2D),
        KEY_C = register("C", 0x2E),
        KEY_V = register("V", 0x2F),
        KEY_B = register("B", 0x30),
        KEY_N = register("N", 0x31),
        KEY_M = register("M", 0x32),
        KEY_COMMA = register("COMMA", 0x33),
        KEY_PERIOD = register("PERIOD", 0x34),
        KEY_SLASH = register("SLASH", 0x35),
        KEY_RSHIFT = register("RSHIFT", 0x36),
        KEY_MULTIPLY = register("MULTIPLY", 0x37),
        KEY_LMENU = register("LMENU", 0x38),
        KEY_SPACE = register("SPACE", 0x39),
        KEY_CAPITAL = register("CAPITAL", 0x3A),
        KEY_F1 = register("F1", 0x3B),
        KEY_F2 = register("F2", 0x3C),
        KEY_F3 = register("F3", 0x3D),
        KEY_F4 = register("F4", 0x3E),
        KEY_F5 = register("F5", 0x3F),
        KEY_F6 = register("F6", 0x40),
        KEY_F7 = register("F7", 0x41),
        KEY_F8 = register("F8", 0x42),
        KEY_F9 = register("F9", 0x43),
        KEY_F10 = register("F10", 0x44),
        KEY_NUMLOCK = register("NUMLOCK", 0x45),
        KEY_SCROLL = register("SCROLL", 0x46),
        KEY_NUMPAD7 = register("NUMPAD7", 0x47),
        KEY_NUMPAD8 = register("NUMPAD8", 0x48),
        KEY_NUMPAD9 = register("NUMPAD9", 0x49),
        KEY_SUBTRACT = register("SUBTRACT", 0x4A),
        KEY_NUMPAD4 = register("NUMPAD4", 0x4B),
        KEY_NUMPAD5 = register("NUMPAD5", 0x4C),
        KEY_NUMPAD6 = register("NUMPAD6", 0x4D),
        KEY_ADD = register("ADD", 0x4E),
        KEY_NUMPAD1 = register("NUMPAD1", 0x4F),
        KEY_NUMPAD2 = register("NUMPAD2", 0x50),
        KEY_NUMPAD3 = register("NUMPAD3", 0x51),
        KEY_NUMPAD0 = register("NUMPAD0", 0x52),
        KEY_DECIMAL = register("DECIMAL", 0x53),
        KEY_F11 = register("F11", 0x57),
        KEY_F12 = register("F12", 0x58),
        KEY_F13 = register("F13", 0x64),
        KEY_F14 = register("F14", 0x65),
        KEY_F15 = register("F15", 0x66),
        KEY_F16 = register("F16", 0x67),
        KEY_F17 = register("F17", 0x68),
        KEY_F18 = register("F18", 0x69),
        KEY_KANA = register("KANA", 0x70),
        KEY_F19 = register("F19", 0x71),
        KEY_CONVERT = register("CONVERT", 0x79),
        KEY_NOCONVERT = register("NOCONVERT", 0x7B),
        KEY_YEN = register("YEN", 0x7D),
        KEY_NUMPADEQUALS = register("NUMPADEQUALS", 0x8D),
        KEY_CIRCUMFLEX = register("CIRCUMFLEX", 0x90),
        KEY_AT = register("AT", 0x91),
        KEY_COLON = register("COLON", 0x92),
        KEY_UNDERLINE = register("UNDERLINE", 0x93),
        KEY_KANJI = register("KANJI", 0x94),
        KEY_STOP = register("STOP", 0x95),
        KEY_AX = register("AX", 0x96),
        KEY_NUMPADENTER = register("NUMPADENTER", 0x9C),
        KEY_RCONTROL = register("RCONTROL", 0x9D),
        KEY_WORLD_1 = register("WORLD_1", 0xA1),
        KEY_WORLD_2 = register("WORLD_2", 0xA2),
        KEY_SECTION = register("SECTION", 0xA7),
        KEY_NUMPADCOMMA = register("NUMPADCOMMA", 0xB3),
        KEY_DIVIDE = register("DIVIDE", 0xB5),
        KEY_SYSRQ = register("SYSRQ", 0xB7),
        KEY_RMENU = register("RMENU", 0xB8),
        KEY_FUNCTION = register("FUNCTION", 0xC4),
        KEY_PAUSE = register("PAUSE", 0xC5),
        KEY_HOME = register("HOME", 0xC7),
        KEY_UP = register("UP", 0xC8),
        KEY_PRIOR = register("PRIOR", 0xC9),
        KEY_LEFT = register("LEFT", 0xCB),
        KEY_RIGHT = register("RIGHT", 0xCD),
        KEY_END = register("END", 0xCF),
        KEY_DOWN = register("DOWN", 0xD0),
        KEY_NEXT = register("NEXT", 0xD1),
        KEY_INSERT = register("INSERT", 0xD2),
        KEY_DELETE = register("DELETE", 0xD3),
        KEY_CLEAR = register("CLEAR", 0xDA),
        KEY_LMETA = register("LMETA", 0xDB),
        KEY_RMETA = register("RMETA", 0xDC),
        KEY_APPS = register("APPS", 0xDD),
        KEY_POWER = register("POWER", 0xDE),
        KEY_SLEEP = register("SLEEP", 0xDF),
        KEY_F20 = register("F20", 0x135),
        KEY_F21 = register("F21", 0x136),
        KEY_F22 = register("F22", 0x137),
        KEY_F23 = register("F23", 0x138),
        KEY_F24 = register("F24", 0x139),
        KEY_F25 = register("F25", 0x13A);

    @Deprecated
    public static final int KEY_LWIN = KEY_LMETA, KEY_RWIN = KEY_RMETA;
}
