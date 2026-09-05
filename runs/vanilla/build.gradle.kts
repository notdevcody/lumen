plugins {
    alias(libs.plugins.loom)
}

val lenis = configurations.create("lenis")

dependencies {
    minecraft("com.mojang:minecraft:1.6.4")
    lenis(project(path = ":", configuration = "distribution"))
    runtimeOnly(project(path = ":", configuration = "distribution"))
}

configurations.configureEach {
    exclude(group = "org.lwjgl.lwjgl")
    exclude(group = "org.lwjgl")
}

loom {
    clientOnlyMinecraftJar()
    runs {
        remove(getByName("server"))
        remove(getByName("client"))
        create("base") {
            client()
            mainClass = "net.minecraft.client.main.Main"
            displayName = "Vanilla 1.6.4 (base)"
            runDirectory = layout.projectDirectory.dir("run/base")
        }
        create("agent") {
            client()
            mainClass = "net.minecraft.client.main.Main"
            displayName = "Vanilla 1.6.4 (agent)"
            runDirectory = layout.projectDirectory.dir("run/agent")
            jvmArguments.add(provider { "-javaagent:${lenis.singleFile.absolutePath}" })
        }
        configureEach {
            generateRunConfig = true
            preferGradleTask = true
            programArguments.addAll("--username", "Player", "--session", "0", "--version", "1.6.4")
            jvmArguments.add("-Djava.util.logging.config.file=${rootProject.file("runs/logging.properties")}")
            jvmArguments.add("--enable-native-access=ALL-UNNAMED")
            if (System.getProperty("os.name").startsWith("Mac")) jvmArguments.add("-XstartOnFirstThread")
        }
    }
}
