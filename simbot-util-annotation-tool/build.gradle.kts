plugins {
    kotlin("jvm")
    id("simbot.base-module-conventions")
    id("simbot.util-module-conventions")
    `simbot-jvm-maven-publish`
}

repositories {
    mavenCentral()
}


dependencies {
    api(kotlin("reflect"))
}
