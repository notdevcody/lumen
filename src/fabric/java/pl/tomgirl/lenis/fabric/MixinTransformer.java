package pl.tomgirl.lenis.fabric;

import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.transformers.TreeTransformer;
import pl.tomgirl.lenis.game.GameTransformer;

public class MixinTransformer<T extends TreeTransformer & IMixinTransformer> extends MixinTransformerDelegate<T> {
    MixinTransformer(T delegate) throws Exception {
        this.delegate = delegate;
    }

    @Override
    public byte[] transformClassBytes(String name, String transformedName, byte[] bytecode) {
        if (bytecode == null) {
            return super.transformClassBytes(name, transformedName, null);
        }

        if (name.startsWith("pl.tomgirl.lenis")) {
            return super.transformClassBytes(name, transformedName, bytecode);
        }

        byte[] transformed = GameTransformer.transform(bytecode);
        if (transformed != null) {
            bytecode = transformed;
        }

        return super.transformClassBytes(name, transformedName, bytecode);
    }
}