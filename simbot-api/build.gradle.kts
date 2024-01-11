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
import love.forte.plugin.suspendtrans.gradle.withKotlinTargets

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
//    @Suppress("OPT_IN_USAGE")
//    wasmJs()
//    @Suppress("OPT_IN_USAGE")
//    wasmWasi()

    withKotlinTargets { target ->
        targets.findByName(target.name)?.compilations?.all {
            // 'expect'/'actual' classes (including interfaces, objects, annotations, enums, and 'actual' typealiases) are in Beta. You can use -Xexpect-actual-classes flag to suppress this warning. Also see: https://youtrack.jetbrains.com/issue/KT-61573
            kotlinOptions.freeCompilerArgs += "-Xexpect-actual-classes"
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                // jvm compile only
                compileOnly(libs.jetbrains.annotations)
                compileOnly(project(":simbot-commons:simbot-common-annotations"))
                api(project(":simbot-commons:simbot-common-suspend-runner"))
                api(project(":simbot-commons:simbot-common-core"))
                api(project(":simbot-commons:simbot-common-collection"))
                api(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.serialization.core)
                // suspend reversal annotations
                compileOnly(libs.suspend.reversal.annotations)
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

        jsMain.dependencies {
            implementation(project(":simbot-commons:simbot-common-annotations"))
            implementation(libs.jetbrains.annotations)
            implementation(libs.suspend.reversal.annotations)
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
    add("kspJvm", libs.suspend.reversal.processor)
}
