plugins {
    alias(libs.plugins.loom)
}

loom {
    clientOnlyMinecraftJar()
    runs {
        remove(getByName("server"))
        named("client") {
            displayName = "Fabric 1.8.9"
            generateRunConfig = true
            preferGradleTask = true
            programArguments.addAll("--username", "Player", "--accessToken", "0")
            jvmArguments.add("-Djava.util.logging.config.file=${rootProject.file("runs/logging.properties")}")
            jvmArguments.add("--enable-native-access=ALL-UNNAMED")
            if (System.getProperty("os.name").startsWith("Mac")) jvmArguments.add("-XstartOnFirstThread")
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:1.8.9")
    implementation(libs.fabric.loader)
    runtimeOnly("org.apache.logging.log4j:log4j-api:2.26.0")
    runtimeOnly("org.apache.logging.log4j:log4j-core:2.26.0")
    runtimeOnly(project(path = ":", configuration = "distribution"))
}

configurations.configureEach {
    exclude(group = "org.lwjgl.lwjgl")
    exclude(group = "org.lwjgl")
}
