import love.forte.gradle.common.kotlin.multiplatform.defaultConfig


plugins {
    kotlin("plugin.serialization")
    kotlin("multiplatform")
    `simbot-simple-project-setup`
    //`simbot-multiplatform-maven-publish`
    id("simbot.dokka-module-configuration")
    id("kotlinx-atomicfu")
}

kotlin {
    defaultConfig {
        nativeCommonMainName = "nativeMain"
        nativeCommonTestName = "nativeTest"
        sourceSetsConfig = {
            commonMain {
                dependencies {
                    api(libs.kotlinx.serialization.core)
                    api("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                }
            }
            commonTest {
                dependencies {
                    // implementation(kotlin("test"))
                    implementation(kotlin("test-annotations-common"))
                    implementation(kotlin("test-common"))
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
            jsMain {
                dependencies {
                    api("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                }
            }
            jsTest {
                dependencies {
                    implementation(kotlin("test-js"))
                }
            }
            nativeCommonMain {
                dependencies {
                    api("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                }
            }
        }
    }
}
