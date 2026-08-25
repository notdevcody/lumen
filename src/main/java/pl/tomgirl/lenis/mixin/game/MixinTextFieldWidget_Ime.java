package pl.tomgirl.lenis.mixin.game;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.TextRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.tomgirl.lenis.window.DisplaySdl;

@Mixin(TextFieldWidget.class)
public abstract class MixinTextFieldWidget_Ime {
    @Shadow public int x;
    @Shadow public int y;
    @Shadow @Final private int height;
    @Shadow @Final private TextRenderer textRenderer;
    @Shadow private String text;
    @Shadow private boolean hasBorder;
    @Shadow private boolean focused;
    @Shadow private boolean editable;
    @Shadow private boolean visible;
    @Shadow private int firstCharacterIndex;
    @Shadow private int selectionStart;
    @Shadow public abstract int getInnerWidth();

    @Inject(method = "render", at = @At("HEAD"))
    private void updateTextInputArea(CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!focused || !editable || !visible || minecraft.screen == null) {
            return;
        }

        int lineX = hasBorder ? x + 4 : x;
        int lineY = hasBorder ? y + (height - 8) / 2 : y;
        int cursor = textRenderer.getWidth(text.substring(firstCharacterIndex, Math.max(firstCharacterIndex, selectionStart)));
        DisplaySdl.instance().setTextInputArea(
            lineX, lineY, getInnerWidth(), textRenderer.fontHeight,
            Math.min(cursor, getInnerWidth()), minecraft.screen.width, minecraft.screen.height
        );
    }
}
