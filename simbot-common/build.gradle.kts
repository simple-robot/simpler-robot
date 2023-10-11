/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

plugins {
    `java-library`
//    id("simbot.simple-module-conventions")
    kotlin("multiplatform")
    kotlin("plugin.serialization")
//    `simbot-multiplatform-maven-publish`
    id("simbot.dokka-module-configuration")
}

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
                javaParameters = true
                freeCompilerArgs = freeCompilerArgs + listOf("-Xjvm-default=all")
            }
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

    val mainPresets = mutableSetOf<KotlinSourceSet>()
    val testPresets = mutableSetOf<KotlinSourceSet>()

    targets {
        presets.filterIsInstance<org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeTargetPreset<*>>()
            .forEach { presets ->
                val target = fromPreset(presets, presets.name)
                mainPresets.add(target.compilations["main"].kotlinSourceSets.first())
                testPresets.add(target.compilations["test"].kotlinSourceSets.first())
            }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                compileOnly(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.serialization.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.serialization.json)
            }
        }

        getByName("jvmTest") {
            dependencies {
                implementation(kotlin("test-junit5"))
            }
        }

        val nativeMain by creating {
            dependsOn(commonMain)
        }
        val nativeTest by creating {
            dependsOn(commonTest)
        }

        configure(mainPresets) { dependsOn(nativeMain) }
        configure(testPresets) { dependsOn(nativeTest) }

    }

}

tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
    options.encoding = "UTF-8"
}
