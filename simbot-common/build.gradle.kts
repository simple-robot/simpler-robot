import love.forte.gradle.common.kotlin.multiplatform.defaultConfig


plugins {
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
                    //api("org.jetbrains.kotlinx:atomicfu:0.18.4")
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
            jsTest {
                dependencies {
                    implementation(kotlin("test-js"))
                }
            }
        }
    }
}
