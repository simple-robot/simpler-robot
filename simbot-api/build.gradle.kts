import love.forte.gradle.common.kotlin.multiplatform.defaultConfig

plugins {
    kotlin("multiplatform")
    `simbot-simple-project-setup`
    //`simbot-multiplatform-maven-publish`
    id("simbot.dokka-module-configuration")
}

kotlin {
    defaultConfig {
        nativeCommonMainName = "nativesMain"
        nativeCommonTestName = "nativesTest"
        sourceSetsConfig = {
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
            // nativeCommonMain {
            //     dependencies {
            //         implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
            //     }
            // }
        }
    }
}
