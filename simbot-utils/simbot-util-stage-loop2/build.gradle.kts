import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinTargetPreset

plugins {
    kotlin("multiplatform") version "1.7.21"
    //`simbot-simple-project-setup`
    //`simbot-multiplatform-maven-publish`
    //id("simbot.dokka-module-configuration")
}

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    explicitApi()
    
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
            //kotlinOptions.javaParameters = true
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    
    js(IR) {
        browser()
        nodejs()
    }
    
    // fun KotlinTargetPreset<*>.isNative(): Boolean {
    //     return this is org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeTargetPreset
    // }
    //
    // val supports = setOf(
    //     "linuxx64",
    //     "iosarm64",
    //     "iosarm32",
    //     "iosx64",
    //     "macosx64",
    //     "mingwx64",
    //     "tvosarm64",
    //     "tvosx64",
    //     "watchosarm32",
    //     "watchosarm64",
    //     "watchosx86",
    //     "watchosx64",
    //     "iossimulatorarm64",
    //     "watchossimulatorarm64",
    //     "tvossimulatorarm64",
    //     "macosarm64",
    // )
    //
    // val mainPresets = mutableSetOf<KotlinSourceSet>()
    // val testPresets = mutableSetOf<KotlinSourceSet>()
    //
    // targets {
    //     presets.filter { it.isNative() }
    //         .filter { it.name.toLowerCase() in supports }
    //         .forEach { presets ->
    //             val target = fromPreset(presets, presets.name)
    //             mainPresets.add(target.compilations["main"].kotlinSourceSets.first())
    //             testPresets.add(target.compilations["test"].kotlinSourceSets.first())
    //         }
    // }
    
    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation(kotlin("stdlib"))
            }
        }
        val commonTest by getting  {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
            }
        }
        getByName("jvmTest") {
            dependencies {
                implementation(kotlin("test-junit5"))
            }
        }
    
        // val nativeMain by creating {
        //     dependsOn(commonMain)
        // }
        // val nativeTest by creating {
        //     dependsOn(commonTest)
        // }
        //
        // configure(mainPresets) { dependsOn(nativeMain) }
        // configure(testPresets) { dependsOn(nativeTest) }
        
    }
    
}

// kotlin {
//     val supports = setOf(
//         "linuxx64",
//         "iosarm64",
//         "iosarm32",
//         "iosx64",
//         "macosx64",
//         "mingwx64",
//         "tvosarm64",
//         "tvosx64",
//         "watchosarm32",
//         "watchosarm64",
//         "watchosx86",
//         "watchosx64",
//         "iossimulatorarm64",
//         "watchossimulatorarm64",
//         "tvossimulatorarm64",
//         "macosarm64",
//     )
//
//     defaultConfig {
//
//         nativeConfigFilter = {
//             this.name.toLowerCase() in supports
//         }
//         nativeCommonMainName = "nativesMain"
//         nativeCommonTestName = "nativesTest"
//         jsBrowser = true
//         jsNodejs = true
//         jsCompilerType = IR
//         sourceSetsConfig = {
//             commonMain {
//                 dependencies {
//                     implementation(libs.kotlinx.coroutines.core)
//                 }
//             }
//             commonTest {
//                 dependencies {
//                     implementation(kotlin("test-annotations-common"))
//                     implementation(kotlin("test-common"))
//                     implementation(libs.kotlinx.coroutines.test)
//                 }
//             }
//             jvmTest {
//                 dependencies {
//                     implementation(kotlin("test-junit5"))
//                 }
//             }
//             jsTest {
//                 dependencies {
//                     implementation(kotlin("test-js"))
//                 }
//             }
//         }
//     }
//
// }
