plugins {
    kotlin("jvm") version "2.3.0"
    id("org.jetbrains.intellij.platform") version "2.10.5"
}

group = "com.geiconsultants"
version = "1.0.0-beta1"

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

    publishPlugin {
        val versionStr = project.version.toString()
        // Regex to find letters after a hyphen (e.g., 'beta' from '1.0.0-beta1')
        val channelMatch = Regex("-([a-z]+)", RegexOption.IGNORE_CASE).find(versionStr)
        if (channelMatch != null) {
            val channelName = channelMatch.groupValues[1].lowercase()
            channels.set(listOf(channelName))
        } else {
            channels.set(listOf("default"))
        }
    }
}
