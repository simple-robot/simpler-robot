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

import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    id("simbot.kotlin-multiplatform")
    id("simbot.kotlin-multiplatform-abi-convention")
    id("org.jetbrains.dokka")
}

configJavaCompileWithModule("simbot.common.suspendrunner")
apply(plugin = "simbot-maven-publish")

kotlin {
    @OptIn(ExperimentalAbiValidation::class)
    abiValidation {
        filters.exclude.byNames.addAll(
            "love.forte.simbot.suspendrunner.SuspendMarker",
            "love.forte.simbot.suspendrunner.SuspendMarker.Container",
            "love.forte.simbot.suspendrunner.SuspendMarker\$Container",
        )
    }

    configKotlinJvm(JVMConstants.KT_JVM_TARGET_VALUE)

    js(IR) {
        configJs()
    }

    applyTier123()

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        configWasmJs()
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":simbot-logger"))

                api(project(":simbot-commons:simbot-common-annotations"))
                api(libs.kotlinx.coroutines.core)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        jvmMain.dependencies {
            api(libs.kotlinx.coroutines.reactive)
            compileOnly(libs.kotlinx.coroutines.reactor)
            compileOnly(libs.kotlinx.coroutines.rx2)
            compileOnly(libs.kotlinx.coroutines.rx3)
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test-junit5"))
                implementation(libs.kotlinx.coroutines.reactor)
                implementation(libs.kotlinx.coroutines.rx2)
                implementation(libs.kotlinx.coroutines.rx3)
                implementation(libs.reactor.test)
            }
        }
    }
}

