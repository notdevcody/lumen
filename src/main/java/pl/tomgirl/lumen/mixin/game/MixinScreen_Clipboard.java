package pl.tomgirl.lumen.mixin.game;

import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.sdl.SDLClipboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@SuppressWarnings({"unused"})
@Mixin(Screen.class)
public abstract class MixinScreen_Clipboard {
    @Inject(at = @At("HEAD"), method = "getClipboard", cancellable = true)
    private static void getClipboard(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(Objects.requireNonNullElse(SDLClipboard.SDL_GetClipboardText(), ""));
    }

    @Inject(at = @At("HEAD"), method = "setClipboard", cancellable = true)
    private static void setClipboard(String text, CallbackInfo ci) {
        SDLClipboard.SDL_SetClipboardText(text);
        ci.cancel();
    }
}
