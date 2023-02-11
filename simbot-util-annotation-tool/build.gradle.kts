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
