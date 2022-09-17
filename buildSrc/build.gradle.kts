import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

val kotlinVersion = "1.7.10"
val dokkaPluginVersion = "1.7.10"

dependencies {
    // kotlin("jvm") apply false
    implementation(gradleApi())
    implementation(kotlin("gradle-plugin", kotlinVersion))
    implementation(kotlin("serialization", kotlinVersion))
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaPluginVersion")
    
    // see https://github.com/gradle-nexus/publish-plugin
    implementation("io.github.gradle-nexus:publish-plugin:1.1.0")
    implementation("love.forte.plugin.suspend-transform:suspend-transform-plugin-gradle:0.0.1")
}

val compileKotlin: KotlinCompile by tasks

compileKotlin.kotlinOptions {
    freeCompilerArgs = listOf("-Xinline-classes")
}