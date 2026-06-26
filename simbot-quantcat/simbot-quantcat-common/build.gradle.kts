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

import love.forte.gradle.common.core.project.setup
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    id("simbot.kotlin-multiplatform")
    id("simbot.kotlin-multiplatform-abi-convention")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka")
    `simbot-maven-publish`
}

setup(P.SimbotQuantcat)

configJavaCompileWithModule("simbot.quantcat.common")

kotlin {
    configKotlinJvm(JVMConstants.KT_JVM_TARGET_VALUE)

    js {
        configJs()
    }

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
                api(project(":simbot-commons:simbot-common-annotations"))
                api(project(":simbot-api"))
                api(project(":simbot-logger"))

            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("reflect"))
            }
        }

        jvmMain.dependencies {
            compileOnly(kotlin("reflect"))
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test-junit5"))
            }
        }
    }

}

