plugins {
    kotlin("jvm")
    id("simbot.util-module-conventions")
    `simbot-jvm-maven-publish`
    id("simbot.dokka-module-configuration")
}

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    sourceSets.all {
        languageSettings {
            optIn("kotlin.RequiresOptIn")
        }
    }
    
    
}

dependencies {
    api(kotlin("reflect"))
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        javaParameters = true
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + listOf("-Xjvm-default=all")
    }
}
