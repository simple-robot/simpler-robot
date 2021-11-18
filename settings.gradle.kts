pluginManagement {
    repositories {
        mavenCentral()
        maven { setUrl("https://plugins.gradle.org/m2/") }
    }
}
rootProject.name = "simply-robot"

include(":api")
include(":annotation")
include(":commons:utils")
