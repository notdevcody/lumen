package pl.tomgirl.lenis.game;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

import javax.imageio.ImageIO;

import org.lwjgl.sdl.SDLClipboard;

import pl.tomgirl.lenis.Lenis;
import pl.tomgirl.lenis.window.DisplaySdl;

@SuppressWarnings("unused")
public final class GameHooks {
    public static volatile boolean screenPatchAvailable;

    private GameHooks() {}

    public static String getClipboard() {
        return Objects.requireNonNullElse(SDLClipboard.SDL_GetClipboardText(), "");
    }

    public static void setClipboard(String text) {
        SDLClipboard.SDL_SetClipboardText(text);
    }

    public static void setIcon() {
        try (InputStream stream = Lenis.class.getResourceAsStream("/icon_256x.png")) {
            if (stream == null) throw new IllegalStateException("Missing default icon");
            BufferedImage image = ImageIO.read(stream);
            if (image == null) throw new IllegalStateException("Unable to decode default icon");
            ByteBuffer pixels = ByteBuffer.allocate(image.getWidth() * image.getHeight() * 4);
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int argb = image.getRGB(x, y);
                    pixels.put((byte) (argb >> 16)).put((byte) (argb >> 8));
                    pixels.put((byte) argb).put((byte) (argb >> 24));
                }
            }
            DisplaySdl.instance().setIcon(new ByteBuffer[] { pixels.flip() });
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load default icon", ex);
        }
    }

    public static void screenOpened(Object screen) {
        DisplaySdl.instance().setTextInputActive(screen != null);
    }
}
