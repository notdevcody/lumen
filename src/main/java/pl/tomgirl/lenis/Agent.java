package pl.tomgirl.lenis;

import java.io.File;
import java.lang.instrument.Instrumentation;

public final class Agent {
    private Agent() {}

    public static void premain(String arguments, Instrumentation instrumentation) {
        if (injectFabric()) {
            Lenis.LOG.log(System.Logger.Level.INFO, "Injected Fabric mod");
            return;
        }

        instrumentation.addTransformer(new pl.tomgirl.lenis.game.GameTransformer());
    }

    public static boolean injectFabric() {
        try {
            Class.forName("net.fabricmc.loader.api.FabricLoader");

            File agentFile = new File(Agent.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            String addMods = System.getProperty("fabric.addMods", "");
            if (!"".equals(addMods)) {
                addMods += File.pathSeparator;
            }
            addMods += agentFile.getAbsolutePath();
            System.setProperty("fabric.addMods", addMods);

            return true;
        } catch (Exception e) {
            if (!(e instanceof ClassNotFoundException)) {
                Lenis.LOG.log(System.Logger.Level.ERROR, "Error injecting into Fabric");
            }
            return false;
        }
    }
}
