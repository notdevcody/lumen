package pl.tomgirl.lumen.mixin.game;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.tomgirl.lumen.Lumen;
import pl.tomgirl.lumen.window.DisplaySdl;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

@Mixin(Minecraft.class)
public class MixinMinecraft_Icon {
    @Inject(method = "initIcon", at = @At("HEAD"), cancellable = true)
    private void initIcon(CallbackInfo ci) {
        try (InputStream stream = Lumen.class.getResourceAsStream("/icon_256x.png")) {
            if (stream == null) {
                throw new IllegalStateException("Missing default icon");
            }

            BufferedImage image = ImageIO.read(stream);
            if (image == null) {
                throw new IllegalStateException("Unable to decode default icon");
            }

            ByteBuffer pixels = ByteBuffer.allocate(image.getWidth() * image.getHeight() * 4);
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int argb = image.getRGB(x, y);
                    pixels.put((byte) (argb >> 16));
                    pixels.put((byte) (argb >> 8));
                    pixels.put((byte) argb);
                    pixels.put((byte) (argb >> 24));
                }
            }
            pixels.flip();
            DisplaySdl.instance().setIcon(new ByteBuffer[] { pixels });
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load default icon", ex);
        }
        ci.cancel();
    }
}
