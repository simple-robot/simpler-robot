import love.forte.gradle.common.kotlin.multiplatform.defaultConfig

plugins {
    `simbot-simple-project-setup`
    `simbot-multiplatform-maven-publish`
    id("simbot.dokka-module-configuration")
}

kotlin {
    defaultConfig {
        nativeCommonMainName = "nativesMain"
        nativeCommonTestName = "nativesTest"
        sourceSetsConfig = {
            commonTest {
                dependencies {
                    implementation(kotlin("test"))
                }
            }
            jvmMain {
                dependencies {
                    api(libs.slf4j.api)
                }
            }
            jvmTest {
                dependencies {
                    implementation(kotlin("test-junit5"))
                }
            }
        }
    }
}
