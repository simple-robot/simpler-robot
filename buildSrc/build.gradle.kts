plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(gradleApi())
    // api("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.20.0")
}