plugins {
    kotlin("jvm") //version "1.5.31"
    id("org.jetbrains.dokka") version "1.5.30"
    kotlin("plugin.serialization") version "1.5.31" apply false
}


group = "love.forte"
version = "3.0.0-preview"

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")
}

allprojects {
    println("Project: $this")
}

// buildscript {
//     repositories { mavenCentral() }
//
//     dependencies {
//         val kotlinVersion = "1.5.31"
//         classpath(kotlin("gradle-plugin", version = kotlinVersion))
//         classpath(kotlin("serialization", version = kotlinVersion))
//     }
// }


