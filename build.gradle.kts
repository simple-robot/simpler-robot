val kotlinVersion = "1.6.0"

plugins {
    `kotlin-dsl`
    // kotlin("gradle-plugin") version "1.5.31"
    // kotlin version "1.6.0"
    // kotlin("jvm") version "1.6.0" apply false
    // kotlin("multiplatform") version "1.6.0" apply false
    // kotlin("jvm") version "1.6.0" apply false
    id("org.jetbrains.dokka") version "1.5.30" apply false
    kotlin("plugin.serialization") version "1.6.0" apply false
}

println()

extra.properties.forEach { (t, u) ->
    println("ext.$t\t=\t$u")
}

println()

group = "love.forte.simple-robot"
version = "3.0.0-preview"

repositories {
    mavenCentral()
}

subprojects {

}







