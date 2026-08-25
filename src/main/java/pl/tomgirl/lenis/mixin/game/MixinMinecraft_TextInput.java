package pl.tomgirl.lenis.mixin.game;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import pl.tomgirl.lenis.window.DisplaySdl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft_TextInput {
    @Inject(method = "openScreen", at = @At("TAIL"))
    private void openScreen(Screen screen, CallbackInfo ci) {
        DisplaySdl.instance().setTextInputActive(screen != null);
    }
}
