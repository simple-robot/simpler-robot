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

plugins {
    id("simbot.kotlin-multiplatform")
    id("simbot.kotlin-multiplatform-abi-convention")
    kotlin("plugin.serialization")
    alias(libs.plugins.dokka)
    id("simbot-maven-publish")
}

configJavaCompileWithModule("simbot.common.collection")

kotlin {
    configKotlinJvm(JVMConstants.KT_JVM_TARGET_VALUE)

    js {
        configJs()
    }

    applyTier123()

    // wasm?
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
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        jvmMain.dependencies {
            compileOnly(libs.kotlinx.coroutines.core)
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test-junit5"))
                implementation(libs.kotlinx.lincheck)
            }
        }
    }
}



// https://book.kotlincn.net/text/testing-strategies.html
tasks.withType<Test> {
    jvmArgs(
        "--add-opens",
        "java.base/jdk.internal.misc=ALL-UNNAMED",
        "--add-exports",
        "java.base/jdk.internal.util=ALL-UNNAMED",
        "--add-exports",
        "java.base/sun.security.action=ALL-UNNAMED"
    )
}
