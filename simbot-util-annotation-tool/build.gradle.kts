plugins {
    id("simbot.base-module-conventions")
    id("simbot.util-module-conventions")
    id("simbot.dokka-module-configuration")
    `simbot-jvm-maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    api(kotlin("reflect"))
}
