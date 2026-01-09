/*
 *     Copyright (c) 2024-2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("love.forte.plugin.suspend-transform")
    alias(libs.plugins.ksp)
    // id("org.jetbrains.dokka")
}

configJavaCompileWithModule("simbot.test")
apply(plugin = "simbot-maven-publish")

kotlin {
    explicitApi()
    applyDefaultHierarchyTemplate()

    @OptIn(ExperimentalAbiValidation::class)
    abiValidation {
        enabled = false
    }

    compilerOptions {
        optIn.addAll(
            "love.forte.simbot.resource.ResourceImplementation",
            "love.forte.simbot.message.OfflineImageImplementation",
        )
    }

    configKotlinJvm(JVMConstants.KT_JVM_TARGET_VALUE)

    js(IR) {
        configJs()
    }

    // tier1
    applyTier123()

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        configWasmJs()
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xexpect-actual-classes"
        )
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.jetbrains.annotations)
                implementation(project(":simbot-commons:simbot-common-annotations"))
                api(project(":simbot-api"))

                // suspend reversal annotations

            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.coroutines.test)
                implementation(kotlin("test"))
                implementation(libs.kotlinx.serialization.json)
            }
        }



        jvmMain {
            dependencies {
                compileOnly(libs.kotlinx.coroutines.reactive)
                compileOnly(libs.kotlinx.coroutines.reactor)
                compileOnly(libs.kotlinx.coroutines.rx2)
                compileOnly(libs.kotlinx.coroutines.rx3)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.kotlinx.coroutines.reactive)
                implementation(libs.kotlinx.coroutines.reactor)
                implementation(libs.kotlinx.coroutines.rx2)
                implementation(libs.kotlinx.coroutines.rx3)
                implementation(libs.ktor.client.core)

                implementation(kotlin("test-junit5"))
                implementation(libs.ktor.client.cio)
            }
        }

        jsTest.dependencies {
            implementation(libs.ktor.client.js)
            implementation(libs.ktor.client.core)
        }
    }
}

// dependencies {
//     add("kspJvm", libs.suspend.reversal.processor)
// }
