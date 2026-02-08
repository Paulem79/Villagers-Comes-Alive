plugins {
    `java-library`
    kotlin("jvm") version "2.+"

    id("idea")
    id("com.gradleup.shadow")

    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"

    id("xyz.jpenilla.run-paper") version "3.0.2"
}

group = "net.paulem.vca"
version = "0.1.0"

val targetJavaVersion = 21

repositories {
    mavenCentral()
    maven {
        name = "spigotmc-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven { url = uri("https://jitpack.io") }
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "paulem-releases"
        url = uri("https://maven.paulem.net/releases/")
    }
    maven {
        name = "radRepoPublic"
        url = uri("https://maven.rad.vg/public")
    }
    maven("https://maven.mcbrawls.net/releases/")
    maven("https://repo.viaversion.com")
    maven {
        url = uri("https://libraries.minecraft.net/")
    }
}

artifacts.archives(tasks.shadowJar)
tasks.shadowJar {
    archiveClassifier.set("")
    exclude("META-INF/**")

    relocate("com.github.Anon8281.universalScheduler", "net.paulem.vca.libs.universalScheduler")
    relocate("com.jeff_media.customblockdata", "net.paulem.vca.libs.customblockdata")
    relocate("com.github.benmanes.caffeine", "net.paulem.vca.libs.caffeine")
    relocate("net.paulem.aihorde4j", "net.paulem.vca.libs.aihorde4j")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

java {
    sourceCompatibility = JavaVersion.toVersion(targetJavaVersion)
    targetCompatibility = JavaVersion.toVersion(targetJavaVersion)
    if (JavaVersion.current() < JavaVersion.toVersion(targetJavaVersion)) {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"

    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    failOnNoDiscoveredTests = false
    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

dependencies {
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")

    compileOnly("org.jetbrains:annotations:26.0.2-1")

    implementation("com.github.Anon8281:UniversalScheduler:0.+")
    implementation("com.jeff-media:custom-block-data:2.2.5")

    implementation("net.paulem.aihorde4j:AiHorde4J:1.0.2")

    implementation("com.github.ben-manes.caffeine:caffeine:3.2.3")
    implementation("org.apache.commons:commons-lang3:3.20.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // MC libraries
    compileOnly("io.netty:netty-all:4.2.9.Final")
    compileOnlyApi("com.mojang:datafixerupper:9.1.20")

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
}

tasks.shadowJar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    minimize {
        exclude(dependency("com.github.ben-manes.caffeine:caffeine"))
    }
}

tasks.compileJava {
    subprojects.forEach {
        dependsOn(it.tasks.build)
    }
}

tasks {
    runServer {
        minecraftVersion("1.21.11")
    }
}

tasks.withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
    javaLauncher = javaToolchains.launcherFor {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(21)
    }
    jvmArgs("-XX:+AllowEnhancedClassRedefinition")
}

paperweight {
    javaLauncher = javaToolchains.launcherFor {
        languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}