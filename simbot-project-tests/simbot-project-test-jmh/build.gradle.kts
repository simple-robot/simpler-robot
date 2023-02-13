plugins {
    id("simbot.test-module-conventions")
    kotlin("kapt")
}

dependencies {
    implementation(libs.openjdk.jmh.core)
    annotationProcessor(libs.openjdk.jmh.generator.annprocess)
    kapt(libs.openjdk.jmh.generator.annprocess)
}