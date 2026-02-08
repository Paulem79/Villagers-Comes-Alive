rootProject.name = "Villagers-Comes-Alive"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    plugins {
        id("com.gradleup.shadow") version "9.3.1"
    }
}

plugins {
    // add toolchain resolver
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}