/*
 *     Copyright (c) 2024. ForteScarlet.
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

import love.forte.gradle.common.core.project.setup
import love.forte.gradle.common.kotlin.multiplatform.applyTier1
import love.forte.gradle.common.kotlin.multiplatform.applyTier2
import love.forte.gradle.common.kotlin.multiplatform.applyTier3
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
//    id("io.gitlab.arturbosch.detekt")
    id("simbot.suspend-transform-configure")
    alias(libs.plugins.ksp)
    id("simbot.dokka-module-configuration")
}
// apply(plugin = "simbot.dokka-module-configuration")

setup(P.Simbot)

configJavaCompileWithModule("simbot.api")
apply(plugin = "simbot-multiplatform-maven-publish")

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    explicitApi()
    applyDefaultHierarchyTemplate()

    configKotlinJvm(JVMConstants.KT_JVM_TARGET_VALUE)

    js(IR) {
        configJs()
    }

    applyTier1()
    applyTier2()
    applyTier3()

    // wasm?
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        configWasmJs()
    }

//    wasmWasi()

    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xexpect-actual-classes"
        )
    }

    sourceSets {
        commonMain {
            dependencies {
                // jvm compile only
                api(libs.jetbrains.annotations)
                api(project(":simbot-commons:simbot-common-annotations"))
                api(libs.kotlinx.serialization.json)
                api(project(":simbot-commons:simbot-common-suspend-runner"))
                api(project(":simbot-commons:simbot-common-core"))
                api(project(":simbot-commons:simbot-common-collection"))
                api(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.serialization.core)
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
                compileOnly(libs.jetbrains.annotations)
                compileOnly(libs.kotlinx.serialization.json)
                compileOnly(project(":simbot-commons:simbot-common-annotations"))

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
                implementation(kotlin("reflect"))
                implementation(libs.ktor.client.cio)
            }
        }

        jsTest.dependencies {
            implementation(libs.ktor.client.js)
            implementation(libs.ktor.client.core)
        }

        nativeTest.dependencies {
            // implementation(libs.ktor.client.core)
            // implementation(libs.ktor.client.cio)
        }

        linuxTest.dependencies {
            // implementation(libs.ktor.client.core)
            // implementation(libs.ktor.client.cio)
        }

        appleTest.dependencies {
            // implementation(libs.ktor.client.core)
            // implementation(libs.ktor.client.cio)
        }

        mingwTest.dependencies {
            // implementation(libs.ktor.client.core)
            // implementation(libs.ktor.client.winhttp)
        }

    }
}

dependencies {
    // add("kspJvm", libs.suspend.reversal.processor)
    add("kspJvm", project(":internal-processors:interface-uml-processor"))
}

ksp {
    // arg("simbot.internal.processor.uml.enable", (!isCi).toString())
    arg("simbot.internal.processor.uml.enable", "false")
    arg("simbot.internal.processor.uml.target", "love.forte.simbot.event.Event")
    // arg("simbot.internal.processor.uml.target", "love.forte.simbot.definition.Actor")
    arg("simbot.internal.processor.uml.output", rootDir.resolve("generated-docs/event-uml.md").absolutePath)
    // arg("simbot.internal.processor.uml.output", rootDir.resolve("generated-docs/actor-uml.md").absolutePath)
}
