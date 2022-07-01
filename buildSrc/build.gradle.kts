plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

val kotlinVersion = "1.6.21"
val dokkaVersion = "1.6.21"

dependencies {
    // kotlin("jvm") apply false
    implementation(gradleApi())
    implementation(kotlin("gradle-plugin", kotlinVersion))
    implementation(kotlin("serialization", kotlinVersion))
    implementation("org.jetbrains.dokka", "dokka-gradle-plugin", dokkaVersion)
    
    // see https://github.com/gradle-nexus/publish-plugin
    implementation("io.github.gradle-nexus:publish-plugin:1.1.0")
}