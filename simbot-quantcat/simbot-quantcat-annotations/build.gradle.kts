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
import love.forte.plugin.suspendtrans.gradle.withKotlinTargets

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

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("simbot.dokka-module-configuration")
}

setup(P.SimbotQuantcat)

configJavaCompileWithModule("simbot.quantcat.annotations")
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

    withKotlinTargets { target ->
        targets.findByName(target.name)?.compilations?.all {
            // 'expect'/'actual' classes (including interfaces, objects, annotations, enums, and 'actual' typealiases) are in Beta. You can use -Xexpect-actual-classes flag to suppress this warning. Also see: https://youtrack.jetbrains.com/issue/KT-61573
            kotlinOptions.freeCompilerArgs += "-Xexpect-actual-classes"
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                compileOnly(project(":simbot-api"))
                // compileOnly(libs.suspend.reversal.annotations)
                compileOnly(project(":simbot-commons:simbot-common-annotations"))
                compileOnly(project(":simbot-quantcat:simbot-quantcat-common"))
            }
        }

        commonTest {
            dependencies {
                implementation(project(":simbot-api"))
                implementation(project(":simbot-commons:simbot-common-annotations"))
                implementation(project(":simbot-quantcat:simbot-quantcat-common"))
                implementation(kotlin("test"))
                implementation(kotlin("reflect"))
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test-junit5"))
            }
        }

        jsMain.dependencies {
            implementation(project(":simbot-api"))
            // implementation(libs.suspend.reversal.annotations)
            implementation(project(":simbot-commons:simbot-common-annotations"))
            implementation(project(":simbot-quantcat:simbot-quantcat-common"))
        }

        jsMain.dependencies {
            api(project(":simbot-api"))
            // api(libs.suspend.reversal.annotations)
            api(project(":simbot-commons:simbot-common-annotations"))
            api(project(":simbot-quantcat:simbot-quantcat-common"))
        }
    }

}
