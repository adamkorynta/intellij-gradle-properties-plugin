plugins {
    kotlin("jvm") version "2.3.0"
    id("org.jetbrains.intellij.platform") version "2.10.5"
}

group = "com.geiconsultants"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create("IC", "2024.3")
    }
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    patchPluginXml {
        sinceBuild.set("243")
        untilBuild.set("253.*")
    }
}