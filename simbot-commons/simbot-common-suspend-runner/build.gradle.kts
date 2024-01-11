/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("simbot.dokka-module-configuration")
}

setup(P.SimbotCommon)

configJavaCompileWithModule("simbot.common.suspendrunner")
apply(plugin = "simbot-multiplatform-maven-publish")

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

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        configWasmJs()
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":simbot-logger"))
                compileOnly(project(":simbot-commons:simbot-common-annotations"))
                api(libs.kotlinx.coroutines.core)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        jvmMain.dependencies {
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
            }
        }

        jsMain.dependencies {
            implementation(project(":simbot-commons:simbot-common-annotations"))
        }

        getByName("wasmJsMain").dependencies {
            implementation(project(":simbot-commons:simbot-common-annotations"))
        }
    }
}

configWasmJsTest()
