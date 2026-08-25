package pl.tomgirl.lenis.mixin.game;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft_Resize {
    @Shadow public int width;
    @Shadow public int height;
    @Shadow protected abstract void resize(int width, int height);

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        this.width = Math.max(1, Display.getWidth());
        this.height = Math.max(1, Display.getHeight());
        this.resize(this.width, this.height);
    }
}
