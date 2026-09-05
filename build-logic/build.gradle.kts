plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("com.gradleup.shadow:shadow-gradle-plugin:9.3.0")
    implementation("org.ow2.asm:asm:9.10.1")
    implementation("org.ow2.asm:asm-commons:9.10.1")
}

gradlePlugin {
    plugins {
        create("lenisBuild") {
            id = "lenis.build"
            implementationClass = "pl.tomgirl.lenis.build.LenisBuildPlugin"
        }
    }
}
