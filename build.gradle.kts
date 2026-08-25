plugins {
    id("java")
    id("maven-publish")
    alias(libs.plugins.loom)
    alias(libs.plugins.ploceus)
}

loom {}
ploceus.setIntermediaryGeneration(2)

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    withSourcesJar()
}

group = providers.gradleProperty("maven_group").get()

base {
    archivesName = providers.gradleProperty("base_name").get()
}

dependencies {
    minecraft(libs.minecraft)
    modImplementation(libs.loader)
    mappings(ploceus.featherMappings(libs.versions.feather.build.get()))

    bundled(libs.lwjgl.asProvider())
    bundled(libs.lwjgl.openal)
    bundled(libs.lwjgl.opengl)
    bundled(libs.lwjgl.sdl)

    lwjglDesktopNatives("lwjgl")
    lwjglDesktopNatives("lwjgl-openal")
    lwjglDesktopNatives("lwjgl-opengl")
    lwjglDesktopNatives("lwjgl-sdl")
}

tasks.processResources {
    filesMatching("fabric.mod.json") {
        expand(Pair("version", project.version))
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = providers.gradleProperty("base_name").get()
            from(components["java"])
        }
    }

    repositories {
        providers.environmentVariable("MAVEN_URL").orNull?.let { repositoryUrl ->
            maven {
                name = "release"
                url = uri(repositoryUrl)
                credentials {
                    username = providers.environmentVariable("MAVEN_USER").orNull
                    password = providers.environmentVariable("MAVEN_PASS").orNull
                }
            }
        }
    }
}

fun DependencyHandlerScope.bundled(dependency: Provider<MinimalExternalModuleDependency>) {
    add("api", dependency)
    add("include", dependency)
}

fun DependencyHandlerScope.lwjglNative(module: String, classifier: String) {
    val notation = "org.lwjgl:$module:${libs.versions.lwjgl.get()}:$classifier"
    add("runtimeOnly", notation)
    add("include", notation)
}

fun DependencyHandlerScope.lwjglDesktopNatives(module: String) {
    lwjglNative(module, "natives-windows")
    lwjglNative(module, "natives-linux")
    lwjglNative(module, "natives-macos")
    lwjglNative(module, "natives-macos-arm64")
}
