plugins {
    kotlin("jvm") //version "1.5.31"
    id("org.jetbrains.dokka") version "1.5.30"
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

}

fun Project.src(base: String) = File(projectDir, "src/$base")
inline val File.main get() = File(this, "main/kotlin")
inline val File.test get() = File(this, "test/kotlin")



