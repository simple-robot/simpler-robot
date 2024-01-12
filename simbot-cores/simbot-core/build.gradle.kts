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
import love.forte.plugin.suspendtrans.gradle.SuspendTransformGradleExtension
import love.forte.plugin.suspendtrans.gradle.withKotlinTargets

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
//    id("io.gitlab.arturbosch.detekt")
    id("simbot.dokka-module-configuration")
}

setup(P.Simbot)

configJavaCompileWithModule("simbot.core")
apply(plugin = "simbot-multiplatform-maven-publish")

kotlin {
    explicitApi()
    applyDefaultHierarchyTemplate()

    configKotlinJvm(JVMConstants.KT_JVM_TARGET_VALUE)

    js(IR) {
        configJs()
    }

    // tier1
    linuxX64()
    macosX64()
    macosArm64()
    iosSimulatorArm64()
    iosX64()

    // tier2
    linuxArm64()
    watchosSimulatorArm64()
    watchosX64()
    watchosArm32()
    watchosArm64()
    tvosSimulatorArm64()
    tvosX64()
    tvosArm64()
    iosArm64()

    // tier3
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()
    mingwX64()
    watchosDeviceArm64()

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
                compileOnly(project(":simbot-commons:simbot-common-annotations"))
                compileOnly(project(":simbot-commons:simbot-common-collection"))
                compileOnly(libs.suspend.reversal.annotations)
                api(project(":simbot-api"))
                api(libs.kotlinx.coroutines.core)
                // api(libs.kotlinx.serialization.core)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.kotlinx.serialization.json)
            }
        }

        jvmTest {
            dependencies {
                implementation(project(":simbot-api"))
                implementation(kotlin("test-junit5"))
            }
        }

        jsMain.dependencies {
            api(project(":simbot-commons:simbot-common-annotations"))
            api(project(":simbot-commons:simbot-common-collection"))
            api("love.forte.plugin.suspend-transform:suspend-transform-annotation:${SuspendTransformGradleExtension().annotationDependencyVersion}")
        }

        nativeMain.dependencies {
            api(project(":simbot-commons:simbot-common-annotations"))
            api(project(":simbot-commons:simbot-common-collection"))
            api(libs.suspend.reversal.annotations)
        }
    }
}
