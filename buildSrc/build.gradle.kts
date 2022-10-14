import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}

val kotlinVersion = "1.7.20"
val dokkaPluginVersion = "1.7.20"
val suspendTransformVersion = "0.0.4"
val gradleCommon = "0.0.1"

dependencies {
    // kotlin("jvm") apply false
    implementation(gradleApi())
    implementation(kotlin("gradle-plugin", kotlinVersion))
    implementation(kotlin("serialization", kotlinVersion))
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaPluginVersion")
    
    // see https://github.com/gradle-nexus/publish-plugin
    implementation("io.github.gradle-nexus:publish-plugin:1.1.0")
    implementation("love.forte.plugin.suspend-transform:suspend-transform-plugin-gradle:$suspendTransformVersion")
    
    implementation("love.forte.gradle.common:gradle-common-core:0.0.1")
    implementation("love.forte.gradle.common:gradle-common-kotlin-multiplatform:0.0.1")
    implementation("love.forte.gradle.common:gradle-common-publication:0.0.1")
}

val compileKotlin: KotlinCompile by tasks

compileKotlin.kotlinOptions {
    freeCompilerArgs = listOf("-Xinline-classes")
}