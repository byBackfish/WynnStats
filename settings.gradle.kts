rootProject.name = "WynnStats"

pluginManagement {
    repositories {
        maven(url = uri("https://maven.minecraftforge.net/"))
        mavenCentral()
        maven (url = uri("https://jitpack.io") )
        maven {
            name = "sonatype"
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
        }
        gradlePluginPortal()
    }
    plugins {
        id("net.minecraftforge.gradle")
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "net.minecraftforge.gradle") useModule("net.minecraftforge.gradle:ForgeGradle:${extra["forgeGradleVersion"]}")
            else if (requested.id.id == "com.vanniktech.maven.publish") useModule("com.vanniktech:gradle-maven-publish-plugin:latest.release")
        }
    }
}