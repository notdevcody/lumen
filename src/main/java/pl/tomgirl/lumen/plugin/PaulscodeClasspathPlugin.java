package pl.tomgirl.lumen.plugin;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public final class PaulscodeClasspathPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
        findPaulscodePath().ifPresent(FabricLauncherBase.getLauncher()::addToClassPath);
    }

    private static Optional<Path> findPaulscodePath() {
        return FabricLauncherBase.getLauncher().getClassPath().stream()
            .filter(path -> path.toString().contains("librarylwjglopenal"))
            .findFirst();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        CompatibilityStubBuilder.build(targetClass);
    }
}
